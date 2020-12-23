package by.bsuir.battleships_app.presentation.battle

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.data.Lobby
import by.bsuir.battleships_app.data.Stats
import by.bsuir.battleships_app.data.Turn
import by.bsuir.battleships_app.domain.Battlefield
import by.bsuir.battleships_app.domain.Battleship
import by.bsuir.battleships_app.domain.EnemyBattlefield
import by.bsuir.battleships_app.domain.EnemyBattlefield.HitType
import by.bsuir.battleships_app.domain.FriendlyBattlefield
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent
import java.util.*
import kotlin.concurrent.schedule

class BattleViewModel : ViewModel() {

    val user = FirebaseAuth.getInstance().currentUser
    private val database = Firebase.database.reference
    private var currentLobby: Lobby? = null

    companion object {
        private const val LOBBY_DB_REF = "LOBBY"
        private const val ACCOUNT_DB_REF = "ACCOUNT"
        private const val STATS_DB_REF = "STATS"
    }

    private val yourBattlefield = FriendlyBattlefield()
    private val enemyBattlefield = EnemyBattlefield()

    fun getYourBattlefieldLiveData() = yourBattlefield.shipBoard
    fun getEnemyBattlefieldLiveData() = enemyBattlefield.shipBoard

    fun getYourShipsLiveData() = yourBattlefield.getShipsLiveData()
    fun getEnemyShipsLiveData() = enemyBattlefield.getShipsLiveData()

    private var canAct = false
    private val _currentTurn = MutableLiveData<Turn>()
    val currentTurn: LiveData<Turn> = _currentTurn

    private val _winnerId = MutableLiveEvent<EventArgs<String>>()
    val winnerId: LiveData<EventArgs<String>> = _winnerId

    private val _yourAvatar = MutableLiveData<Drawable>()
    val yourAvatar: LiveData<Drawable> = _yourAvatar

    private val _enemyNickname = MutableLiveData<String>()
    val enemyNickname: LiveData<String> = _enemyNickname

    private val _enemyAvatar = MutableLiveData<Drawable>()
    val enemyAvatar: LiveData<Drawable> = _enemyAvatar

    fun getCurrentLobby() = currentLobby

    fun loadLobbyData(context: Context, lobbyId: String) {
        val lobbyDbRef = database.child(LOBBY_DB_REF).child(lobbyId)
        loadYourAvatar(context)

        lobbyDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentLobby = snapshot.getValue(Lobby::class.java)
                initGame(context)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initGame(context: Context) {
        if (currentLobby!!.firstPlayer!!.id == user!!.uid) {
            yourBattlefield.setShips(currentLobby!!.firstPlayerBattleships!!)
            enemyBattlefield.setShips(currentLobby!!.secondPlayerBattleships!!)
            subscribeToUpdates("first")

            _enemyNickname.value = currentLobby!!.secondPlayer!!.nickname
            loadEnemyAvatar(context, currentLobby!!.secondPlayer!!.avatarUrl)
        } else {
            yourBattlefield.setShips(currentLobby!!.secondPlayerBattleships!!)
            enemyBattlefield.setShips(currentLobby!!.firstPlayerBattleships!!)
            subscribeToUpdates("second")

            _enemyNickname.value = currentLobby!!.firstPlayer!!.nickname
            loadEnemyAvatar(context, currentLobby!!.firstPlayer!!.avatarUrl)
        }
    }

    private fun subscribeToUpdates(playerNumber: String) {
        val lobbyDbRef = database.child(LOBBY_DB_REF).child(currentLobby!!.id!!)

        val battleshipsDbRef = lobbyDbRef.child(playerNumber + "PlayerBattleships")
        val hitPointsDbRef = lobbyDbRef.child(playerNumber + "PlayerHitPoints")

        val turnDbRef = lobbyDbRef.child("turn")
        val winnerDbRef = lobbyDbRef.child("Winner")

        battleshipsDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return
                val ships = mutableListOf<Battleship>()

                snapshot.children.forEach {
                    ships.add(it.getValue(Battleship::class.java)!!)
                }

                yourBattlefield.setShips(ships)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        hitPointsDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return
                val hitPoints = mutableListOf<Int>()

                snapshot.children.forEach {
                    hitPoints.add(it.getValue(Int::class.java)!!)
                }

                yourBattlefield.setHitPoints(hitPoints)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        turnDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return
                _currentTurn.value = snapshot.getValue(Turn::class.java)
                canAct = true
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        winnerDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return
                updateStatsDeclareWinner(snapshot.getValue(String::class.java)!!)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadYourAvatar(context: Context) {
        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.octopus)
            .error(R.drawable.octopus)
            .target { drawable ->
                _yourAvatar.postValue(drawable)
            }

        if (user?.photoUrl != null)
            request.data(user.photoUrl)
        else
            request.data(R.drawable.octopus)

        ImageLoader(context).enqueue(request.build())
    }

    private fun loadEnemyAvatar(context: Context, photoUri: String?) {
        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.octopus)
            .error(R.drawable.octopus)
            .target { drawable ->
                _enemyAvatar.postValue(drawable)
            }

        if (!photoUri.isNullOrBlank() && photoUri != "null")
            request.data(photoUri)
        else
            request.data(R.drawable.octopus)

        ImageLoader(context).enqueue(request.build())
    }

    fun processEnemyShot(position: Int) {
        if (!canAct) return
        val cellPosition = position - 11
        val cellNumber = (cellPosition - cellPosition / 11 - 1)

        val hitResult = enemyBattlefield.hitCell(cellNumber)
        if (hitResult == HitType.UNDEFINED) return

        canAct = false
        uploadEnemyBattlefield()

        Timer("Delay", false).schedule(500) {
            when {
                enemyBattlefield.getShips()!!.all { it.isDestroyed } -> uploadWinner()
                hitResult == HitType.HIT -> uploadNextTurn(true)
                else -> uploadNextTurn(false)
            }
        }
    }

    private fun uploadEnemyBattlefield() {
        if (currentLobby!!.firstPlayer!!.id == user!!.uid) {
            uploadBattlefield("second", enemyBattlefield)
        } else {
            uploadBattlefield("first", enemyBattlefield)
        }
    }

    private fun uploadBattlefield(playerNumber: String, battlefield: Battlefield) {
        val lobbyDbRef = database.child(LOBBY_DB_REF).child(currentLobby!!.id!!)
        val battleshipsDbRef = lobbyDbRef.child(playerNumber + "PlayerBattleships")
        val hitPointsDbRef = lobbyDbRef.child(playerNumber + "PlayerHitPoints")

        battleshipsDbRef.setValue(battlefield.getShips())
        hitPointsDbRef.setValue(battlefield.getHitPoints())
    }

    private fun uploadWinner() {
        val lobbyDbRef = database.child(LOBBY_DB_REF).child(currentLobby!!.id!!)
        val winnerDbRef = lobbyDbRef.child("Winner")

        winnerDbRef.setValue(user!!.uid)
    }

    private fun uploadNextTurn(wasHit: Boolean) {
        val currentPlayerId = currentTurn.value!!.playerId
        val otherPlayerId = if (currentPlayerId == currentLobby!!.firstPlayer!!.id)
            currentLobby!!.secondPlayer!!.id
        else
            currentLobby!!.firstPlayer!!.id

        val nextPlayerId = if (wasHit) currentPlayerId else otherPlayerId
        val nextTurn = Turn(nextPlayerId, currentTurn.value!!.number + 1)

        val turnDbRef = database.child(LOBBY_DB_REF).child(currentLobby!!.id!!).child("turn")
        turnDbRef.setValue(nextTurn)
    }

    private fun updateStatsDeclareWinner(winnerId: String) {
        val accountDbRef = database.child(ACCOUNT_DB_REF).child(user!!.uid)
        val statsDbRef = accountDbRef.child(STATS_DB_REF)

        statsDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentStats = if (snapshot.exists())
                    snapshot.getValue(Stats::class.java)!!
                else
                    Stats()

                val newStats = getNewStats(currentStats, winnerId)

                statsDbRef.setValue(newStats).addOnSuccessListener {
                    _winnerId.value = EventArgs(winnerId)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getNewStats(oldStats: Stats, winnerId: String): Stats {
        var newWins = oldStats.wins
        var newLooses = oldStats.looses

        if (winnerId == user!!.uid)
            newWins++
        else
            newLooses++

        return Stats(
            oldStats.totalFights + 1, newWins, newLooses,
            oldStats.shipsDestroyed + enemyBattlefield.getShips()!!.count { it.isDestroyed })
    }
}