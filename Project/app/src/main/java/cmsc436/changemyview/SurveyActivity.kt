package cmsc436.changemyview

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SurveyActivity: AppCompatActivity() {

    private lateinit var debateID: String
    private lateinit var mode: String

    private lateinit var title: TextView

    private lateinit var question1Fragment: TopicQuestionFragment
    private lateinit var question2Fragment: TopicQuestionFragment
    private lateinit var question3Fragment: TopicQuestionFragment
    private lateinit var question4Fragment: TopicQuestionFragment
    private lateinit var question5Fragment: TopicQuestionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topic_survey)

        debateID = intent.getStringExtra(Database.DEBATE_ID)!!
        mode = intent.getStringExtra(MODE)!!

        title = findViewById(R.id.topic_survey_title)

        question1Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_1_fragment) as TopicQuestionFragment
        question2Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_2_fragment) as TopicQuestionFragment
        question3Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_3_fragment) as TopicQuestionFragment
        question4Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_4_fragment) as TopicQuestionFragment
        question5Fragment = supportFragmentManager.findFragmentById(R.id.topic_question_5_fragment) as TopicQuestionFragment

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
    }

    companion object {
        const val TAG = "CMV-SurveyActivity"

        const val MODE = "mode"
        const val PRE_DEBATE = "preDebate"
        const val POST_DEBATE = "postDebate"
    }

}