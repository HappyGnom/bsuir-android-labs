package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object AreaConverter : Converter(CategoryType.AREA) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "m\u00B2" -> originalValue
        "cm\u00B2" -> originalValue / 10000
        "km\u00B2" -> originalValue * 1e+6
        "in\u00B2" -> originalValue * 1.55e+9
        "ft\u00B2" -> originalValue * 1.076e+7
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "m\u00B2" -> valueInSI
        "cm\u00B2" -> valueInSI * 10000
        "km\u00B2" -> valueInSI / 1e+6
        "in\u00B2" -> valueInSI * 1550
        "ft\u00B2" -> valueInSI * 10.764
        else -> 0.0
    }
}