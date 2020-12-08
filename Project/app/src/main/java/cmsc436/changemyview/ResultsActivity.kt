package cmsc436.changemyview

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ResultsActivity : AppCompatActivity() {

    private lateinit var initialScoreFrag: ScoreFragment
    private lateinit var finalScoreFrag: ScoreFragment
    private lateinit var averageInitialScoreFrag: ScoreFragment
    private lateinit var averageFinalScoreFrag: ScoreFragment

    private val fragManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.results)

        initialScoreFrag = supportFragmentManager.findFragmentById(R.id.results_initial_score_fragment) as ScoreFragment
        finalScoreFrag = supportFragmentManager.findFragmentById(R.id.results_final_score_fragment) as ScoreFragment
        averageInitialScoreFrag = supportFragmentManager.findFragmentById(R.id.results_average_initial_score_fragment) as ScoreFragment
        averageFinalScoreFrag = supportFragmentManager.findFragmentById(R.id.results_average_final_score_fragment) as ScoreFragment

        val currentUser = FirebaseAuth.getInstance().currentUser
        val debateID = intent.getStringExtra(Database.DEBATE_ID)

        // Set the title
        val titleLabel = findViewById<TextView>(R.id.results_debate_title)
        Database.debates.child(debateID!!).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(DebateTopic::class.java)
                titleLabel.text = data?.title
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Fetch and update the user's initial and final scores
        Database.users.child(currentUser!!.uid).child(Database.DEBATES).child(debateID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children) {
                    if(data.key == Database.INITIAL_SCORE) {
                        val initialScore = data.getValue(Score::class.java)!!
                        initialScoreFrag.updateScore(initialScore.left, initialScore.right)
                    }

                    else if(data.key == Database.FINAL_SCORE) {
                        val finalScore = data.getValue(Score::class.java)!!
                        finalScoreFrag.updateScore(finalScore.left, finalScore.right)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Fetch and update the debate topic's average initial score
        Database.averageScores.child(debateID).child(Database.INITIAL_SCORE).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val averageScore = snapshot.getValue(Score::class.java)!!
                averageInitialScoreFrag.updateScore(averageScore.left, averageScore.right)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Continually update the debate topic's average final score
        Database.averageScores.child(debateID).child(Database.FINAL_SCORE).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val averageScore = snapshot.getValue(Score::class.java)!!
                averageFinalScoreFrag.updateScore(averageScore.left, averageScore.right)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Always go back to the home page after seeing results
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
    }

    companion object {
        const val TAG = "CMV-ResultsActivity"
    }

}

