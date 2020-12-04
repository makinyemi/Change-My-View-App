package cmsc436.changemyview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
    }

    companion object {
        const val TAG = "CMV-RegisterActivity"
    }

}