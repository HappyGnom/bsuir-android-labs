package by.bsuir.tabata_app.presentation.timer

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.utils.showShortToast
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.data.database.TabataDatabase
import by.bsuir.tabata_app.data.entity.Timer
import by.bsuir.tabata_app.domain.TimeUtils
import by.bsuir.tabata_app.model.ExerciseItem
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.timer_fragment.*
import kotlinx.android.synthetic.main.timer_fragment.view.*

class TimerFragment : Fragment() {

    private val viewModel: TimerViewModel by lazy {
        getViewModel { TimerViewModel(TabataDatabase.getInstance(requireContext())) }
    }

    private val exerciseAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timer_fragment, container, false)

        view.exercises_recyclerView.adapter = exerciseAdapter

        view.back_button.setOnClickListener { closeTimer() }
        view.color_button.setOnClickListener { showSelectColorDialog() }
        view.confirm_button.setOnClickListener {
            if (saveCurrentTimer()) {
                requireActivity().showShortToast(getString(R.string.timer_saved))
                closeTimer()
            }
        }

        view.timer_name_editText.doOnTextChanged { name, _, _, _ ->
            viewModel.setTimerName(name.toString())
        }

        view.work_timeInput.observeSeconds(viewLifecycleOwner, viewModel::setWorkSeconds)
        view.rest_timeInput.observeSeconds(viewLifecycleOwner, viewModel::setRestSeconds)
        view.break_timeInput.observeSeconds(viewLifecycleOwner, viewModel::setBreakSeconds)
        view.intervals_numberInput.observeValue(viewLifecycleOwner, viewModel::setIntervalsCount)
        view.cycles_numberInput.observeValue(viewLifecycleOwner, viewModel::setCyclesCount)

        view.start_button.setOnClickListener { startTimer() }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.totalSeconds.observe(viewLifecycleOwner, { timeSeconds ->
            setStartButtonText(timeSeconds)
        })

        viewModel.intervalsCount.observe(
            viewLifecycleOwner, Observer(this::updateExercisesRecycler)
        )

        viewModel.timerColor.observe(viewLifecycleOwner, { color ->
            color_button.backgroundTintList = ColorStateList.valueOf(color)
        })

        val args = TimerFragmentArgs.fromBundle(requireArguments())
        if (args.timer != null)
            setSavedTimerParams(args.timer)
    }

    private fun setSavedTimerParams(timer: Timer) {
        viewModel.setSelectedTimerId(timer.id!!)

        timer_name_editText.setText(timer.name)
        viewModel.setTimerColor(timer.color)

        work_timeInput.setSeconds(timer.workSeconds)
        rest_timeInput.setSeconds(timer.restSeconds)
        intervals_numberInput.setNumber(timer.intervalsCount)
        cycles_numberInput.setNumber(timer.cyclesCount)
        break_timeInput.setSeconds(timer.breakSeconds)

        setExerciseList(timer.exercises)
        viewModel.setSelectedTimerExercises(timer.exercises)

        setStartButtonText(timer.getTotalTimeSeconds())
    }

    private fun updateExercisesRecycler(exercisesCount: Int) {
        while (exerciseAdapter.groupCount > exercisesCount) {
            exerciseAdapter.removeGroupAtAdapterPosition(exerciseAdapter.groupCount - 1)
            viewModel.removeLastExercise()
        }

        while (exerciseAdapter.groupCount < exercisesCount) {
            exerciseAdapter.add(ExerciseItem { position, newText ->
                onExerciseChangeListener(position, newText)
            })
            viewModel.addExercise()
        }
    }

    private fun showSelectColorDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.choose_color)
            cornerRadius(8f)
            colorChooser(
                viewModel.possibleTimerColors,
                initialSelection = viewModel.timerColor.value!!,
                waitForPositiveButton = false
            ) { dialog, color ->
                viewModel.setTimerColor(color)
                dialog.dismiss()
            }
        }
    }

    private fun setExerciseList(exercisesList: List<String>) {
        exerciseAdapter.clear()

        exercisesList.forEach { exercise ->
            val exerciseItem = ExerciseItem(exercise) { position, newText ->
                onExerciseChangeListener(position, newText)
            }

            exerciseAdapter.add(exerciseItem)
        }
    }

    private fun setStartButtonText(timeSeconds: Int) {
        start_button.text = getString(R.string.start_workout_template)
            .replace("x", TimeUtils.secondsToTime(timeSeconds))
    }

    private fun onExerciseChangeListener(position: Int, newText: String) =
        viewModel.changeExerciseAt(position, newText)

    private fun saveCurrentTimer(): Boolean {
        val errorId = viewModel.getErrorId()

        if (errorId != null) {
            requireActivity().showShortToast(getString(errorId))
            return false
        }

        viewModel.saveCurrentTimer()
        return true
    }

    private fun startTimer() {
        if (!saveCurrentTimer()) return
        val timer = viewModel.getCurrentTimerNoId()

        hideKeyboardFrom()
        val action = TimerFragmentDirections
            .actionTimerFragmentToCountdownFragment(timer)
        findNavController().navigate(action)
    }

    private fun closeTimer() {
        hideKeyboardFrom()
        findNavController().popBackStack()
    }

    private fun hideKeyboardFrom() {
        val imm = requireContext()
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}