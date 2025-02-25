package com.example.unipiaudiostories.data.firebase

import android.util.Log
import com.example.unipiaudiostories.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * FirebaseService is a singleton object that provides centralized access to Firebase components.
 *
 * This service ensures that FirebaseAuth and FirebaseFirestore instances are initialized only once
 * and are available globally throughout the application.
 *
 * **Responsibilities:**
 * - Provides the Firebase Firestore instance for database operations.
 * - Provides the Firebase Auth instance for user authentication.
 *
 * **Usage:**
 * - Access the Firestore database via [firestore].
 * - Access Firebase Authentication via [auth].
 */
object FirebaseService {

    /**
     * Lazily initialized Firebase Firestore instance for database operations.
     * Use this instance to interact with Firestore collections and documents.
     */
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    /**
     * Lazily initialized Firebase Authentication instance.
     * Use this instance to handle user authentication (e.g., sign-in and sign-out).
     */
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    /**
     * Signs in a user with email and password.
     *
     * This suspend function uses Firebase Auth's `signInWithEmailAndPassword` method and awaits
     * the result. If successful, the currently authenticated user is returned.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return The authenticated [FirebaseUser], or `null` if sign-in fails.
     */
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            auth.currentUser
        } catch (e: Exception) {
            Log.e("Auth", "Error signing in with email", e)
            null
        }
    }

    /**
     * Signs out the currently authenticated user.
     * This method logs out the user by calling [FirebaseAuth.signOut].
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Retrieves the currently signed-in user.
     * @return The current [FirebaseUser], or `null` if no user is signed in.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Creates a new user document in Firestore.
     *
     * The user data is stored under the "users" collection, using the current user's UID as the document ID.
     *
     * @param user The [User] object representing the user's data.
     * @throws IllegalStateException If no user is signed in.
     */
    suspend fun createUserInFirestore(user: User) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User must be logged in")
        firestore.collection("users").document(uid).set(user).await()
    }

    /**
     * Fetches the currently signed-in user's profile from Firestore.
     *
     * The user's profile is retrieved from the "users" collection using the current user's UID.
     *
     * @return The [User] object representing the user's profile, or `null` if not found.
     * @throws IllegalStateException If no user is signed in.
     */
    suspend fun getUserProfile(): User? {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User must be logged in")
        val document = firestore.collection("users").document(uid).get().await()
        return document.toObject(User::class.java)
    }

}