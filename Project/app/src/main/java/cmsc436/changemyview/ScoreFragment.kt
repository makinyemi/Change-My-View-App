package cmsc436.changemyview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

class ScoreFragment(val scoreLeft: Int = 1, val scoreRight: Int = 1) : Fragment(R.layout.score_fragment) {

    private lateinit var left: View
    private lateinit var right: View

    override fun onStart() {
        super.onStart()

        // Set the score bar on start with the given (or default) values
        updateScore(scoreLeft, scoreRight)
    }

    /**
     * Updates the score bar based on the provided left and right scores.
     * This can only be called after this fragment has been displayed due
     * to the view referenced below not existing otherwise!
     */
    fun updateScore(scoreLeft: Int, scoreRight: Int) {
        // Hopefully catch any errors before a crash due to the below non-null assertion
        if(view != null) {
            val bar = view!!.findViewById<ProgressBar>(R.id.score_bar)
            bar.max = scoreLeft + scoreRight
            bar.progress = scoreLeft
        }
    }

    companion object {
        const val TAG = "CMV-ScoreFragment"
    }

}
