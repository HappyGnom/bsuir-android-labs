package by.bsuir.battleships_app.presentation.join_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.utils.showToast
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.battleships_app.R
import kotlinx.android.synthetic.main.join_game_fragment.*
import kotlinx.android.synthetic.main.join_game_fragment.view.*

class JoinGameFragment : Fragment() {

    private val viewModel: JoinGameViewModel by lazy {
        getViewModel { JoinGameViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.join_game_fragment, container, false)

        view.back_button.setOnClickListener {
            findNavController().popBackStack()
        }

        view.join_game_button.setOnClickListener {
            joinGame()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.gameJoinSuccess.observe(viewLifecycleOwner, Observer {
            if (it.handled) return@Observer

            if (it.data!!) enterLobby()
            else requireActivity().showToast(getString(R.string.lobby_join_fail))

            it.handled = true
        })
    }

    private fun joinGame() {
        val id = lobby_id_editText.text.toString().trim()
        val password = password_editText.text.toString().trim()

        val errorId = viewModel.inputErrors(id, password)

        if (errorId != null)
            requireActivity().showToast(getString(errorId))
        else
            viewModel.joinGame(id, password)
    }

    private fun enterLobby() {
        val id = lobby_id_editText.text.toString().trim()

        val action = JoinGameFragmentDirections
            .actionJoinGameFragmentToBattleFragment(id)
        findNavController().navigate(action)
    }
}