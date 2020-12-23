package by.bsuir.battleships_app.presentation.sign_up

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
import kotlinx.android.synthetic.main.sign_up_fragment.*
import kotlinx.android.synthetic.main.sign_up_fragment.view.*

class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by lazy {
        getViewModel { SignUpViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_up_fragment, container, false)

        view.to_sign_in_button.setOnClickListener { toSignInScreen() }
        view.sign_up_button.setOnClickListener { register() }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.registerSuccess.observe(viewLifecycleOwner, Observer {
            if (it.handled) return@Observer

            if (it.data!!) enterApp()
            else requireActivity().showToast(getString(R.string.failed_to_create_user))

            it.handled = true
        })
    }

    private fun toSignInScreen() {
        val action = SignUpFragmentDirections
            .actionSignUpFragmentToSignInFragment()

        findNavController().navigate(action)
    }

    private fun register() {
        val email = email_editText.text.toString().trim()
        val password = password_editText.text.toString().trim()
        val username = username_editText.text.toString().trim()

        val errorId = viewModel.inputErrors(email, password, username)

        if (errorId != null)
            requireActivity().showToast(getString(errorId))
        else
            viewModel.register(email, password, username)
    }

    private fun enterApp() {
        val action = SignUpFragmentDirections
            .actionSignUpFragmentToMainActivity()

        findNavController().navigate(action)
        requireActivity().finish()
    }
}