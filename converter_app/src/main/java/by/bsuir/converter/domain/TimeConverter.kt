package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object TimeConverter : Converter(CategoryType.TIME) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "s" -> originalValue
        "ms" -> originalValue / 1000
        "min" -> originalValue * 60
        "h" -> originalValue * 3600
        "day(s)" -> originalValue * 86400
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "s" -> valueInSI
        "ms" -> valueInSI * 1000
        "min" -> valueInSI / 60
        "h" -> valueInSI / 3600
        "day(s)" -> valueInSI / 86400
        else -> 0.0
    }
}