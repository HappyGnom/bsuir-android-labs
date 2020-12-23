package by.bsuir.battleships_app.presentation.await_players

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.utils.showShortToast
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.battleships_app.R
import kotlinx.android.synthetic.main.await_players_fragment.*
import kotlinx.android.synthetic.main.await_players_fragment.view.*


class AwaitPlayersFragment : Fragment() {

    private val viewModel: AwaitPlayersViewModel by lazy {
        getViewModel { AwaitPlayersViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.await_players_fragment, container, false)

        view.back_button.setOnClickListener {
            //TODO Cancel game
            findNavController().popBackStack()
        }

        val args = AwaitPlayersFragmentArgs.fromBundle(requireArguments())
        view.lobby_id_textView.text = args.lobbyId

        view.lobby_id_textView.setOnClickListener {
            copyId()
        }

        view.share_id_button.setOnClickListener {
            shareId()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.lobbyCreated.observe(viewLifecycleOwner, {
            if (it.handled) return@observe
            if (it.data!!) toBattleScreen()

            it.handled = true
        })

        val args = AwaitPlayersFragmentArgs.fromBundle(requireArguments())
        viewModel.awaitSecondPlayer(args.lobbyId)
    }

    private fun copyId() {
        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java) ?: return
        val clip = ClipData.newPlainText(getString(R.string.battleship_id), lobby_id_textView.text)
        clipboard.setPrimaryClip(clip)

        requireActivity().showShortToast(getString(R.string.id_copied))
    }

    private fun shareId() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.battleship_id))
            putExtra(Intent.EXTRA_TEXT, lobby_id_textView.text)
        }

        startActivity(Intent.createChooser(intent, getString(R.string.share_id_using)))
    }

    private fun toBattleScreen() {
        val args = AwaitPlayersFragmentArgs.fromBundle(requireArguments())

        val action = AwaitPlayersFragmentDirections
            .actionAwaitPlayersFragmentToBattleFragment(args.lobbyId)
        findNavController().navigate(action)
    }
}