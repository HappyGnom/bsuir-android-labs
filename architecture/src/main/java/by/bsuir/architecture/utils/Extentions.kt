package by.bsuir.architecture.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Activity.showToast(message: String, length: Int = Toast.LENGTH_LONG) =
    runOnUiThread { Toast.makeText(this, message, length).show() }

fun Activity.showShortToast(message: String) = showToast(message, Toast.LENGTH_SHORT)

fun Context.showToast(message: String, length: Int = Toast.LENGTH_LONG) {
    CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(this@showToast, message, length).show()
    }
}

fun Context.showShortToast(message: String) = showToast(message, Toast.LENGTH_SHORT)

