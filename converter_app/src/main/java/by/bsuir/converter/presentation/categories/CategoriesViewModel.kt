package by.bsuir.converter.presentation.categories

import androidx.lifecycle.ViewModel
import by.bsuir.converter.R
import by.bsuir.converter.data.Category
import by.bsuir.converter.data.CategoryType

class CategoriesViewModel : ViewModel() {

    val categories = listOf(
        Category(R.string.weight, R.drawable.ic_weight, R.array.weight_units, CategoryType.WEIGHT),
        Category(R.string.length, R.drawable.ic_ruler, R.array.length_units, CategoryType.LENGTH),
        Category(R.string.speed, R.drawable.ic_speed, R.array.speed_units, CategoryType.SPEED),
        Category(R.string.area, R.drawable.ic_area, R.array.area_units, CategoryType.AREA),
        Category(R.string.volume, R.drawable.ic_volume, R.array.volume_units, CategoryType.VOLUME),
        Category(R.string.temperature, R.drawable.ic_temperature, R.array.temperature_units, CategoryType.TEMPERATURE),
        Category(R.string.density, R.drawable.ic_density, R.array.density_units, CategoryType.DENSITY),
        Category(R.string.sound, R.drawable.ic_sound, R.array.sound_units, CategoryType.SOUND),
        Category(R.string.time, R.drawable.ic_time, R.array.time_units, CategoryType.TIME)
    )
}