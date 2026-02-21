package pl.gocards.ui.auth

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import pl.gocards.R

/**
 * @author Grzegorz Ziemski
 */
class AuthLauncher(
    val activity: ComponentActivity
) {
    val token = mutableStateOf<String?>(null)

    init {
        getToken()
    }

    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private val providers = arrayListOf(
        //AuthUI.IdpConfig.EmailBuilder().build(),
        //AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Log.i("AuthLauncher", user.toString())
            getToken()
        } else {
            Log.i("AuthLauncher", result.toString())
        }
    }

    fun launch() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_App)
            .build()

        signInLauncher.launch(signInIntent)
    }

    fun getToken() {
        val user = FirebaseAuth.getInstance().currentUser
        token.value = null
        user?.getIdToken(true)?.addOnCompleteListener {
            token.value = it.result.token
        }
    }

    fun logOut() {
        AuthUI.getInstance().signOut(activity).addOnCompleteListener {
            getToken()
        }
    }
}