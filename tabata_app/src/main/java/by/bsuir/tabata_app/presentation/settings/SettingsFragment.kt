package by.bsuir.tabata_app.presentation.settings

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import by.bsuir.architecture.utils.showShortToast
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.data.database.TabataDatabase
import com.afollestad.materialdialogs.MaterialDialog
import com.yariksoffice.lingver.Lingver
import kotlinx.android.synthetic.main.settings_fragment_layout.view.*

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by lazy {
        getViewModel { SettingsViewModel(TabataDatabase.getInstance(requireContext())) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<SwitchPreferenceCompat>("enable_night_theme")?.setOnPreferenceChangeListener { _, _ ->
            requireActivity().recreate()
            true
        }

        findPreference<DropDownPreference>("language")?.setOnPreferenceChangeListener { _, language ->
            Lingver.getInstance().setLocale(requireContext(), language.toString())
            requireActivity().recreate()
            true
        }

        findPreference<DropDownPreference>("font_size")?.setOnPreferenceChangeListener { _, _ ->
            requireActivity().recreate()
            true
        }

        findPreference<Preference>("clear_data")?.setOnPreferenceClickListener {
            showClearDataDialog()
            true
        }
    }

    private fun showClearDataDialog() {
        val icon = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.theme_deleteIcon, icon, true)

        MaterialDialog(requireContext()).show {
            title(R.string.clear_data)
            message(R.string.clear_data_question)
            cornerRadius(8f)
            positiveButton(R.string.yes) {
                viewModel.clearAllData()
                requireActivity().showShortToast(getString(R.string.all_data_cleared))
            }
            negativeButton(R.string.no)
            icon(icon.resourceId)
        }
    }
}