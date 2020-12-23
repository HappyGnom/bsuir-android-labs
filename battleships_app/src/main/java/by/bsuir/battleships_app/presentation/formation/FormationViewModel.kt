package by.bsuir.battleships_app.presentation.formation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.domain.Battleship
import by.bsuir.battleships_app.domain.FormationBattlefield
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent

class FormationViewModel : ViewModel() {

    private val user = FirebaseAuth.getInstance().currentUser
    private val database = Firebase.database.reference

    private val _dbMessageID = MutableLiveEvent<EventArgs<Int>>()
    val dbMessageID: LiveData<EventArgs<Int>> = _dbMessageID

    companion object {
        private const val ACCOUNT_DB_REF = "ACCOUNT"
        private const val FORMATION_DB_REF = "FORMATION"
    }

    private val battlefield = FormationBattlefield()
    fun getBattlefieldLiveData() = battlefield.shipBoard

    init {
        loadSavedFormation()
    }

    fun loadSavedFormation() {
        if (user == null) return

        val accountDbRef = database.child(ACCOUNT_DB_REF).child(user.uid)
        val formationDbRef = accountDbRef.child(FORMATION_DB_REF)

        formationDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return
                val ships = mutableListOf<Battleship>()

                snapshot.children.forEach {
                    ships.add(it.getValue(Battleship::class.java)!!)
                }

                battlefield.setShips(ships)
                _dbMessageID.postValue(EventArgs(R.string.formation_load_success))
            }

            override fun onCancelled(error: DatabaseError) {
                _dbMessageID.postValue(EventArgs(R.string.formation_load_fail))
            }
        })
    }

    fun saveFormation() {
        val ships = battlefield.getShips() ?: return
        if (user == null) return

        val accountDbRef = database.child(ACCOUNT_DB_REF).child(user.uid)
        val formationDbRef = accountDbRef.child(FORMATION_DB_REF)

        formationDbRef.setValue(ships).addOnSuccessListener {
            _dbMessageID.postValue(EventArgs(R.string.formation_save_success))
        }.addOnFailureListener {
            _dbMessageID.postValue(EventArgs(R.string.formation_save_fail))
        }
    }

    fun processPressedCell(position: Int) {
        val cellPosition = position - 11
        val cellNumber = (cellPosition - cellPosition / 11 - 1)

        battlefield.selectShip(cellNumber)
    }

    fun unselectShip() = battlefield.clearShipSelection()
    fun moveShipUp() = battlefield.moveSelectedUp()
    fun moveShipDown() = battlefield.moveSelectedDown()
    fun moveShipLeft() = battlefield.moveSelectedLeft()
    fun moveShipRight() = battlefield.moveSelectedRight()
    fun rotateShip() = battlefield.rotateSelected()
}

