package by.bsuir.tabata_app.presentation.congratulation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.bsuir.architecture.viewmodel.getViewModel
import by.bsuir.tabata_app.R
import kotlinx.android.synthetic.main.congratulation_fragment.*
import kotlinx.android.synthetic.main.congratulation_fragment.view.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class CongratulationFragment : Fragment() {

    private val viewModel: CongratulationViewModel by lazy {
        getViewModel { CongratulationViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.congratulation_fragment, container, false)

        view.finish_button.setOnClickListener { finishTimer() }

        view.konfettiView.post {
            konfettiView.build()
                .addColors(viewModel.confettiColors)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 4.5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square, Shape.Circle)
                .addSizes(Size(12))
                .setPosition(-50f, konfettiView.width + 50f, -70f, -70f)
                .streamFor(260, 5000L)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun finishTimer() {
        findNavController().popBackStack()
    }
}