package com.example.unipiaudiostories.data.auth

import com.example.unipiaudiostories.data.firebase.FirebaseService
import com.example.unipiaudiostories.data.model.User
import com.example.unipiaudiostories.data.model.UserStats
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * AuthenticationManager handles user authentication tasks such as sign in, registration, and sign out.
 *
 * It leverages the [FirebaseService] to perform authentication and user data operations.
 */
class AuthenticationManager {

    /**
     * Signs in a user with the provided email and password.
     *
     * This method calls [FirebaseService.signInWithEmail] to authenticate the user.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return The [FirebaseUser] on successful sign in, or `null` if sign in fails.
     */
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return FirebaseService.signInWithEmail(email, password)
    }

    /**
     * Registers a new user account with the provided username, email, and password.
     *
     * This method creates a new user using Firebase Authentication and then stores the user details
     * (including an empty [UserStats] object) in Firestore via [FirebaseService.createUserInFirestore].
     *
     * @param username The user's name.
     * @param email The user's email address.
     * @param password The user's password.
     * @return The [FirebaseUser] on successful registration, or `null` if registration fails.
     */
     suspend fun register(username: String, email: String, password: String): FirebaseUser? {
        val user = FirebaseService.auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = user.user
        firebaseUser?.let {
            // Create User object and save it to Firestore
            val newUser = User(
                name = username,
                email = email,
                stats = UserStats()
            )
            FirebaseService.createUserInFirestore(newUser)
        }
        return firebaseUser
     }

    /**
     * Signs out the currently authenticated user.
     * This method delegates the sign out operation to [FirebaseService.signOut].
     */
    fun signOut() {
        FirebaseService.signOut()
    }

    /**
     * Retrieves the currently authenticated user.
     * @return The current [FirebaseUser] if a user is signed in, or `null` otherwise.
     */
    fun getCurrentUser(): FirebaseUser? {
        return FirebaseService.getCurrentUser()
    }
}