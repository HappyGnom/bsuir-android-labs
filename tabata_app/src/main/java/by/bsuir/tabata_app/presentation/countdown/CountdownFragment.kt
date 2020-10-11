package by.bsuir.tabata_app.presentation.countdown

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.domain.ActionsQueue
import by.bsuir.tabata_app.domain.CountdownService
import by.bsuir.tabata_app.domain.TimeUtils
import by.bsuir.tabata_app.util.IOnBackPressed
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.countdown_fragment.*
import kotlinx.android.synthetic.main.countdown_fragment.view.*

class CountdownFragment : Fragment(), IOnBackPressed {

    private val viewModel: CountdownViewModel by lazy {
        getViewModel { CountdownViewModel() }
    }

    private val countdownBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.processCountdownIntent(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.countdown_fragment, container, false)

        view.back_button.setOnClickListener { showStopTimerDialog() }
        view.forward_button.setOnClickListener { viewModel.nextTimerStep() }
        view.backward_button.setOnClickListener { viewModel.prevTimerStep() }

        view.play_stop_button.setOnClickListener {
            if (viewModel.countdownIsRunning.value!!) viewModel.pauseTimer()
            else viewModel.resumeTimer()
        }

        val args = CountdownFragmentArgs.fromBundle(requireArguments())
        view.timer_name_textView.text = args.timer.name
        val actionsQueue = ActionsQueue(requireContext(), args.timer)

        viewModel.setSelectedTimer(args.timer, actionsQueue)

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.startCountdownSeconds.observe(viewLifecycleOwner, {
            if (it.handled) return@observe
            startCountdownService(it.data!!)

            it.handled = true
        })

        viewModel.stopCountdownEvent.observe(viewLifecycleOwner, {
            if (it.handled) return@observe
            stopCountdownService()

            it.handled = true
        })

        viewModel.currentCycle.observe(viewLifecycleOwner, {
            cycle_number_textView.text = "$it/${viewModel.timer?.cyclesCount}"
        })

        viewModel.currentInterval.observe(viewLifecycleOwner, {
            setCurrentIntervalText(it)
        })

        viewModel.secondsLeft.observe(viewLifecycleOwner, {
            countdown_textView.text = TimeUtils.secondsToTime(it)
            playAlarmSound(it)
        })

        viewModel.currentStage.observe(viewLifecycleOwner, {
            current_stage_textView.text = it
        })

        viewModel.nextExercise.observe(viewLifecycleOwner, {
            this.setNextUp(it)
        })

        viewModel.countdownIsRunning.observe(viewLifecycleOwner, {
            setTimerControlButton(it)
        })

        viewModel.timerFinished.observe(viewLifecycleOwner, {
            finishTimer()
        })

        viewModel.startTimer()
    }

    private fun startCountdownService(seconds: Int) {
        val intent = Intent(requireContext().applicationContext, CountdownService::class.java)
        intent.action = CountdownService.ACTION_START_TIMER
        intent.putExtra(CountdownService.EXTRA_TIMER_SECONDS, seconds)

        requireActivity().startService(intent)
    }

    private fun stopCountdownService() {
        val intent = Intent(requireContext().applicationContext, CountdownService::class.java)
        intent.action = CountdownService.ACTION_STOP_TIMER

        requireActivity().startService(intent)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            countdownBroadcastReceiver, IntentFilter(CountdownService.COUNTDOWN_SERVICE)
        )
        checkCountdownFinishedService()
    }

    private fun checkCountdownFinishedService() {
        val intent = Intent(requireContext().applicationContext, CountdownService::class.java)
        intent.action = CountdownService.ACTION_CHECK_TIMER_FINISHED

        requireActivity().startService(intent)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(countdownBroadcastReceiver)
    }

    override fun onStop() {
        try {
            requireActivity().unregisterReceiver(countdownBroadcastReceiver)
        } catch (e: Exception) {
        }
        super.onStop()
    }

    override fun onDestroy() {
        requireActivity().stopService(
            Intent(requireContext().applicationContext, CountdownService::class.java)
        )
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    private fun setCurrentIntervalText(currentInterval: Int?) = if (currentInterval != null) {
        interval_number_textView.visibility = View.VISIBLE
        interval_number_label.visibility = View.VISIBLE
        interval_number_textView.text = "$currentInterval/${viewModel.timer?.intervalsCount}"
    } else {
        interval_number_textView.visibility = View.INVISIBLE
        interval_number_label.visibility = View.INVISIBLE
    }

    private fun setNextUp(text: String?) = if (text != null) {
        next_exercise_textView.visibility = View.VISIBLE
        next_exercise_textView.text = getString(R.string.next_up_template).replace("y", text)
    } else
        next_exercise_textView.visibility = View.INVISIBLE

    private fun setTimerControlButton(isRunning: Boolean) = if (isRunning)
        play_stop_button.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause)
        )
    else
        play_stop_button.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_play)
        )

    private fun playAlarmSound(secondsLeft: Int) {
        val mp = when {
            secondsLeft == 1 -> MediaPlayer.create(requireContext().applicationContext, R.raw.bell)
            secondsLeft < 6 -> MediaPlayer.create(requireContext().applicationContext, R.raw.blop)
            else -> null
        }

        mp?.start()
    }

    override fun onBackPressed() {
        showStopTimerDialog()
    }

    private fun showStopTimerDialog() {
        viewModel.pauseTimer()

        val icon = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.theme_deleteIcon, icon, true)

        MaterialDialog(requireContext()).show {
            title(R.string.stop_timer)
            message(R.string.stop_timer_question)
            cornerRadius(8f)
            positiveButton(R.string.yes) { stopTimer() }
            negativeButton(R.string.no) { viewModel.resumeTimer() }
            icon(icon.resourceId)
        }
    }

    private fun stopTimer() {
        findNavController().popBackStack()
    }

    private fun finishTimer() {
        val action = CountdownFragmentDirections
            .actionCountdownFragmentToCongratulationFragment()
        findNavController().navigate(action)
    }
}