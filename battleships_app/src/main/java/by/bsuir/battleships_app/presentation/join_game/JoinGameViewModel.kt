package by.bsuir.battleships_app.presentation.join_game

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.data.Lobby
import by.bsuir.battleships_app.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent

class JoinGameViewModel : ViewModel() {

    private val user = FirebaseAuth.getInstance().currentUser
    private val database = Firebase.database.reference

    private val _dbMessageID = MutableLiveEvent<EventArgs<Int>>()
    val dbMessageID: LiveData<EventArgs<Int>> = _dbMessageID

    companion object {
        private const val LOBBY_DB_REF = "LOBBY"
    }

    private val _gameJoinSuccess = MutableLiveEvent<EventArgs<Boolean>>()
    val gameJoinSuccess: LiveData<EventArgs<Boolean>> = _gameJoinSuccess

    fun inputErrors(id: String, password: String) = when {
        password.isBlank() || id.isBlank() -> R.string.fill_all_data
        password.length < 5 -> R.string.password_too_short
        password.length > 16 -> R.string.password_too_long
        id.length < 4 -> R.string.lobby_id_too_short
        else -> null
    }

    fun joinGame(id: String, password: String) {
        if (user == null) return

        val lobbyDbRef = database.child(LOBBY_DB_REF).child(id)
        lobbyDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lobby = snapshot.getValue(Lobby::class.java) ?: return
                if (lobby.password != password) {
                    _dbMessageID.postValue(EventArgs(R.string.wrong_lobby_password))
                    return
                }

                updateLobbyInfo(lobby)
            }

            override fun onCancelled(error: DatabaseError) {
                _dbMessageID.postValue(EventArgs(R.string.wrong_lobby_id))
            }
        })
    }

    private fun updateLobbyInfo(lobby: Lobby) {
        val lobbyDbRef = database.child(LOBBY_DB_REF).child(lobby.id!!)
        val secondPlayerDbRef = lobbyDbRef.child("secondPlayer")
        val userData = UserData(user!!.uid, user.displayName!!, user.photoUrl.toString())

        secondPlayerDbRef.setValue(userData).addOnSuccessListener {
            _dbMessageID.postValue(EventArgs(R.string.lobby_join_success))
        }.addOnFailureListener {
            _dbMessageID.postValue(EventArgs(R.string.lobby_join_fail))
        }

        val secondPlayerShipsDbRef = lobbyDbRef.child("secondPlayerBattleships")
        secondPlayerShipsDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                    _gameJoinSuccess.postValue(EventArgs(true))
            }

            override fun onCancelled(error: DatabaseError) {
                _gameJoinSuccess.postValue(EventArgs(false))
            }
        })
    }
}