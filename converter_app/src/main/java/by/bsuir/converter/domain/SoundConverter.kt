package by.bsuir.converter.domain

import by.bsuir.converter.data.CategoryType

object SoundConverter : Converter(CategoryType.SOUND) {

    override fun convertToSI(originalValue: Double, unitFrom: String) = when (unitFrom) {
        "db" -> originalValue
        "b" -> originalValue * 10
        "np" -> originalValue * 8.685889638
        else -> 0.0
    }

    override fun convertFromSI(valueInSI: Double, unitTo: String) = when (unitTo) {
        "db" -> valueInSI
        "b" -> valueInSI / 10
        "np" -> valueInSI * 0.115129255
        else -> 0.0
    }
}