package cmsc436.changemyview

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    private lateinit var mEmail: EditText
    private lateinit var mUsername: EditText
    private lateinit var mPassword: EditText
    private lateinit var mVerifyPassword: EditText
    private lateinit var mProgress: ProgressBar
    private lateinit var mRegister: Button

    private var showPassword = false
    private lateinit var mPasswordVisibility: ImageButton

    private lateinit var mUserList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        mEmail = findViewById(R.id.register_edit_email)
        mUsername = findViewById(R.id.register_edit_username)
        mPassword = findViewById(R.id.register_edit_password)
        mVerifyPassword = findViewById(R.id.register_edit_password_verify)
        mProgress = findViewById(R.id.register_progress)
        mRegister = findViewById(R.id.register_btn_register)

        // Allow the user to make their password visible
        mPasswordVisibility = findViewById(R.id.register_password_visibility)
        mPasswordVisibility.setOnClickListener {
            // Flop-flop the value
            showPassword = !showPassword

            if(showPassword) {
                mPasswordVisibility.setImageResource(R.drawable.ic_baseline_visibility_24)

                // Show text
                mPassword.transformationMethod = null
                mVerifyPassword.transformationMethod = null
            } else {
                mPasswordVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24)

                // Set text to dots
                mPassword.transformationMethod = PasswordTransformationMethod()
                mVerifyPassword.transformationMethod = PasswordTransformationMethod()
            }
        }

        // Make sure everything is enabled
        enable()

        mRegister.setOnClickListener {
            register()
        }

        // Initialize username list
        mUserList = ArrayList()

        // Update username list with all usernames from the database
        val usersDB = FirebaseDatabase.getInstance().getReference("users")
        usersDB.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUserList.clear() // clear list before repopulating it

                for(u in snapshot.children){
                    val user = u.getValue(UserData::class.java)
                    if(user != null) {
                        mUserList.add(user.username)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    /**
     * Registers a new user.
     * This will validate all input data before completing registration.
     * By validating all information we avoid errors down the line from
     * malformed data being sent from the database.  This also prevents
     * a need to validate input data at the login stage.
     */
    private fun register() {
        // Lock inputs
        disable()

        val email = mEmail.text?.toString()
        val username = mUsername.text?.toString()
        val password = mPassword.text?.toString()
        val passwordValidator = mVerifyPassword.text?.toString()

        // Validate all data
        if(
            validateEmail(email) &&
            validateUsername(username) &&
            validatePassword(password) &&
            validatePassword(passwordValidator)
        ) {
            // Ensure the intended password was given
            if(!password.equals(passwordValidator)) {
                Toast.makeText(this, getString(R.string.register_passwords_mismatch), Toast.LENGTH_LONG).show()
            }else {

                // register user
                auth.createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener { task ->
                        // Re-enable inputs
                        enable()

                        if (task.isSuccessful) {
                            // only clear on success to allow for editing on a failed attempt
                            clear()

                            Toast.makeText(
                                this,
                                getString(R.string.successful_register),
                                Toast.LENGTH_LONG
                            ).show()

                            // Add the user to the database
                            Database.pushUser(task.result!!.user!!.uid, username!!, email)

                            val loginIntent = Intent(this, LoginActivity::class.java)
                            startActivity(loginIntent)
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.failed_register),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
            }
        }

        // Catch-all Re-enable inputs
        enable()
    }

    /**
     * Matches an email address following RFC 5322.
     * The below regular expression was copied from:
     * https://stackoverflow.com/questions/201323/how-to-validate-an-email-address-using-a-regular-expression
     * to save time and correct several errors
     */
    private fun validateEmail(email: String?): Boolean {
        if(email.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.register_no_email), Toast.LENGTH_LONG).show()
            return false
        }

        val emailRegex = Regex(
            "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)" +
                    "*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
                    "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9" +
                    "-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|" +
                    "[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|" +
                    "1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c" +
                    "\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")

        val result = emailRegex.matches(email)
        if(!result) {
            Toast.makeText(this, getString(R.string.register_invalid_email), Toast.LENGTH_LONG).show()
        }

        return result
    }

    /**
     * Validates a username.
     * A valid username is one that is not empty and
     * is not taken by another user.
     */
    private fun validateUsername(username: String?): Boolean {
        if(username.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.register_no_username), Toast.LENGTH_LONG).show()
            return false
        }

        // Check if the username is already in use
        var exists: Boolean = false
        for(user in mUserList) {
            if(user.equals(username, ignoreCase = true)){
                exists = true
                break
            }
        }

        if(exists) {
            Toast.makeText(this, getString(R.string.register_username_exists), Toast.LENGTH_LONG).show()
        }

        return !exists
    }

    /**
     * Validates a password:
     * A password must have at least:
     *  - 1 lowercase letter
     *  - 1 uppercase letter
     *  - 1 number
     * and be at least 8 characters in length
     */
    private fun validatePassword(password: String?): Boolean {
        if(password.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.register_no_password), Toast.LENGTH_LONG).show()
            return false
        }

        var lowerCount = 0
        var upperCount = 0
        var numCount = 0

        password.forEach {
            if(it.isDigit())
                numCount++
            else if(it.isLetter())
                if(it.isLowerCase())
                    lowerCount++
                else
                    upperCount++
        }

        if(password.length >= 8 && lowerCount >= 1 && upperCount >= 1 && numCount >= 1)
            return true

        Toast.makeText(this, getString(R.string.register_password_requirements), Toast.LENGTH_LONG).show()
        return false
    }

    /**
     * Enable all inputs
     */
    private fun enable() {
        mProgress.visibility = View.GONE
        mEmail.isEnabled = true
        mUsername.isEnabled = true
        mPassword.isEnabled = true
        mVerifyPassword.isEnabled = true
        mRegister.isEnabled = true
    }

    /**
     * Disable all inputs
     */
    private fun disable() {
        mProgress.visibility = View.VISIBLE
        mEmail.isEnabled = false
        mUsername.isEnabled = false
        mPassword.isEnabled = false
        mVerifyPassword.isEnabled = false
        mRegister.isEnabled = false
    }

    /**
     * Clears all inputs
     */
    private fun clear() {
        mEmail.text.clear()
        mUsername.text.clear()
        mPassword.text.clear()
        mVerifyPassword.text.clear()
    }

    companion object {
        const val TAG = "CMV-RegisterActivity"
    }

}