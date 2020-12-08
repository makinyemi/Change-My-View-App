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

    private var averageInitialScores: Score? = null
    private var averageFinalScores: Score? = null

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

        // Disable input until we can check if the data has already been set
        disable()

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

        // Continually update the average initial and final scores
        Database.averageScores.child(debateID).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children) {
                    if(data.key == Database.INITIAL_SCORE) {
                        averageInitialScores = data.getValue(Score::class.java)
                    }

                    else if(data.key == Database.FINAL_SCORE) {
                        averageFinalScores = data.getValue(Score::class.java)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Check if the user's side has been set for this debate
        Database.users.child(uid).child(Database.DEBATES).child(debateID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var run = false

                for (data in snapshot.children) {
                    run = true
                    Log.i(TAG, "Key: ${data.key}")
                    if(mode == PRE_DEBATE && data.key == Database.INITIAL_SCORE) {
                        // Side has been set, send to the queue activity
                        val queueIntent = Intent(applicationContext, QueueActivity::class.java)
                        queueIntent.putExtra(Database.DEBATE_ID, debateID)
                        startActivity(queueIntent)
                    }else if(mode == POST_DEBATE && data.key == Database.FINAL_SCORE) {
                        // Start results activity
                        val resultsIntent = Intent(applicationContext, ResultsActivity::class.java)
                        resultsIntent.putExtra(Database.DEBATE_ID, debateID)
                        startActivity(resultsIntent)
                    }else {
                        enable()
                    }
                }

                if(!run) {
                    enable()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnSubmit.setOnClickListener {
            disable()
            submitBtn()
        }
    }

    private fun submitBtn() {
        // Calculate score
        val score = calculateScore()

        // BEFORE THE DEBATE
        if(mode == PRE_DEBATE) {
            // Write score to user database
            Database.users
                .child(uid)
                .child(Database.DEBATES)
                .child(debateID)
                .child(Database.INITIAL_SCORE)
                .setValue(score)

            // Write score to average database
            var newAverage = score
            if(averageInitialScores != null) {
                newAverage = Score(averageInitialScores!!.left + score.left, averageInitialScores!!.right + score.right)
            }
            Database.averageScores
                .child(debateID)
                .child(Database.INITIAL_SCORE)
                .setValue(newAverage)

            // Determine user side
            var side = Database.NONE

            if (score.left > score.right)
                side = Database.LEFT
            else if (score.left < score.right)
                side = Database.RIGHT
            else
                side = Database.LEFT

            // Write side to database
            Database.users
                .child(uid)
                .child(Database.DEBATES)
                .child(debateID)
                .child(Database.SIDE)
                .setValue(side)

            // Write participation to database
            Database.users
                .child(uid)
                .child(Database.DEBATES)
                .child(debateID)
                .child(Database.PARTICIPATION)
                .setValue(participation)

            // Go to the waiting queue for enough participants
            val queueIntent = Intent(this, QueueActivity::class.java)
            queueIntent.putExtra(Database.DEBATE_ID, debateID)
            startActivity(queueIntent)
        }

        // AFTER THE DEBATE IS OVER
        else if(mode == POST_DEBATE) {
            // Write score to database
            Database.users
                .child(uid)
                .child(Database.DEBATES)
                .child(debateID)
                .child(Database.FINAL_SCORE)
                .setValue(score)

            // Write score to average database
            var newAverage = score
            if(averageFinalScores != null) {
                newAverage = Score(averageFinalScores!!.left + score.left, averageFinalScores!!.right + score.right)
            }
            Database.averageScores
                .child(debateID)
                .child(Database.FINAL_SCORE)
                .setValue(newAverage)

            // Start results activity
            val resultsIntent = Intent(this, ResultsActivity::class.java)
            resultsIntent.putExtra(Database.DEBATE_ID, debateID)
            startActivity(resultsIntent)
        }
    }

    private fun calculateScore(): Score {
        val q1 = question1Fragment.getResult()
        val q2 = question2Fragment.getResult()
        val q3 = question3Fragment.getResult()
        val q4 = question4Fragment.getResult()
        val q5 = question5Fragment.getResult()

        val score = q1 + q2 + q3 + q4 + q5

        return Score(10 - score, 10 + score)
    }

    private fun enable() {
        btnSubmit.isEnabled = true
        question1Fragment.enable(true)
        question2Fragment.enable(true)
        question3Fragment.enable(true)
        question4Fragment.enable(true)
        question5Fragment.enable(true)
    }

    private fun disable() {
        btnSubmit.isEnabled = false
        question1Fragment.enable(false)
        question2Fragment.enable(false)
        question3Fragment.enable(false)
        question4Fragment.enable(false)
        question5Fragment.enable(false)
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