package by.bsuir.battleships_app.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.battleships_app.R
import kotlinx.android.synthetic.main.menu_fragment.*
import kotlinx.android.synthetic.main.menu_fragment.view.*

class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by lazy {
        getViewModel { MenuViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.menu_fragment, container, false)

        view.avatar_imageView.setOnClickListener { toUserProfileScreen() }
        view.nickname_textView.setOnClickListener { toUserProfileScreen() }
        view.account_button.setOnClickListener { toUserProfileScreen() }

        view.host_game_button.setOnClickListener { toHostGameScreen() }
        view.join_game_button.setOnClickListener { toJoinGameScreen() }

        view.formation_button.setOnClickListener { toFormationScreen() }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.username.observe(viewLifecycleOwner, {
            nickname_textView.text = it
        })

        viewModel.avatar.observe(viewLifecycleOwner, {
            avatar_imageView.setImageDrawable(it)
        })

        viewModel.requestUserInfo(requireContext())
    }

    private fun toUserProfileScreen() {
        val action = MenuFragmentDirections
            .actionMenuFragmentToProfileFragment()

        findNavController().navigate(action)
    }

    private fun toHostGameScreen() {
        val action = MenuFragmentDirections
            .actionMenuFragmentToHostGameFragment()

        findNavController().navigate(action)
    }

    private fun toJoinGameScreen() {
        val action = MenuFragmentDirections
            .actionMenuFragmentToJoinGameFragment()

        findNavController().navigate(action)
    }

    private fun toFormationScreen() {
        val action = MenuFragmentDirections
            .actionMenuFragmentToFormationFragment()

        findNavController().navigate(action)
    }
}