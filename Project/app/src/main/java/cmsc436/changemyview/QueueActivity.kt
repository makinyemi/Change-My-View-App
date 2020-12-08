package cmsc436.changemyview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class QueueActivity: AppCompatActivity() {

    private lateinit var uid: String
    private lateinit var debateID: String

    private lateinit var side: String
    private lateinit var queueData: QueueData

    private lateinit var debateTitle: TextView
    private lateinit var sideIndicator: TextView
    private lateinit var againstParticipants: TextView
    private lateinit var forParticipants: TextView
    private lateinit var btnLaunch: Button
    private var started: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.queue)

        uid = FirebaseAuth.getInstance().currentUser!!.uid
        debateID = intent.getStringExtra(Database.DEBATE_ID)!!

        val scoreFragment = supportFragmentManager.findFragmentById(R.id.queue_score_fragment) as ScoreFragment

        // Fetch and update the user's initial score and side
        Database.users.child(uid).child(Database.DEBATES).child(debateID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children) {
                    // Get initial score
                    if (data.key == Database.INITIAL_SCORE) {
                        val score = data.getValue(Score::class.java)!!
                        scoreFragment.updateScore(score.left, score.right)
                    }

                    // Get side
                    else if (data.key == Database.SIDE) {
                        side = data.getValue(String::class.java)!!

                        // Set side indicator
                        if(side == Database.LEFT) {
                            sideIndicator.text = getString(R.string.queue_against)
                        }else if(side == Database.RIGHT) {
                            sideIndicator.text = getString(R.string.queue_for)
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        debateTitle = findViewById(R.id.queue_debate_title)
        sideIndicator = findViewById(R.id.queue_side_indicator)
        againstParticipants = findViewById(R.id.queue_current_against)
        forParticipants = findViewById(R.id.queue_current_for)
        btnLaunch = findViewById(R.id.queue_btn_launch)

        // Set the debate title
        Database.debates.child(debateID).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(DebateTopic::class.java)
                if(data != null) {
                    debateTitle.text = data.title
                    started = !data.startTime.isNullOrEmpty()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Continually update the numerical presentation of the waiting area
        Database.queue.child(debateID).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(QueueData::class.java)
                if(data != null) {
                    queueData = data

                    // Update screen text
                    againstParticipants.text = data.left.toString()
                    forParticipants.text = data.right.toString()

                    // Check if we should start the debate
                    // Only if there is at least one user on each side
                    if(data.left >= 1 && data.right >= 1) {
                        btnLaunch.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        btnLaunch.setOnClickListener {
            if(!started) {
                Database.startDebate(debateID)
            }

            val chatIntent = Intent(this, chat_activity::class.java)
            chatIntent.putExtra(Database.DEBATE_ID, debateID)
            startActivity(chatIntent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Remove user from the queue
        when(side) {
            Database.LEFT -> {
                queueData = QueueData(queueData.max, if(queueData.left > 0) {queueData.left - 1} else {queueData.left}, queueData.right)
                Database.queue.child(debateID).setValue(queueData)
            }
            Database.RIGHT -> {
                queueData = QueueData(queueData.max, queueData.left, if(queueData.right > 0) {queueData.right - 1} else {queueData.right})
                Database.queue.child(debateID).setValue(queueData)
            }
        }

        // Redirect to the home activity
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
    }

    companion object {
        const val TAG = "CMV-QueueActivity"
    }

}