package com.example.twinmindassignment

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.example.twinmindassignment.ui.theme.LoginScreen
import com.example.twinmindassignment.ui.theme.HomeScreen

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "Firebase login success: ${firebaseAuth.currentUser?.displayName}")
                        setContent { HomeScreen(firebaseAuth.currentUser) }
                    } else {
                        Log.e("Login", "Firebase login failed", task.exception)
                    }
                }
        } catch (e: ApiException) {
            Log.e("Login", "Google sign-in failed", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("162472105855-18pn35v2fdl8ioae35b13it49eq7m1mt.apps.googleusercontent.com") // 🔁 Replace with Firebase web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            if (firebaseAuth.currentUser == null) {
                LoginScreen {
                    val signInIntent = googleSignInClient.signInIntent
                    signInLauncher.launch(signInIntent)
                }
            } else {
                HomeScreen(firebaseAuth.currentUser)
            }
        }
    }
}
