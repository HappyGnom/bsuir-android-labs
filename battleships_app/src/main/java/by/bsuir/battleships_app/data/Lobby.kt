package by.bsuir.battleships_app.data

import by.bsuir.battleships_app.domain.Battleship

data class Lobby(
    val name: String = "",
    val password: String = "",
    val id: String? = null,
    val firstPlayer: UserData? = null,
    val secondPlayer: UserData? = null,
    val turn: Turn = Turn(),
    val firstPlayerBattleships: List<Battleship>? = null,
    val firstPlayerHitPoints: List<Int>? = null,
    val secondPlayerBattleships: List<Battleship>? = null,
    val secondPlayerHitPoints: List<Int>? = null,
)
