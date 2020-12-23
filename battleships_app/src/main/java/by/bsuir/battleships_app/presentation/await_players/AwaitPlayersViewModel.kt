package by.bsuir.battleships_app.presentation.await_players

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.data.UserData
import by.bsuir.battleships_app.domain.Battlefield
import by.bsuir.battleships_app.domain.Battleship
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AwaitPlayersViewModel : ViewModel() {

    private val user = FirebaseAuth.getInstance().currentUser
    private val database = Firebase.database.reference

    companion object {
        private const val ACCOUNT_DB_REF = "ACCOUNT"
        private const val FORMATION_DB_REF = "FORMATION"
        private const val LOBBY_DB_REF = "LOBBY"
    }

    private val _lobbyCreated = MutableLiveEvent<EventArgs<Boolean>>()
    val lobbyCreated: LiveData<EventArgs<Boolean>> = _lobbyCreated

    fun awaitSecondPlayer(lobbyId: String) {
        if (user == null) return

        val secondPlayerDbRef = database.child(LOBBY_DB_REF).child(lobbyId).child("secondPlayer")
        secondPlayerDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val secondPlayer = snapshot.getValue(UserData::class.java) ?: return
                loadShipFormations(lobbyId, user.uid, secondPlayer.id)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadShipFormations(lobbyId: String, firstPlayerId: String, secondPlayerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val firstPlayerFormation = loadShips(firstPlayerId)
            val secondPlayerFormation = loadShips(secondPlayerId)

            val lobbyDbRef = database.child(LOBBY_DB_REF).child(lobbyId)
            val firstPlayerShipsDbRef = lobbyDbRef.child("firstPlayerBattleships")
            val secondPlayerShipsDbRef = lobbyDbRef.child("secondPlayerBattleships")

            firstPlayerShipsDbRef.setValue(firstPlayerFormation)
            secondPlayerShipsDbRef.setValue(secondPlayerFormation).addOnSuccessListener {
                _lobbyCreated.postValue(EventArgs(true))
            }
        }
    }

    private suspend fun loadShips(playerId: String) =
        suspendCoroutine<List<Battleship>> { continuation ->
            val accountDbRef = database.child(ACCOUNT_DB_REF).child(playerId)
            val formationDbRef = accountDbRef.child(FORMATION_DB_REF)

            formationDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()){
                        continuation.resume(Battlefield.getDefaultShipsFormation())
                        return
                    }

                    val ships = mutableListOf<Battleship>()
                    snapshot.children.forEach {
                        ships.add(it.getValue(Battleship::class.java)!!)
                    }

                    continuation.resume(ships)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
}