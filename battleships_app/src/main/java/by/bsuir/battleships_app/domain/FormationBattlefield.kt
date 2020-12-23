package by.bsuir.battleships_app.domain

import androidx.lifecycle.MutableLiveData
import by.bsuir.battleships_app.domain.Battlefield.CellStatus.*

class FormationBattlefield(battleships: List<Battleship> = listOf()) : Battlefield(battleships) {

    private val selectedShip = MutableLiveData<Battleship?>()
    val isShipSelected get() = selectedShip.value != null

    init {
        selectedShip.observeForever {
            projectShipsToBoard()
        }
    }

    override fun setShips(ships: List<Battleship>) {
        clearShipSelection()
        super.setShips(ships)
    }

    override fun projectShipsToBoard() {
        val boardCopy = shipBoard.value?.toMutableList() ?: return

        for (index in 0.until(boardCopy.size)) {
            boardCopy[index] = WATER
        }

        ships.value?.forEach { ship ->
            ship.shipParts.forEach { part ->
                val cellStatus = when {
                    otherShipsAround(ship, part) -> SHIP_CONFLICT
                    part.isDestroyed -> SHIP_HIT
                    else -> SHIP
                }

                boardCopy[part.y * 10 + part.x] = cellStatus
            }
        }

        selectedShip?.value?.shipParts?.forEach {
            boardCopy[it.y * 10 + it.x] = SHIP_SELECTED
        }

        _shipBoard.value = boardCopy.toList()
    }

    fun moveSelectedUp() {
        selectedShip.value?.moveUp()
        projectShipsToBoard()
    }

    fun moveSelectedDown() {
        selectedShip.value?.moveDown()
        projectShipsToBoard()
    }

    fun moveSelectedLeft() {
        selectedShip.value?.moveLeft()
        projectShipsToBoard()
    }

    fun moveSelectedRight() {
        selectedShip.value?.moveRight()
        projectShipsToBoard()
    }

    fun rotateSelected() {
        selectedShip.value?.rotate()
        projectShipsToBoard()
    }

    fun selectShip(cellNumber: Int) {
        val x = cellNumber % 10
        val y = cellNumber / 10

        clearShipSelection()

        val allShipsCopy = ships.value?.toMutableList() ?: return
        val cellStatus = shipBoard.value?.get(cellNumber) ?: return

        if (cellStatus == SHIP || cellStatus == SHIP_CONFLICT) {
            val ship = allShipsCopy.find { it.hasPartAt(x, y) }
            selectedShip.value = ship
            allShipsCopy.remove(ship)
        }

        ships.value = allShipsCopy
    }

    fun clearShipSelection() {
        if (selectedShip.value == null) return

        val allShipsCopy = ships.value?.toMutableList()
        allShipsCopy?.add(selectedShip.value!!)
        ships.value = allShipsCopy

        selectedShip.value = null
    }

    private fun otherShipsAround(ship: Battleship, part: BattleshipPart): Boolean {
        val otherShips = ships.value!!.filterNot { it == ship }

        val onSame = otherShips.find { it.hasPartAt(part.x, part.y) } != null
        val onLeft = otherShips.find { it.hasPartAt(part.x - 1, part.y) } != null
        val onLeftBottom = otherShips.find { it.hasPartAt(part.x - 1, part.y + 1) } != null
        val onBottom = otherShips.find { it.hasPartAt(part.x, part.y + 1) } != null
        val onRightBottom = otherShips.find { it.hasPartAt(part.x + 1, part.y + 1) } != null
        val onRight = otherShips.find { it.hasPartAt(part.x + 1, part.y) } != null
        val onRightTop = otherShips.find { it.hasPartAt(part.x + 1, part.y - 1) } != null
        val onTop = otherShips.find { it.hasPartAt(part.x, part.y - 1) } != null
        val onLeftTop = otherShips.find { it.hasPartAt(part.x - 1, part.y - 1) } != null

        return onSame || onLeft || onLeftBottom || onBottom || onRightBottom || onRight || onRightTop || onTop || onLeftTop
    }
}