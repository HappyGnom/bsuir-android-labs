package by.bsuir.battleships_app.domain

class EnemyBattlefield(battleships: List<Battleship> = listOf()) : Battlefield(battleships) {

    enum class HitType { HIT, MISS, UNDEFINED }

    override fun projectShipsToBoard() {
        val boardCopy = shipBoard.value?.toMutableList() ?: return

        for (index in 0.until(boardCopy.size)) {
            boardCopy[index] = CellStatus.WATER
        }

        ships.value?.forEach { ship ->
            ship.shipParts.forEach { part ->
                if (part.isDestroyed)
                    boardCopy[part.y * 10 + part.x] = CellStatus.SHIP_HIT
            }
        }

        hitPoints.value?.forEach {
            boardCopy[it] = CellStatus.WATER_HIT
        }

        _shipBoard.value = boardCopy.toList()
    }

    fun hitCell(cellNumber: Int): HitType {
        val x = cellNumber % 10
        val y = cellNumber / 10

        val allShipsCopy = ships.value?.toMutableList() ?: return HitType.UNDEFINED
        val cellStatus = shipBoard.value?.get(cellNumber) ?: return HitType.UNDEFINED

        if (cellStatus != CellStatus.WATER) return HitType.UNDEFINED

        val ship = allShipsCopy.find { it.hasPartAt(x, y) }
        if (ship != null) {
            allShipsCopy.remove(ship)
            ship.shipParts.find { it.x == x && it.y == y }?.destroy()
            allShipsCopy.add(ship)

            if (ship.isDestroyed)
                markPointsAroundDestroyedShip(ship)

            ships.value = allShipsCopy
            return HitType.HIT
        } else {
            val hitPointsCopy = hitPoints.value?.toMutableList() ?: return HitType.UNDEFINED
            hitPointsCopy.add(cellNumber)

            hitPoints.value = hitPointsCopy.distinct()
            return HitType.MISS
        }
    }

    private fun markPointsAroundDestroyedShip(ship: Battleship) {
        val hitPointsCopy = hitPoints.value?.toMutableList() ?: return

        val topLeft = ship.shipParts.sortedBy { it.y }.minByOrNull { it.x }!!
        val bottomRight = ship.shipParts.sortedByDescending { it.y }.maxByOrNull { it.x }!!

        for (x in (topLeft.x - 1)..(bottomRight.x + 1))
            for (y in (topLeft.y - 1)..(bottomRight.y + 1))
                if (x >= 0 && y >= 0 && x < 10 && y < 10 && !ship.hasPartAt(x, y))
                    hitPointsCopy.add(y * 10 + x)

        hitPoints.value = hitPointsCopy.distinct()
    }
}