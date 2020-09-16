package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object SpeedConverter : Converter(CategoryType.SPEED) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "m/s" -> originalValue
        "km/h" -> originalValue / 3.6
        "mile/h" -> originalValue / 2.237
        "ft/s" -> originalValue / 3.281
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "m/s" -> valueInSI
        "km/h" -> valueInSI * 3.6
        "mile/h" -> valueInSI * 2.237
        "ft/s" -> valueInSI * 3.281
        else -> 0.0
    }
}