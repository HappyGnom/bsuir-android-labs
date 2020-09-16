package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object DensityConverter : Converter(CategoryType.DENSITY) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "g/m\u00B3" -> originalValue
        "kg/m\u00B3" -> originalValue * 1000
        "g/cm\u00B3" -> originalValue * 1e+6
        "kg/cm\u00B3" -> originalValue * 1e+9
        "g/l" -> originalValue * 1000
        "kg/l" -> originalValue * 1e+6
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "g/m\u00B3" -> valueInSI
        "kg/m\u00B3" -> valueInSI / 1000
        "g/cm\u00B3" -> valueInSI / 1e+6
        "kg/cm\u00B3" -> valueInSI / 1e+9
        "g/l" -> valueInSI / 1000
        "kg/l" -> valueInSI / 1e+6
        else -> 0.0
    }
}