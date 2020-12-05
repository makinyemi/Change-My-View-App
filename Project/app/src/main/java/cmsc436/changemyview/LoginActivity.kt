package cmsc436.changemyview

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
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

    private var showPassword = false
    private lateinit var mPasswordVisibility: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mUsername = findViewById(R.id.login_edit_username)
        mPassword = findViewById(R.id.login_edit_password)
        mSignUp = findViewById(R.id.login_register)
        mProgress = findViewById(R.id.login_progress)
        mLogin = findViewById(R.id.login_btn_login)

        // Allow the user to make their password visible
        mPasswordVisibility = findViewById(R.id.login_password_visibility)
        mPasswordVisibility.setOnClickListener {
            // Flop-flop the value
            showPassword = !showPassword

            if(showPassword) {
                mPasswordVisibility.setImageResource(R.drawable.ic_baseline_visibility_24)

                // Show text
                mPassword.transformationMethod = null
            } else {
                mPasswordVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24)

                // Set text to dots
                mPassword.transformationMethod = PasswordTransformationMethod()
            }
        }

        // Make sure all inputs are enabled
        enable()

        // Give the sign up text functionality
        mSignUp.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        mLogin.setOnClickListener {
            login()
        }
    }

    /**
     * Logs a user in with the given credentials.
     * This does not validate any data!
     * Data validation is done in registration to ensure the
     * database contains the correct information.
     * Providing malformed data in the login will only result
     * in a failed login attempt and not affect the database in a
     * greater way.  Thus, aside from rare user frustration from
     * inputting incorrect data, there is no real point in adding
     * input validation in the login form.
     */
    private fun login() {
        // Disable inputs until login has succeeded or failed
        disable()

        val email = mUsername.text?.toString()
        val password = mPassword.text?.toString()

        // Ensure non-empty form data
        if(email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_input), Toast.LENGTH_LONG).show()
            enable()
            return
        }

        // Attempt to sign the user in
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            // Re-enable inputs
            enable()
            clear()

            // Successful login
            if(task.isSuccessful) {
                Toast.makeText(this, getString(R.string.successful_login), Toast.LENGTH_LONG).show()

                // Start the home activity
                val homeIntent = Intent(this, MainActivity::class.java)
                startActivity(homeIntent)
            }

            // Failed login
            else {
                Toast.makeText(this, getString(R.string.failed_login), Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Enable all inputs
     */
    private fun enable() {
        mProgress.visibility = View.GONE
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
    }
}