package by.bsuir.battleships_app.presentation.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent

class SignInViewModel : ViewModel() {

    private val _signInSuccess = MutableLiveEvent<EventArgs<Boolean>>()
    val signInSuccess: LiveData<EventArgs<Boolean>> = _signInSuccess

    private val _isProcessing = MutableLiveData<Boolean>(false)
    val isProcessing: LiveData<Boolean> = _isProcessing

    fun signIn(email: String, password: String) {
        _isProcessing.value = true

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    _signInSuccess.postValue(EventArgs(true))
                else
                    _signInSuccess.postValue(EventArgs(false))

                _isProcessing.value = false
            }
    }
}