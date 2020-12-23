package by.bsuir.battleships_app.domain

class FriendlyBattlefield(battleships : List<Battleship> = listOf()) : Battlefield(battleships) {

    override fun projectShipsToBoard() {
        val boardCopy = shipBoard.value?.toMutableList() ?: return

        for (index in 0.until(boardCopy.size)) {
            boardCopy[index] = CellStatus.WATER
        }

        ships.value?.forEach { ship ->
            ship.shipParts.forEach { part ->
                val cellStatus = when {
                    part.isDestroyed -> CellStatus.SHIP_HIT
                    else -> CellStatus.SHIP
                }

                boardCopy[part.y * 10 + part.x] = cellStatus
            }
        }

        hitPoints.value?.forEach {
            boardCopy[it] = CellStatus.WATER_HIT
        }

        _shipBoard.value = boardCopy.toList()
    }
}