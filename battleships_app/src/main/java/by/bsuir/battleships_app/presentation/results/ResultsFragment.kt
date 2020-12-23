package by.bsuir.battleships_app.presentation.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.battleships_app.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.results_fragment.*
import kotlinx.android.synthetic.main.results_fragment.view.*

class ResultsFragment : Fragment() {

    private val viewModel: ResultsViewModel by lazy {
        getViewModel { ResultsViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.results_fragment, container, false)

        view.your_username_textView.text = viewModel.user?.displayName
        view.finish_game_button.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.yourAvatar.observe(viewLifecycleOwner, {
            your_avatar_imageView.setImageDrawable(it)
        })

        viewModel.enemyAvatar.observe(viewLifecycleOwner, {
            enemy_avatar_imageView.setImageDrawable(it)
        })

        viewModel.enemyNickname.observe(viewLifecycleOwner, {
            enemy_username_textView.text = it
        })

        val args = ResultsFragmentArgs.fromBundle(requireArguments())
        viewModel.loadPlayersData(requireContext(), args.firstPlayerData, args.secondPlayerData)
        showResults(args)
    }

    private fun showResults(args: ResultsFragmentArgs) {
        if (args.winnerId == viewModel.user!!.uid) {
            highlightWinner(your_avatar_imageView)
            personal_result_textView.text = getString(R.string.you_won)
        } else {
            highlightWinner(enemy_avatar_imageView)
            personal_result_textView.text = getString(R.string.you_lost)
        }
    }

    private fun highlightWinner(avatar: CircleImageView) {
        avatar.layoutParams.width = (avatar.layoutParams.width*1.15).toInt()
        avatar.layoutParams.height = (avatar.layoutParams.height*1.15).toInt()
        avatar.requestLayout()

        avatar.borderColor = ContextCompat.getColor(requireContext(), R.color.support)
    }
}