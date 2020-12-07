package cmsc436.changemyview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private lateinit var mLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Handle logout button
        mLogout = findViewById(R.id.home_btn_logout)
        mLogout.setOnClickListener {
            logout()
        }

        findViewById<FloatingActionButton>(R.id.home_btn_profile).setOnClickListener {
            val intent = Intent(this, SurveyActivity::class.java)
            intent.putExtra(Database.DEBATE_ID, "-MNir5jwkTgaSn3rVNqc")
            intent.putExtra(SurveyActivity.MODE, SurveyActivity.PRE_DEBATE)
            startActivity(intent)
        }
    }

    /**
     * Logs the current user out.
     * Asks for confirmation first.
     */
    private fun logout() {
        // Confirm logout
        val logoutAlert = AlertDialog.Builder(this)
        logoutAlert.apply {

            // Logout confirmation
            setPositiveButton(R.string.btn_logout) { _, _ ->
                auth.signOut()
                val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(loginIntent)
            }

            // Do nothing on cancel
            setNegativeButton(R.string.btn_cancel) {_, _ -> }
        }
            .setTitle(R.string.btn_logout)
            .setMessage(R.string.logout_message)
            .create()
        logoutAlert.show()
    }

    override fun onStart() {
        super.onStart()

        // Check if user is logged in
        if(auth.currentUser == null) {
            // No user is signed in, launch the login activity
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        } else {
            // User is logged in, proceed to the home page
            currentUser = auth.currentUser!!
        }
    }

    companion object {
        const val TAG = "CMV-MainActivity"
    }
}