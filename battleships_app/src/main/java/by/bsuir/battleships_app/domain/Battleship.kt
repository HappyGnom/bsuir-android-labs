package by.bsuir.battleships_app.domain

class Battleship(shipParts: List<BattleshipPart> = listOf()) {

    private var parts: List<BattleshipPart> = shipParts
    val shipParts get() = parts

    val length get() = parts.size
    val isDestroyed get() = parts.all { it.isDestroyed }

    constructor(vararg shipParts: BattleshipPart) : this(shipParts.toList())

    fun setShipParts(shipParts: List<BattleshipPart>) {
        parts = shipParts
    }

    fun hasPartAt(x: Int, y: Int) = parts.find { it.x == x && it.y == y } != null

    fun moveUp() {
        val top = parts.minByOrNull { it.y }!!
        if (top.y == 0) return

        parts.forEach { it.y-- }
    }

    fun moveDown() {
        val bottom = parts.maxByOrNull { it.y }!!
        if (bottom.y == 9) return

        parts.forEach { it.y++ }
    }

    fun moveLeft() {
        val mostLeft = parts.minByOrNull { it.x }!!
        if (mostLeft.x == 0) return

        parts.forEach { it.x-- }
    }

    fun moveRight() {
        val mostRight = parts.maxByOrNull { it.x }!!
        if (mostRight.x == 9) return

        parts.forEach { it.x++ }
    }

    fun rotate() {
        val topLeft = parts.sortedBy { it.y }.minByOrNull { it.x }!!
        val otherLength = length - 1

        val isOkToRight = parts.find { it.x == topLeft.x + 1 } == null
                && (topLeft.x + otherLength) <= 9
        val isOkToTop = parts.find { it.y == topLeft.y - 1 } == null
                && (topLeft.y - otherLength) >= 0

        val isOkToLeft = parts.find { it.x == topLeft.x - 1 } == null
                && (topLeft.x - otherLength) >= 0
        val isOkToBottom = parts.find { it.y == topLeft.y + 1 } == null
                && (topLeft.y + otherLength) <= 9

        when {
            isOkToRight -> rotateToRight(topLeft)
            isOkToTop -> rotateToTop(topLeft)
            isOkToLeft -> rotateToLeft(topLeft)
            isOkToBottom -> rotateToBottom(topLeft)
        }
    }

    private fun rotateToRight(startPart: BattleshipPart) {
        val newParts = mutableListOf<BattleshipPart>()
        for (x in (startPart.x).until(startPart.x + length))
            newParts.add(BattleshipPart(x, startPart.y))

        parts = newParts
    }

    private fun rotateToTop(startPart: BattleshipPart) {
        val newParts = mutableListOf<BattleshipPart>()
        for (y in (startPart.y).downTo(startPart.y - length + 1))
            newParts.add(BattleshipPart(startPart.x, y))

        parts = newParts
    }

    private fun rotateToLeft(startPart: BattleshipPart) {
        val newParts = mutableListOf<BattleshipPart>()
        for (x in (startPart.x).downTo(startPart.x - length + 1))
            newParts.add(BattleshipPart(x, startPart.y))

        parts = newParts
    }

    private fun rotateToBottom(startPart: BattleshipPart) {
        val newParts = mutableListOf<BattleshipPart>()
        for (y in (startPart.y).until(startPart.y + length))
            newParts.add(BattleshipPart(startPart.x, y))

        parts = newParts
    }
}