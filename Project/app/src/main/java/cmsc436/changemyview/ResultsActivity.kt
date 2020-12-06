package cmsc436.changemyview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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

        initialScoreFrag = ScoreFragment()
        finalScoreFrag = ScoreFragment()
        averageInitialScoreFrag = ScoreFragment()
        averageFinalScoreFrag = ScoreFragment()

        fragManager.beginTransaction()
            .add(R.id.results_initial_score_fragment, initialScoreFrag)
            .add(R.id.results_final_score_fragment, finalScoreFrag)
            .add(R.id.results_average_initial_score_fragment, averageInitialScoreFrag)
            .add(R.id.results_average_final_score_fragment, averageFinalScoreFrag)
            .commit()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val debateID = intent.getStringExtra(Database.DEBATE_ID)

        // Fetch and update the user's initial and final scores
        database.getReference(Database.USERS).child(currentUser!!.uid).child(debateID!!).addListenerForSingleValueEvent(object: ValueEventListener {
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

        // Fetch and update the debate topic's average score
        database.getReference(Database.AVERAGE_SCORES).child(debateID!!).child(Database.INITIAL_SCORE).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val averageScore = snapshot.getValue(Score::class.java)!!
                averageInitialScoreFrag.updateScore(averageScore.left, averageScore.right)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Continually update the debate topic's average final score
        database.getReference(Database.AVERAGE_SCORES).child(debateID!!).child(Database.FINAL_SCORE).addValueEventListener(object: ValueEventListener {
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

