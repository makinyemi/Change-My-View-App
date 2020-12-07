package cmsc436.changemyview

import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class TopicQuestionFragment : Fragment(R.layout.topic_question_fragment) {

    private var value: Int = 2

    fun updateQuestion(text: String) {
        // Hopefully catch any errors before a crash due to the below non-null assertion
        if(view != null) {
            val title = view!!.findViewById<TextView>(R.id.topic_question_title)
            val indicator = view!!.findViewById<TextView>(R.id.topic_question_indicator)
            val input = view!!.findViewById<SeekBar>(R.id.topic_question_input)

            title.text = text

            input.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    when (progress) {
                        0 -> indicator.text = getString(R.string.topic_question_option_strongly_disagree)
                        1 -> indicator.text = getString(R.string.topic_question_option_disagree)
                        2 -> indicator.text = getString(R.string.topic_question_option_neutral)
                        3 -> indicator.text = getString(R.string.topic_question_option_agree)
                        4 -> indicator.text = getString(R.string.topic_question_option_strongly_agree)
                    }

                    value = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    fun getResult(): Int {
        // Scores go from [-2, 2]
        // -2 being completely left and 2 being completely right
        return value - 2
    }

    companion object {
        const val TAG = "CMV-TopicQuestionFragment"
    }

}