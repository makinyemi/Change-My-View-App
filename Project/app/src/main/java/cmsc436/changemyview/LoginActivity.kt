package cmsc436.changemyview

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    private lateinit var mUsername: EditText
    private lateinit var mPassword: EditText
    private lateinit var mSignUp: TextView
    private lateinit var mProgress: ProgressBar
    private lateinit var mLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mUsername = findViewById(R.id.login_edit_username)
        mPassword = findViewById(R.id.login_edit_password)
        mSignUp = findViewById(R.id.login_register)
        mProgress = findViewById(R.id.login_progress)
        mLogin = findViewById(R.id.login_btn_login)

        // Make sure all inputs are enabled
        enable()

        mSignUp.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        mLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {// Disable inputs until validation
        disable()

        val email = mUsername.text?.toString()
        val password = mPassword.text?.toString()

        if(email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_input), Toast.LENGTH_LONG).show()
            enable()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            // Successful login
            if(task.isSuccessful) {
                enable()
                clear()
                val homeIntent = Intent(this, MainActivity::class.java)
                homeIntent.putExtra(LOGIN, true)

                Toast.makeText(this, "Login success!", Toast.LENGTH_LONG).show()
                startActivity(homeIntent)
            }

            // Failed login
            else {
                enable()
                clear()
                Toast.makeText(this, getString(R.string.failed_login), Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Enable all inputs
     */
    private fun enable() {
        mProgress.visibility = View.INVISIBLE
        mLogin.isEnabled = true
        mUsername.isEnabled = true
        mPassword.isEnabled = true
        mSignUp.isEnabled = true
    }

    /**
     * Disable all inputs
     */
    private fun disable() {
        mProgress.visibility = View.VISIBLE
        mLogin.isEnabled = false
        mUsername.isEnabled = false
        mPassword.isEnabled = false
        mSignUp.isEnabled = false
    }

    /**
     * Clears all inputs
     */
    private fun clear() {
        mUsername.text.clear()
        mPassword.text.clear()
    }

    companion object {
        const val TAG = "CMV-LoginActivity"
        const val LOGIN = "login-indicator"
    }
}