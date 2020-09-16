package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object WeightConverter : Converter(CategoryType.WEIGHT) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "kg" -> originalValue
        "ton" -> originalValue * 1000
        "g" -> originalValue / 1000
        "mg" -> originalValue / 1000000
        "lb" -> originalValue * 0.45359237
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "kg" -> valueInSI
        "ton" -> valueInSI / 1000
        "g" -> valueInSI * 1000
        "mg" -> valueInSI * 1000000
        "lb" -> valueInSI / 0.45359237
        else -> 0.0
    }
}