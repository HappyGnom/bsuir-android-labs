package by.bsuir.battleships_app.presentation.formation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.utils.showShortToast
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.domain.Battlefield.CellStatus.*
import by.bsuir.battleships_app.model.CellImageItem
import by.bsuir.battleships_app.model.CellTextItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.formation_fragment.view.*

class FormationFragment : Fragment() {

    private val viewModel: FormationViewModel by lazy {
        getViewModel { FormationViewModel() }
    }

    private val boardAdapter = GroupAdapter<GroupieViewHolder>()
    private val displayedBoard: MutableList<CellImageItem> = mutableListOf()

    private val cellDrawables by lazy {
        mapOf(
            Pair(WATER, ContextCompat.getDrawable(requireContext(), R.drawable.ic_waves)!!),
            Pair(WATER_HIT, ContextCompat.getDrawable(requireContext(),R.drawable.ic_cross)!!),
            Pair(SHIP, ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship)!!),
            Pair(SHIP_HIT, ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship_hit)!!),
            Pair(SHIP_SELECTED, ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship_selected)!!),
            Pair(SHIP_CONFLICT, ContextCompat.getDrawable(requireContext(), R.drawable.ic_ship_conflict)!!),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.formation_fragment, container, false)

        view.back_button.setOnClickListener {
            findNavController().popBackStack()
        }

        view.up_button.setOnClickListener { viewModel.moveShipUp() }
        view.down_button.setOnClickListener { viewModel.moveShipDown() }
        view.left_button.setOnClickListener { viewModel.moveShipLeft() }
        view.right_button.setOnClickListener { viewModel.moveShipRight() }
        view.rotate_button.setOnClickListener { viewModel.rotateShip() }

        view.save_button.setOnClickListener { saveFormation() }
        view.reset_button.setOnClickListener { viewModel.loadSavedFormation() }

        view.board_recyclerView.adapter = boardAdapter
        initBoard()

        boardAdapter.setOnItemClickListener { item, _ ->
            val position = boardAdapter.getAdapterPosition(item)
            viewModel.processPressedCell(position)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getBattlefieldLiveData().observe(viewLifecycleOwner, { board ->
            board.forEachIndexed { index, cellStatus ->
                displayedBoard[index].updateImage(cellDrawables.getValue(cellStatus))
            }
        })

        viewModel.dbMessageID.observe(viewLifecycleOwner, {
            if(it.handled) return@observe

            requireActivity().showShortToast(getString(it.data!!))
            it.handled = true
        })
    }

    private fun saveFormation() {
        viewModel.unselectShip()

        if (viewModel.getBattlefieldLiveData().value!!.contains(SHIP_CONFLICT))
            requireActivity().showShortToast(getString(R.string.resolve_conflicts))
        else
            viewModel.saveFormation()
    }

    private fun initBoard() {
        boardAdapter.add(CellTextItem(""))
        for (x in 1..10) boardAdapter.add(CellTextItem(x.toString()))

        for (y in "ABCDEFGHIJ") {
            boardAdapter.add(CellTextItem(y.toString()))

            for (x in 1..10) {
                val cell = CellImageItem(cellDrawables.getValue(WATER))
                displayedBoard.add(cell)
                boardAdapter.add(cell)
            }
        }
    }
}