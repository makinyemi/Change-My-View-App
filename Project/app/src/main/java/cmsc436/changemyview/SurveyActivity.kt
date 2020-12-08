package cmsc436.changemyview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SurveyActivity: AppCompatActivity() {

    private lateinit var uid: String
    private lateinit var debateID: String
    private lateinit var mode: String
    private lateinit var participation: String

    private lateinit var title: TextView
    private lateinit var btnSubmit: Button

    private lateinit var question1Fragment: TopicQuestionFragment
    private lateinit var question2Fragment: TopicQuestionFragment
    private lateinit var question3Fragment: TopicQuestionFragment
    private lateinit var question4Fragment: TopicQuestionFragment
    private lateinit var question5Fragment: TopicQuestionFragment

    private var max: Int = 0
    private var left: Int = 0
    private var right: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topic_survey)

        uid = FirebaseAuth.getInstance().currentUser!!.uid
        debateID = intent.getStringExtra(Database.DEBATE_ID)!!
        mode = intent.getStringExtra(MODE)!!
        if(intent.hasExtra(PARTICIPATION))
            participation = intent.getStringExtra(PARTICIPATION)!!

        title = findViewById(R.id.topic_survey_title)
        btnSubmit = findViewById(R.id.topic_survey_btn_submit)

        question1Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_1_fragment) as TopicQuestionFragment
        question2Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_2_fragment) as TopicQuestionFragment
        question3Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_3_fragment) as TopicQuestionFragment
        question4Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_4_fragment) as TopicQuestionFragment
        question5Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_5_fragment) as TopicQuestionFragment

        // Fetch the debate topic questions
        Database.debates.child(debateID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(DebateTopic::class.java)
                if(data != null) {
                    title.text = data.title

                    question1Fragment.updateQuestion(data.questions[0])
                    question2Fragment.updateQuestion(data.questions[1])
                    question3Fragment.updateQuestion(data.questions[2])
                    question4Fragment.updateQuestion(data.questions[3])
                    question5Fragment.updateQuestion(data.questions[4])
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Continually update the participants per side
        Database.queue.child(debateID).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(QueueData::class.java)
                if(data != null) {
                    max = data.max
                    left = data.left
                    right = data.right
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        btnSubmit.setOnClickListener {
            submitBtn()
        }
    }

    private fun submitBtn() {
        Log.i(TAG, "Clicked")

        // Calculate score
        val score = calculateScore()
        Log.i(TAG, "Left: ${score.left}, Right: ${score.right}")

        if(mode == PRE_DEBATE) {
            // Write score to database
            Database.users
                .child(uid)
                .child(Database.DEBATES)
                .child(debateID)
                .child(Database.INITIAL_SCORE)
                .setValue(score)

            // Determine user side
            var side = Database.NONE
            val limit = max / 2

            // If debating, make sure there is room on the side
            if(participation == DEBATING) {
                // Check left side
                if (score.left > score.right && left < limit)
                    side = Database.LEFT

                // Check right side
                else if (score.left < score.right && right < limit)
                    side = Database.RIGHT

                // Scored neutral
                else if (score.left == score.right && (left <= limit || right <= limit)) {
                    // Right side has room
                    if(left >= limit && right < limit)
                        side = Database.RIGHT

                    // Left side has room
                    else if(right >= limit && left < limit)
                        side = Database.LEFT

                    // If both sides have room
                    else
                        side = Database.LEFT
                }

                // If the scored side is full, default to an observer.
                // We don't want to allow people who scored on one side
                // to negatively affect the opposing side by placing them
                // on that other side for the debate. (No devils advocate)
                else
                    participation = OBSERVING
            }

            // If observing, set side just based on score
            if(participation == OBSERVING) {
                if (score.left > score.right)
                    side = Database.LEFT
                else if (score.left < score.right)
                    side = Database.RIGHT
            }

            // Write side to database
            Database.users
                .child(uid)
                .child(Database.DEBATES)
                .child(debateID)
                .child(Database.SIDE)
                .setValue(side)

            // Update the queue tracker
            // Side should never be Database.NONE at this point
            if(participation == DEBATING) {
                when (side) {
                    Database.LEFT -> Database.queue.child(debateID).child(Database.LEFT)
                        .setValue(left + 1)
                    Database.RIGHT -> Database.queue.child(debateID).child(Database.RIGHT)
                        .setValue(right + 1)
                }
            }

            // Write participation to database
            Database.users
                .child(uid)
                .child(Database.DEBATES)
                .child(debateID)
                .child(Database.PARTICIPATION)
                .setValue(participation)

            val queueIntent = Intent(this, QueueActivity::class.java)
            queueIntent.putExtra(Database.DEBATE_ID, debateID)
            startActivity(queueIntent)
        }

        else if(mode == POST_DEBATE) {
            // Start results activity
            // TODO
        }
    }

    private fun calculateScore(): Score {
        val q1 = question1Fragment.getResult()
        val q2 = question2Fragment.getResult()
        val q3 = question3Fragment.getResult()
        val q4 = question4Fragment.getResult()
        val q5 = question5Fragment.getResult()

        val score = q1 + q2 + q3 + q4 + q5

        return Score(5 - score, 5 + score)
    }

    companion object {
        const val TAG = "CMV-SurveyActivity"

        const val MODE = "mode"
        const val PRE_DEBATE = "preDebate"
        const val POST_DEBATE = "postDebate"

        const val PARTICIPATION = "participation"
        const val DEBATING = "debating"
        const val OBSERVING = "observing"
    }

}