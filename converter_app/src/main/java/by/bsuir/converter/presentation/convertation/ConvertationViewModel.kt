package by.bsuir.converter.presentation.convertation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.converter.data.CategoryType
import by.bsuir.converter.domain.*

class ConvertationViewModel : ViewModel() {

    private val _convertedValue = MutableLiveData(0.0)
    val convertedValue: LiveData<Double> = _convertedValue

    private val converters = listOf(
        WeightConverter, LengthConverter, SpeedConverter,
        AreaConverter, VolumeConverter, TemperatureConverter,
        DensityConverter, SoundConverter, TimeConverter
    )

    private fun getConverter(categoryType: CategoryType) =
        converters.find { it.categoryType == categoryType }!!

    fun convert(
        originalValue: String, originalUnit: String,
        convertedUnit: String, categoryType: CategoryType
    ) {
        val converter = getConverter(categoryType)

        _convertedValue.value = if (originalValue.isEmpty() || originalValue == ".")
            converter.convert(0.0, originalUnit, convertedUnit)
        else
            converter.convert(originalValue.toDouble(), originalUnit, convertedUnit)
    }
}