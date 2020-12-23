package by.bsuir.battleships_app.presentation.results

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.data.UserData
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ResultsViewModel : ViewModel() {

    val user = FirebaseAuth.getInstance().currentUser
    private val database = Firebase.database.reference

    private val _yourAvatar = MutableLiveData<Drawable>()
    val yourAvatar: LiveData<Drawable> = _yourAvatar

    private val _enemyNickname = MutableLiveData<String>()
    val enemyNickname: LiveData<String> = _enemyNickname

    private val _enemyAvatar = MutableLiveData<Drawable>()
    val enemyAvatar: LiveData<Drawable> = _enemyAvatar

    fun loadPlayersData(context: Context, firstPlayer: UserData, secondPlayer: UserData) {
        loadYourAvatar(context)

        if (firstPlayer.id == user!!.uid) {
            _enemyNickname.value = secondPlayer.nickname
            loadEnemyAvatar(context, secondPlayer.avatarUrl)
        } else {
            _enemyNickname.value = firstPlayer.nickname
            loadEnemyAvatar(context, firstPlayer.avatarUrl)
        }
    }

    private fun loadYourAvatar(context: Context) {
        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.octopus)
            .error(R.drawable.octopus)
            .target { drawable ->
                _yourAvatar.postValue(drawable)
            }

        if (user?.photoUrl != null)
            request.data(user.photoUrl)
        else
            request.data(R.drawable.octopus)

        ImageLoader(context).enqueue(request.build())
    }

    private fun loadEnemyAvatar(context: Context, photoUri: String?) {
        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.octopus)
            .error(R.drawable.octopus)
            .target { drawable ->
                _enemyAvatar.postValue(drawable)
            }

        if (!photoUri.isNullOrBlank() && photoUri != "null")
            request.data(photoUri)
        else
            request.data(R.drawable.octopus)

        ImageLoader(context).enqueue(request.build())
    }
}