package by.bsuir.battleships_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityIntent = if (FirebaseAuth.getInstance().currentUser != null)
            Intent(this, MainActivity::class.java)
        else
            Intent(this, AuthenticationActivity::class.java)

        startActivity(activityIntent)
        finish()
    }
}