package cmsc436.changemyview

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    private lateinit var currentUser: String
    private lateinit var mLogout: Button
    private lateinit var debateButton : Button
    private lateinit var observerButton : Button
    private lateinit var profileButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        //Current firebase user logged in
        val currentUser = FirebaseAuth.getInstance().currentUser

        debateButton = findViewById(R.id.home_btn_debate)
        observerButton = findViewById(R.id.home_btn_observe)
        profileButton = findViewById(R.id.home_btn_profile)

        debateButton.setOnClickListener {
            val intent = Intent(this, TopicActivity::class.java)
            //TODO
            intent.putExtra(SurveyActivity.PARTICIPATION,SurveyActivity.DEBATING)
            startActivity(intent)
        }

        observerButton.setOnClickListener{
            val intent = Intent(this, TopicActivity::class.java)
            //TODO
            //Set flag user in database as a observer
            intent.putExtra(SurveyActivity.PARTICIPATION,SurveyActivity.OBSERVING)

            startActivity(intent)
        }


        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Handle logout button
        mLogout = findViewById(R.id.home_btn_logout)
        mLogout.setOnClickListener {
            logout()
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
            currentUser = auth.currentUser.toString()!!
        }
    }

    companion object {
        const val TAG = "CMV-MainActivity"
    }
}