package cmsc436.changemyview

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

class ScoreFragment : Fragment(R.layout.score_fragment) {

    private var lastRatio: Double? = null

    /**
     * Updates the score bar based on the provided left and right scores.
     * This can only be called after this fragment has been displayed due
     * to the view referenced below not existing otherwise!
     */
    fun updateScore(scoreLeft: Int, scoreRight: Int) {
        // Hopefully catch any errors before a crash due to the below non-null assertion
        if(view != null) {
            val bar = view!!.findViewById<ProgressBar>(R.id.score_bar)

            var max = scoreLeft + scoreRight
            val ratio = scoreLeft.toDouble() / max.toDouble()

            // Make sure the progress bar has enough granularity to have a smooth animation
            max *= 100
            bar.max = max
            val newProgress = (max * ratio).toInt()

            // Always start the animation from the last progress point
            // If this is the first update, start from the middle
            val animationStartProgress: Int =
                if(lastRatio != null) {
                    (max.toDouble() * lastRatio!!).toInt()
                } else {
                    max / 2
                }

            // Save last ratio in case of another update
            lastRatio = ratio

            // Setup and start the animation
            val progressAnimation = ValueAnimator.ofInt(animationStartProgress, newProgress)
            progressAnimation.addUpdateListener {
                val value = it.animatedValue as Int
                bar.progress = value
            }
            progressAnimation.interpolator = LinearInterpolator()
            progressAnimation.duration = 1000
            progressAnimation.start()
        }
    }

    companion object {
        const val TAG = "CMV-ScoreFragment"
    }

}
