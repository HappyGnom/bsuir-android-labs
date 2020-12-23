package by.bsuir.battleships_app.presentation.menu

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth

class MenuViewModel : ViewModel() {

    private val user = FirebaseAuth.getInstance().currentUser

    private val _username = MutableLiveData("Player")
    val username: LiveData<String> = _username

    private val _avatar = MutableLiveData<Drawable>()
    val avatar: LiveData<Drawable> = _avatar

    fun requestUserInfo(context: Context) {
        _username.postValue(user?.displayName)

        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.octopus)
            .error(R.drawable.octopus)
            .target{
                _avatar.postValue(it)
            }

        if (user?.photoUrl != null)
            request.data(user.photoUrl)
        else
            request.data(R.drawable.octopus)

        ImageLoader(context).enqueue(request.build())
    }
}