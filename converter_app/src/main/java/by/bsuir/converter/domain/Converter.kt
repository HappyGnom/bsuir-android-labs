package by.bsuir.converter.domain

import android.annotation.SuppressLint
import by.bsuir.converter.data.CategoryType

abstract class Converter(val categoryType: CategoryType) {

    @SuppressLint("DefaultLocale")
    fun convert(originalValue: Double, originalUnit: String, convertedUnit: String): Double {
        if (originalUnit == convertedUnit)
            return originalValue

        val valueInSI = convertToSI(originalValue, originalUnit.toLowerCase())
        return convertFromSI(valueInSI, convertedUnit.toLowerCase())
    }

    protected abstract fun convertToSI(originalValue: Double, unitFrom: String): Double

    protected abstract fun convertFromSI(valueInSI: Double, unitTo: String): Double
}