package by.bsuir.battleships_app.presentation.host_game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.data.Lobby
import by.bsuir.battleships_app.data.Turn
import by.bsuir.battleships_app.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent
import java.util.*

class HostGameViewModel : ViewModel() {

    private val user = FirebaseAuth.getInstance().currentUser
    private val database = Firebase.database.reference

    companion object {
        private const val LOBBY_DB_REF = "LOBBY"
    }

    private val _gameCreatedId = MutableLiveEvent<EventArgs<String>>()
    val gameCreatedId: LiveData<EventArgs<String>> = _gameCreatedId

    private val _isProcessing = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> = _isProcessing

    fun inputErrors(name: String, password: String) = when {
        password.isBlank() || name.isBlank() -> R.string.fill_all_data
        password.length < 5 -> R.string.password_too_short
        password.length > 16 -> R.string.password_too_long
        name.length > 20 -> R.string.lobby_name_too_long
        name.length < 4 -> R.string.lobby_name_too_short
        else -> null
    }

    fun hostGame(name: String, password: String) {
        if (user == null) return

        val lobbyId = UUID.randomUUID().toString()
        val userData = UserData(user.uid, user.displayName!!, user.photoUrl.toString())
        val lobby = Lobby(name, password, lobbyId, userData, turn = Turn(user.uid, 1))

        val lobbyDbRef = database.child(LOBBY_DB_REF).child(lobbyId)
        lobbyDbRef.setValue(lobby).addOnSuccessListener {
            _gameCreatedId.postValue(EventArgs(lobbyId))
        }.addOnFailureListener {
            _gameCreatedId.postValue(EventArgs(""))
        }
    }
}