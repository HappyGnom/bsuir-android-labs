package by.bsuir.battleships_app.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.bsuir.battleships_app.domain.Battlefield.CellStatus.WATER

abstract class Battlefield(battleships: List<Battleship> = listOf()) {

    protected val ships = MutableLiveData<List<Battleship>>()
    protected val hitPoints = MutableLiveData<List<Int>>(listOf())

    protected val _shipBoard = MutableLiveData<List<CellStatus>>()
    val shipBoard: LiveData<List<CellStatus>> = _shipBoard

    enum class CellStatus { WATER, WATER_HIT, SHIP, SHIP_HIT, SHIP_CONFLICT, SHIP_SELECTED }

    companion object {
        fun getDefaultShipsFormation(): List<Battleship> {
            val ship1 = Battleship(BattleshipPart(0, 0), BattleshipPart(0, 1))
            val ship2 = Battleship(BattleshipPart(2, 0), BattleshipPart(2, 1), BattleshipPart(2, 2))
            val ship3 = Battleship(BattleshipPart(4, 0), BattleshipPart(4, 1), BattleshipPart(4, 2))
            val ship4 = Battleship(
                BattleshipPart(6, 0), BattleshipPart(6, 1),
                BattleshipPart(6, 2), BattleshipPart(6, 3)
            )
            val ship5 = Battleship(
                BattleshipPart(8, 0), BattleshipPart(8, 1),
                BattleshipPart(8, 2), BattleshipPart(8, 3),
                BattleshipPart(8, 4)
            )

            return listOf(ship1, ship2, ship3, ship4, ship5)
        }
    }

    init {
        if (battleships.isNotEmpty())
            ships.value = battleships
        else
            ships.value = getDefaultShipsFormation()

        _shipBoard.value = MutableList(100) { WATER }
        ships.observeForever { projectShipsToBoard() }
        hitPoints.observeForever { projectShipsToBoard() }
    }

    abstract fun projectShipsToBoard()

    fun getShips() = ships.value
    fun getShipsLiveData(): LiveData<List<Battleship>> = ships

    fun getHitPoints() = hitPoints.value

    open fun setShips(ships: List<Battleship>) {
        this.ships.value = ships
    }

    fun setHitPoints(hitPoints: List<Int>) {
        this.hitPoints.value = hitPoints
    }
}