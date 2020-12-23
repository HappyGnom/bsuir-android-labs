package by.bsuir.battleships_app.presentation.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.battleships_app.R
import by.bsuir.battleships_app.data.Stats
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileViewModel : ViewModel() {

    val user = FirebaseAuth.getInstance().currentUser
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val database = Firebase.database.reference

    companion object {
        private const val ACCOUNT_DB_REF = "ACCOUNT"
        private const val STATS_DB_REF = "STATS"
    }

    private val _username = MutableLiveData("Player")
    val username: LiveData<String> = _username

    private val _avatar = MutableLiveData<Drawable>()
    val avatar: LiveData<Drawable> = _avatar

    private val _userStats = MutableLiveData(Stats())
    val userStats: LiveData<Stats> = _userStats

    fun requestUserInfo(context: Context) {
        _username.postValue(user?.displayName)
        loadAvatar(context, user?.photoUrl)
        loadStats()
    }

    fun updateUsername(newName: String) {
        user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(newName).build())
            ?.addOnSuccessListener { _username.postValue(newName) }
    }

    fun updateAvatar(context: Context, avatarUri: Uri) {
        val ref = firebaseStorage.child("images/" + UUID.randomUUID().toString())

        ref.putFile(avatarUri).addOnSuccessListener { uploadedImage ->
            uploadedImage.storage.downloadUrl.addOnSuccessListener { fileUri ->
                user?.updateProfile(UserProfileChangeRequest.Builder().setPhotoUri(fileUri).build())
                    ?.addOnSuccessListener {
                        loadAvatar(context, fileUri)
                    }
            }
        }
    }

    private fun loadAvatar(context: Context, photoUri: Uri?) {
        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.octopus)
            .error(R.drawable.octopus)
            .target { drawable ->
                _avatar.postValue(drawable)
            }

        if (photoUri != null)
            request.data(photoUri)
        else
            request.data(R.drawable.octopus)

        ImageLoader(context).enqueue(request.build())
    }

    private fun loadStats() {
        val accountDbRef = database.child(ACCOUNT_DB_REF).child(user!!.uid)
        val statsDbRef = accountDbRef.child(STATS_DB_REF)

        statsDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return
                _userStats.postValue(snapshot.getValue(Stats::class.java))
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}