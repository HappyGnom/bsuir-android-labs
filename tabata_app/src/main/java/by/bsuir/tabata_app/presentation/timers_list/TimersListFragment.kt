package by.bsuir.tabata_app.presentation.timers_list

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.data.database.TabataDatabase
import by.bsuir.tabata_app.data.entity.Timer
import by.bsuir.tabata_app.model.TimerItem
import com.afollestad.materialdialogs.MaterialDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.timers_list_fragment.view.*

class TimersListFragment : Fragment() {

    private val viewModel: TimersListViewModel by lazy {
        getViewModel { TimersListViewModel(TabataDatabase.getInstance(requireContext())) }
    }

    private val timersAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timers_list_fragment, container, false)

        view.timers_recyclerView.adapter = timersAdapter
        timersAdapter.setOnItemClickListener { item, _ ->
            openTimer((item as TimerItem).timer)
        }

        timersAdapter.setOnItemLongClickListener { item, _ ->
            showDeleteTimerDialog((item as TimerItem).timer)
            true
        }

        view.motivation_textView.text =
            resources.getStringArray(R.array.motivation_quotes).toList().random()

        view.settings_button.setOnClickListener {
            openSettings()
        }

        view.add_button.setOnClickListener { createNewTimer() }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.timers.observe(viewLifecycleOwner, { timers ->
            timersAdapter.updateAsync(timers.map { TimerItem(it) })
        })

        viewModel.getAllTimersAsync()
    }

    private fun showDeleteTimerDialog(timer: Timer) {
        if (timer.id == null) return

        val deleteTimerMessage = getString(R.string.delete_timer_question_template)
            .replace("x", timer.name)

        val icon = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.theme_deleteIcon, icon, true)

        MaterialDialog(requireContext()).show {
            title(R.string.delete_timer)
            message(text = deleteTimerMessage)
            cornerRadius(8f)
            positiveButton(R.string.yes) { viewModel.deleteTimer(timer.id) }
            negativeButton(R.string.no)
            icon(icon.resourceId)
        }
    }

    private fun openSettings() {
        val action = TimersListFragmentDirections
            .actionTimersListFragmentToSettingsFragment()
        findNavController().navigate(action)
    }

    private fun openTimer(timer: Timer) {
        val action = TimersListFragmentDirections
            .actionTimersListFragmentToTimerFragment(timer)
        findNavController().navigate(action)
    }

    private fun createNewTimer() {
        val action = TimersListFragmentDirections
            .actionTimersListFragmentToTimerFragment()
        findNavController().navigate(action)
    }
}
