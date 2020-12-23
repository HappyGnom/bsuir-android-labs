package by.bsuir.battleships_app.presentation.sign_in

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
import kotlinx.android.synthetic.main.sign_in_fragment.*
import kotlinx.android.synthetic.main.sign_in_fragment.view.*

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by lazy {
        getViewModel { SignInViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)

        view.to_sign_up_button.setOnClickListener { toSignUpScreen() }
        view.sign_in_button.setOnClickListener { signIn() }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.signInSuccess.observe(viewLifecycleOwner, Observer {
            if (it.handled) return@Observer

            if (it.data!!) enterApp()
            else requireActivity().showToast(getString(R.string.wrong_user_data))

            it.handled = true
        })
    }

    private fun toSignUpScreen() {
        findNavController().popBackStack()
    }

    private fun signIn() {
        val email = email_editText.text.toString().trim()
        val password = password_editText.text.toString().trim()

        viewModel.signIn(email, password)
    }

    private fun enterApp() {
        val action = SignInFragmentDirections
            .actionSignInFragmentToMainActivity()

        findNavController().navigate(action)
        requireActivity().finish()
    }
}