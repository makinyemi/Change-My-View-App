package cmsc436.changemyview

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    private lateinit var mUsername: EditText
    private lateinit var mPassword: EditText
    private lateinit var mLogin: Button
    private lateinit var mSignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mUsername = findViewById(R.id.login_edit_username)
        mPassword = findViewById(R.id.login_edit_password)
        mLogin = findViewById(R.id.login_btn_login)
        mSignUp = findViewById(R.id.login_register)

        mSignUp.setOnClickListener {

        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = mAuth.currentUser
        if(currentUser != null) {
            // Already signed in

        } else {
            // User needs to sign in

        }
    }

    private fun register() {

    }

}