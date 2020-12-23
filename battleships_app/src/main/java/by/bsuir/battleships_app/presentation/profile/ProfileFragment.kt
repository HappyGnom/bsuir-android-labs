package by.bsuir.battleships_app.presentation.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.data.Stats
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.view.*

class ProfileFragment : Fragment() {

    companion object {
        private const val GET_PHOTO_CODE = 546
    }

    private val viewModel: ProfileViewModel by lazy {
        getViewModel { ProfileViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        view.back_button.setOnClickListener {
            findNavController().popBackStack()
        }

        view.sign_out_button.setOnClickListener {
            signOut()
        }

        view.edit_username_button.setOnClickListener {
            showChangeNameDialog()
        }

        view.avatar_imageView.setOnClickListener {
            changeAvatarIntent()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.username.observe(viewLifecycleOwner, {
            username_textView.text = it
        })

        viewModel.avatar.observe(viewLifecycleOwner, {
            avatar_imageView.setImageDrawable(it)
        })

        viewModel.userStats.observe(viewLifecycleOwner, {
            displayStats(it)
        })

        viewModel.requestUserInfo(requireContext())
    }

    private fun showChangeNameDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.change_nick)
            cornerRadius(8f)
            input(
                hintRes = R.string.how_to_call_you,
                prefill = viewModel.user?.displayName,
                waitForPositiveButton = false
            ) { dialog, text ->
                val inputField = dialog.getInputField()

                val inputError = when {
                    text.length > 20 -> getString(R.string.username_too_long)
                    text.length < 4 -> getString(R.string.username_too_short)
                    else -> null
                }

                inputField.error = inputError
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, inputError == null)
            }
            icon(R.drawable.ic_edit)
            positiveButton(R.string.save) {
                val inputField = it.getInputField()
                val newName = inputField.text.toString()
                viewModel.updateUsername(newName)
            }
            negativeButton(R.string.dismiss)
        }
    }

    private fun displayStats(stats: Stats) {
        total_fights_textView.text = stats.totalFights.toString()
        wins_textView.text = stats.wins.toString()
        looses_textView.text = stats.looses.toString()
        ships_destroyed_textView.text = stats.shipsDestroyed.toString()
    }

    private fun changeAvatarIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, GET_PHOTO_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GET_PHOTO_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data ?: return
            viewModel.updateAvatar(requireContext(), imageUri)
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()

        val action = ProfileFragmentDirections
            .actionProfileFragmentToAuthenticationActivity()

        findNavController().navigate(action)
        requireActivity().finish()
    }
}