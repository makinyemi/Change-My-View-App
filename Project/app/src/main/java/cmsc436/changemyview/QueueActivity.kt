package cmsc436.changemyview

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class QueueActivity: AppCompatActivity() {

    private lateinit var uid: String
    private lateinit var debateID: String

    private lateinit var currentParticipants: TextView
    private lateinit var minParticipants: TextView
    private lateinit var maxParticipants: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.queue)

        uid = FirebaseAuth.getInstance().currentUser!!.uid
        debateID = intent.getStringExtra(Database.DEBATE_ID)!!

        val scoreFragment = supportFragmentManager.findFragmentById(R.id.queue_score_fragment) as ScoreFragment

        // Fetch and update the user's initial score
        Database.users.child(uid).child(Database.DEBATES).child(debateID).child(Database.INITIAL_SCORE).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(Score::class.java)
                if(data != null) {
                    scoreFragment.updateScore(data.left, data.right)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        currentParticipants = findViewById(R.id.queue_current_participants)
        minParticipants = findViewById(R.id.queue_min_participants)
        maxParticipants = findViewById(R.id.queue_max_participants)

        // Continually update the numerical presentation of the waiting area
        Database.queue.child(debateID).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(QueueData::class.java)
                if(data != null) {
                    currentParticipants.text = data.current.toString()
                    minParticipants.text = data.min.toString()
                    maxParticipants.text = data.max.toString()

                    if(data.current == data.min) {
                        // Start the debate
                        // TODO
                        Log.i(TAG, "Start the debate!")
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    companion object {
        const val TAG = "CMV-QueueActivity"
    }

}