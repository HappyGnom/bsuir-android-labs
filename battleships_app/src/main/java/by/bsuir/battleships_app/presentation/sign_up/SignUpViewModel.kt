package by.bsuir.battleships_app.presentation.sign_up

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent

class SignUpViewModel : ViewModel() {

    private val _registerSuccess = MutableLiveEvent<EventArgs<Boolean>>()
    val registerSuccess: LiveData<EventArgs<Boolean>> = _registerSuccess

    private val _isProcessing = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> = _isProcessing

    fun inputErrors(email: String, password: String, username: String) = when {
        password.isBlank() || username.isBlank() || email.isBlank() -> R.string.fill_all_data
        password.length < 5 -> R.string.password_too_short
        password.length > 16 -> R.string.password_too_long
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.wrong_email_format
        username.length > 20 -> R.string.username_too_long
        username.length < 4 -> R.string.username_too_short
        else -> null
    }

    fun register(email: String, password: String, username: String) {
        _isProcessing.value = true

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    setUserName(task.result!!.user!!, username)
                else
                    registrationFailed()
            }
    }

    private fun setUserName(user: FirebaseUser, username: String) {
        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(username).build()

        user.updateProfile(profileUpdate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    registrationSucceeded()
                else
                    registrationFailed()
            }
    }
    
    private fun registrationSucceeded(){
        _registerSuccess.postValue(EventArgs(true))
        _isProcessing.value = false
    }

    private fun registrationFailed(){
        _registerSuccess.postValue(EventArgs(false))
        _isProcessing.value = false
    }
}