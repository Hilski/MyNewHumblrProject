package com.example.mynewhumblr.ui.profile_fragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynewhumblr.data.models.MeResponse
import com.example.mynewhumblr.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private var userProfile: MeResponse? = null
    private val _userProfileStateFlow = MutableStateFlow(userProfile)
    val userProfileStateFlow = _userProfileStateFlow

    fun getCurrentUserProfile(){
        viewModelScope.launch {
            runCatching {
                userProfile = repository.getUserProfile()
            }.onSuccess {
                _userProfileStateFlow.value = userProfile
            }.onFailure {
                Timber.tag("error").d("VM error: %s", it.message.toString())
            }
        }
    }
}