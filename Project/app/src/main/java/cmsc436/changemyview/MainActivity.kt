package cmsc436.changemyview

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private lateinit var mLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        mLogout = findViewById(R.id.home_btn_logout)
        mLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val logoutAlert = AlertDialog.Builder(this)
        logoutAlert.apply {
            setPositiveButton(R.string.btn_logout,
                DialogInterface.OnClickListener{dialog, id ->
                    auth.signOut()
                    val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(loginIntent)
            })
            setNegativeButton(R.string.btn_cancel,
                DialogInterface.OnClickListener{dialog, id -> })
        }
            .setTitle(R.string.btn_logout)
            .setMessage(R.string.logout_message)
            .create()
        logoutAlert.show()
    }

    override fun onStart() {
        super.onStart()

        Log.i(TAG, "onStart()")


        if(auth.currentUser == null) {
            // No user is signed in, launch the login activity
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        } else {
            currentUser = auth.currentUser!!
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(intent.hasExtra(LoginActivity.LOGIN)) {
            auth.signOut()
        }
    }

    companion object {
        const val TAG = "CMV-MainActivity"
    }
}