package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object TemperatureConverter : Converter(CategoryType.TEMPERATURE) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "\u2103" -> originalValue
        "\u2109" -> (originalValue - 32) * 5 / 9
        "k" -> originalValue - 273.15
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "\u2103" -> valueInSI
        "\u2109" -> (valueInSI * 9 / 5) + 32
        "k" -> valueInSI + 273.15
        else -> 0.0
    }
}