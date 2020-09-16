package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object VolumeConverter : Converter(CategoryType.VOLUME) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "m\u00B3" -> originalValue
        "cm\u00B3" -> originalValue / 1e+6
        "km\u00B3" -> originalValue * 1e+9
        "l" -> originalValue / 1000
        "ml" -> originalValue / 1e+6
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "m\u00B3" -> valueInSI
        "cm\u00B3" -> valueInSI * 1e+6
        "km\u00B3" -> valueInSI / 1e+9
        "l" -> valueInSI * 1000
        "ml" -> valueInSI * 1e+6
        else -> 0.0
    }
}