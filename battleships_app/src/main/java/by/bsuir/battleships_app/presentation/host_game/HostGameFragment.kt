package by.bsuir.battleships_app.presentation.host_game

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
import kotlinx.android.synthetic.main.host_game_fragment.*
import kotlinx.android.synthetic.main.host_game_fragment.view.*
import kotlinx.android.synthetic.main.menu_fragment.view.*

class HostGameFragment : Fragment() {

    private val viewModel: HostGameViewModel by lazy {
        getViewModel { HostGameViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.host_game_fragment, container, false)

        view.back_button.setOnClickListener {
            findNavController().popBackStack()
        }

        view.start_button.setOnClickListener {
            hostGame()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.gameCreatedId.observe(viewLifecycleOwner, Observer {
            if (it.handled) return@Observer

            if (it.data!!.isNotBlank()) enterLobby(it.data!!)
            else requireActivity().showToast(getString(R.string.failed_to_create_lobby))

            it.handled = true
        })
    }

    private fun hostGame() {
        val name = lobby_name_editText.text.toString().trim()
        val password = password_editText.text.toString().trim()

        val errorId = viewModel.inputErrors(name, password)

        if (errorId != null)
            requireActivity().showToast(getString(errorId))
        else
            viewModel.hostGame(name, password)
    }

    private fun enterLobby(lobbyId : String) {
        val action = HostGameFragmentDirections
            .actionHostGameFragmentToAwaitPlayersFragment(lobbyId)

        findNavController().navigate(action)
    }
}