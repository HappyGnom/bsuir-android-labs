package by.bsuir.converter.presentation.convertation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.utils.showToast
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.converter.BuildConfig
import by.bsuir.converter.Numpad
import by.bsuir.converter.R
import by.bsuir.converter.data.Category
import kotlinx.android.synthetic.main.convertation_fragment.*
import kotlinx.android.synthetic.main.convertation_fragment.view.*
import java.text.DecimalFormat

class ConvertationFragment : Fragment() {

    private val viewModel: ConvertationViewModel by lazy {
        getViewModel { ConvertationViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.convertation_fragment, container, false)

        val args = ConvertationFragmentArgs.fromBundle(requireArguments())
        val category = args.category

        view.category_name_textView.text = getString(category.nameId)
        setupUnitSpinners(view.original_unit_spinner, view.converted_unit_spinner, category)
        setupNumpad(view.numpad, view.original_value_editText)

        view.original_value_editText.doOnTextChanged { _, _, _, _ ->
            convert(category)
        }

        view.back_button.setOnClickListener { findNavController().popBackStack() }

        view.swap_button.setOnClickListener { swapValues() }
        view.original_copy_button.setOnClickListener { copyOriginalValue() }
        view.converted_copy_button.setOnClickListener { copyConvertedValue() }

        @Suppress("ConstantConditionIf")
        if (!BuildConfig.IS_PREMIUM)
            disablePremiumButtons(view)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.convertedValue.observe(viewLifecycleOwner, Observer { convertedValue ->
            converted_value_textView.text = DecimalFormat("#.#####").format(convertedValue)
        })
    }

    private fun setupUnitSpinners(
        originalUnitSpinner: Spinner,
        convertedUnitSpinner: Spinner,
        category: Category
    ) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setupSpinner(
                originalUnitSpinner, R.layout.unit_spinner_item,
                R.layout.unit_spinner_dropdown_item_colored, category
            )
        else
            setupSpinner(
                originalUnitSpinner, R.layout.unit_spinner_item,
                R.layout.unit_spinner_dropdown_item, category
            )


        setupSpinner(
            convertedUnitSpinner, R.layout.unit_spinner_item_colored,
            R.layout.unit_spinner_dropdown_item_colored, category
        )
        convertedUnitSpinner.setSelection(1)
    }

    private fun setupSpinner(
        spinner: Spinner, @LayoutRes itemLayoutId: Int,
        @LayoutRes dropdownLayoutId: Int, category: Category
    ) {
        val unitAdapter = ArrayAdapter.createFromResource(
            requireContext(), category.unitsId, itemLayoutId
        )
        unitAdapter.setDropDownViewResource(dropdownLayoutId)
        spinner.adapter = unitAdapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                convert(category)
            }
        }
    }

    private fun setupNumpad(numpad: Numpad, inputField: TextView) {
        inputField.setRawInputType(InputType.TYPE_CLASS_TEXT)
        val inputConnection = inputField.onCreateInputConnection(EditorInfo())
        numpad.setInputConnection(inputConnection)
    }

    private fun convert(category: Category) = viewModel.convert(
        original_value_editText.text.toString(),
        original_unit_spinner.selectedItem.toString(),
        converted_unit_spinner.selectedItem.toString(),
        category.type
    )

    private fun disablePremiumButtons(view: View) {
        view.original_copy_button.visibility = View.INVISIBLE
        view.converted_copy_button.visibility = View.INVISIBLE
        view.swap_button.visibility = View.INVISIBLE
    }

    private fun swapValues() {
        original_value_editText.setText(converted_value_textView.text.removePrefix("-"))
        original_value_editText.setSelection(original_value_editText.text.length)

        val originalUnitPosition = original_unit_spinner.selectedItemPosition
        val convertedUnitPosition = converted_unit_spinner.selectedItemPosition

        original_unit_spinner.setSelection(convertedUnitPosition)
        converted_unit_spinner.setSelection(originalUnitPosition)
    }

    private fun copyOriginalValue() {
        var originalValue = original_value_editText.text.toString()
        val originalUnit = original_unit_spinner.selectedItem.toString()

        if (originalValue.isEmpty() || originalValue == ".")
            originalValue = "0.0"

        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
        val clip = ClipData.newPlainText("Original value", "$originalValue $originalUnit")
        clipboard?.setPrimaryClip(clip)

        requireActivity().showToast(getString(R.string.original_copied))
    }

    private fun copyConvertedValue() {
        val convertedValue = converted_value_textView.text.toString()
        val convertedUnit = converted_unit_spinner.selectedItem.toString()

        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
        val clip = ClipData.newPlainText("Converted value", "$convertedValue $convertedUnit")
        clipboard?.setPrimaryClip(clip)

        requireActivity().showToast(getString(R.string.converted_copied))
    }
}