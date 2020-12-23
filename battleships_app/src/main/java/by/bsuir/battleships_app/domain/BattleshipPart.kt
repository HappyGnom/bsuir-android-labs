package by.bsuir.battleships_app.domain

import androidx.annotation.IntRange

data class BattleshipPart(
    @IntRange(from = 0, to = 9) var x: Int = 0,
    @IntRange(from = 0, to = 9) var y: Int = 0,
    var isDestroyed: Boolean = false
) {
    fun destroy() {
        isDestroyed = true
    }
}