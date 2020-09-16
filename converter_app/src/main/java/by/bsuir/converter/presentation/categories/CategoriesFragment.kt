package by.bsuir.converter.presentation.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.converter.BuildConfig
import by.bsuir.converter.R
import by.bsuir.converter.data.Category
import by.bsuir.converter.model.CategoryButtonItem
import by.bsuir.ui.SpacingDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.categories_fragment.view.*

class CategoriesFragment : Fragment() {

    private val viewModel: CategoriesViewModel by lazy {
        getViewModel { CategoriesViewModel() }
    }

    private val categoriesAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.categories_fragment, container, false)

        view.categories_recyclerView.adapter = categoriesAdapter
        val spacingItemDecoration = SpacingDecoration(80)
        view.categories_recyclerView.addItemDecoration(spacingItemDecoration)

        @Suppress("ConstantConditionIf")
        if (BuildConfig.BUILD_TYPE == "debug")
            setDebugInfoText(view.info_textView)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        categoriesAdapter.update(
            viewModel.categories.map {
                CategoryButtonItem(requireContext(), it, this::moveToConverterScreen)
            }
        )
    }

    private fun setDebugInfoText(infoTextView: TextView) {
        val infoText = StringBuilder().appendln(getString(R.string.developer_info))
            .append(getString(R.string.app_name)).append(" v").append(BuildConfig.VERSION_NAME)
            .toString()

        infoTextView.text = infoText
    }

    private fun moveToConverterScreen(category: Category) {
        val action = CategoriesFragmentDirections
            .actionCategoriesFragmentToConvertationFragment(category)
        findNavController().navigate(action)
    }
}