package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object LengthConverter : Converter(CategoryType.LENGTH) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "m" -> originalValue
        "dm" -> originalValue / 10
        "cm" -> originalValue / 100
        "km" -> originalValue * 1000
        "in" -> originalValue / 39.37
        "ft" -> originalValue / 3.281
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "m" -> valueInSI
        "dm" -> valueInSI * 10
        "cm" -> valueInSI * 100
        "km" -> valueInSI / 1000
        "in" -> valueInSI * 39.37
        "ft" -> valueInSI * 3.281
        else -> 0.0
    }
}