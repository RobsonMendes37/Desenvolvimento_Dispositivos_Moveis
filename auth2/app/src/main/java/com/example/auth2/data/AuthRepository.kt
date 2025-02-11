package com.example.auth2.data

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository{
    //fazer login
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    //cadastrar usuário
    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(
        email: String,
        password:String,
        name: String
    ): Boolean{
        return try{
            val result = auth.createUserWithEmailAndPassword(email,password).await() //cria o cara de forma solto
            val uid = result.user?.uid

            //abstrai o usuário, tem que ter um model
            if(uid != null){
                val user = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "created_at" to System.currentTimeMillis()
                )
                firestore.collection("Users")
                    .document(uid).set(user).await()
            }

            true
        } catch (e:Exception){
            Log.e("error", "Erro no cadastro")
            false
        }
    }


    suspend fun loginUser(
      email: String,
      password: String,
    ): Boolean{
        return try {
            auth.signInWithEmailAndPassword(email,password).await()
            true
        }catch(e : Exception){
            Log.e("Error", "Erro no login")
            false
        }
    }


    suspend fun resetPassword(email: String) : Boolean{
        return try{
            auth.sendPasswordResetEmail(email).await()
            true
        }catch (e :Exception){
            Log.e("Error", "Erro em resgatar a senha")
            false
        }
    }


//    suspend fun getUserName(): String?{
//        return try{
//            val uid = auth.currentUser?.uid
//            if(uid != null){
//                val snapshot = firestore.collection("Users")
//                    .document(uid).get().await()
//            }else{
//                null
//            }
//        }catch (e : Exception){
//            Log.e("Error", "Erro ao buscar usuário")
//            null
//        }
//    }


    suspend fun getUserName(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("Users")
                    .document(uid)
                    .get()
                    .await()

                snapshot.getString("name") // Obtém o nome do usuário do Firestore
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Error", "Erro ao buscar usuário: ${e.message}")
            null
        }
    }


    //login facilitado com o google
    fun getGoogleSignClient(context: Context): GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.auth2.R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context,gso )
    }


    suspend fun loginWithGoogle(idToken : String): Boolean{
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken,null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            user?.let{
                val uid = it.uid
                val name = it.displayName?: "usuário"
                val email = it.email ?: ""

                val userRef = firestore.collection("users").document(uid)
                val snapshot = userRef.get().await()

                if(!snapshot.exists()){
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "created_at" to System.currentTimeMillis()
                    )
                    userRef.set(userData).await() //cadastra de não tiver outra conta
                }
            }
            true
        } catch (e : Exception){
            Log.e("Error", "error no login como o google")
            false
        }
    }


    fun logout(){
        auth.signOut()
    }

    fun isuserlogged():Boolean{
        return auth.currentUser != null
    }


}