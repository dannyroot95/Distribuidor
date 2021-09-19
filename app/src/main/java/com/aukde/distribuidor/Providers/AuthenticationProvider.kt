package com.aukde.distribuidor.Providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class AuthenticationProvider {

    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(email: String, password: String): Task<AuthResult?> {
        return mAuth.createUserWithEmailAndPassword(email, password)
    }

    fun login(email: String, password: String): Task<AuthResult?> {
        return mAuth.signInWithEmailAndPassword(email, password)
    }

    fun logout() {
        mAuth.signOut()
    }

    fun getId(): String {
        return Objects.requireNonNull(mAuth.currentUser)!!.uid
    }

    fun existSession(): Boolean {
        var exist = false
        if (mAuth.currentUser != null) {
            exist = true
        }
        return exist
    }

}