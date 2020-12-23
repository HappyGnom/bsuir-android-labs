package by.bsuir.battleships_app.presentation.battle

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.domain.Battlefield
import by.bsuir.battleships_app.domain.Battleship
import by.bsuir.battleships_app.model.CellImageItem
import by.bsuir.battleships_app.model.CellTextItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.battle_fragment.*
import kotlinx.android.synthetic.main.battle_fragment.view.*

class BattleFragment : Fragment() {

    private val viewModel: BattleViewModel by lazy {
        getViewModel { BattleViewModel() }
    }

    private val yourBoardAdapter = GroupAdapter<GroupieViewHolder>()
    private val yourBoardCells: MutableList<CellImageItem> = mutableListOf()

    private val enemyBoardAdapter = GroupAdapter<GroupieViewHolder>()
    private val enemyBoardCells: MutableList<CellImageItem> = mutableListOf()

    private val cellDrawables by lazy {
        mapOf(
            Pair(
                Battlefield.CellStatus.WATER,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_waves)!!
            ),
            Pair(
                Battlefield.CellStatus.WATER_HIT,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_cross)!!
            ),
            Pair(
                Battlefield.CellStatus.SHIP,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship)!!
            ),
            Pair(
                Battlefield.CellStatus.SHIP_HIT,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship_hit)!!
            ),
            Pair(
                Battlefield.CellStatus.SHIP_SELECTED,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship_selected)!!
            ),
            Pair(
                Battlefield.CellStatus.SHIP_CONFLICT,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship_conflict)!!
            ),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.battle_fragment, container, false)

        view.your_nickname_textView.text = viewModel.user?.displayName
        view.cancel_button.setOnClickListener { }

        view.your_board_recyclerView.adapter = yourBoardAdapter
        view.enemy_board_recyclerView.adapter = enemyBoardAdapter
        initBoards()

        enemyBoardAdapter.setOnItemClickListener { item, _ ->
            val position = enemyBoardAdapter.getAdapterPosition(item)
            viewModel.processEnemyShot(position)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getYourBattlefieldLiveData().observe(viewLifecycleOwner, { board ->
            board.forEachIndexed { index, cellStatus ->
                yourBoardCells[index].updateImage(cellDrawables.getValue(cellStatus))
            }
        })

        viewModel.getEnemyBattlefieldLiveData().observe(viewLifecycleOwner, { board ->
            board.forEachIndexed { index, cellStatus ->
                enemyBoardCells[index].updateImage(cellDrawables.getValue(cellStatus))
            }
        })

        viewModel.getYourShipsLiveData().observe(viewLifecycleOwner, { ships ->
            displayShipsData(ships, your_ships_textView)
        })

        viewModel.getEnemyShipsLiveData().observe(viewLifecycleOwner, { ships ->
            displayShipsData(ships, enemy_ships_textView)
        })

        viewModel.currentTurn.observe(viewLifecycleOwner, {
            if (it.playerId == viewModel.user!!.uid) displayYourTurn()
            else displayEnemyTurn()

            setTurnNumber(it.number)
        })

        viewModel.yourAvatar.observe(viewLifecycleOwner, {
            your_avatar_imageView.setImageDrawable(it)
        })

        viewModel.enemyAvatar.observe(viewLifecycleOwner, {
            enemy_avatar_imageView.setImageDrawable(it)
        })

        viewModel.enemyNickname.observe(viewLifecycleOwner, {
            enemy_nickname_textView.text = it
        })

        viewModel.winnerId.observe(viewLifecycleOwner, {
            if(it.handled) return@observe
            toResultScreen(it.data!!)

            it.handled = true
        })

        val args = BattleFragmentArgs.fromBundle(requireArguments())
        viewModel.loadLobbyData(requireContext(), args.lobbyId)
    }

    private fun initBoards() {
        yourBoardAdapter.add(CellTextItem(""))
        enemyBoardAdapter.add(CellTextItem(""))

        for (x in 1..10) {
            yourBoardAdapter.add(CellTextItem(x.toString()))
            enemyBoardAdapter.add(CellTextItem(x.toString()))
        }

        for (y in "ABCDEFGHIJ") {
            yourBoardAdapter.add(CellTextItem(y.toString()))
            enemyBoardAdapter.add(CellTextItem(y.toString()))

            for (x in 1..10) {
                val yourCell = CellImageItem(cellDrawables.getValue(Battlefield.CellStatus.WATER))
                yourBoardCells.add(yourCell)
                yourBoardAdapter.add(yourCell)

                val enemyCell = CellImageItem(cellDrawables.getValue(Battlefield.CellStatus.WATER))
                enemyBoardCells.add(enemyCell)
                enemyBoardAdapter.add(enemyCell)
            }
        }
    }

    private fun displayShipsData(ships: List<Battleship>, textView: TextView) {
        val shipsInfo = StringBuilder()

        val aliveShips = ships.filterNot { it.isDestroyed }
        val lengths = aliveShips.distinctBy { it.length }.map { it.length }.sorted()

        lengths.forEach { length ->
            val shipInfo = getString(R.string.ship_template).replace("x", length.toString())
                .replace("y", aliveShips.count { it.length == length }.toString())

            shipsInfo.appendLine(shipInfo)
        }

        textView.text = shipsInfo.toString()
    }

    private fun displayYourTurn() {
        turn_textView.text = getString(R.string.your_turn)
        highlightCard(your_cardView, R.color.success)
        disableCard(enemy_cardView)

        enemy_board_recyclerView.visibility = View.VISIBLE
        your_board_recyclerView.visibility = View.INVISIBLE
    }

    private fun displayEnemyTurn() {
        turn_textView.text = getString(R.string.enemies_turn)
        highlightCard(enemy_cardView, R.color.danger)
        disableCard(your_cardView)

        enemy_board_recyclerView.visibility = View.INVISIBLE
        your_board_recyclerView.visibility = View.VISIBLE
    }

    private fun setTurnNumber(number: Int) {
        turn_number_textView.text = getString(R.string.turn_number_template)
            .replace("x", number.toString())
    }

    private fun highlightCard(card: CardView, @ColorRes colorId: Int) {
        card.alpha = 1.0f
        card.outlineSpotShadowColor = ContextCompat.getColor(requireContext(), colorId)
    }

    private fun disableCard(card: CardView) {
        card.alpha = 0.8f
        card.outlineSpotShadowColor = Color.BLACK
    }

    private fun toResultScreen(winnerId: String){
        val currentLobby = viewModel.getCurrentLobby()!!

        val action = BattleFragmentDirections
            .actionBattleFragmentToResultsFragment(winnerId, currentLobby.firstPlayer!!, currentLobby.secondPlayer!!)
        findNavController().navigate(action)
    }
}