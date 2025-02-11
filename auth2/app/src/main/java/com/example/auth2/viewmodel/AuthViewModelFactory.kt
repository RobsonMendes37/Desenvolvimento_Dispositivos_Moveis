package com.example.auth2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.auth2.data.AuthRepository

class AuthViewModelFactory(private val repostory: AuthRepository): ViewModelProvider.Factory{
    override  fun <T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)){
            return AuthViewModel(repostory) as T
        }
        throw IllegalArgumentException("Erro")
    }
}