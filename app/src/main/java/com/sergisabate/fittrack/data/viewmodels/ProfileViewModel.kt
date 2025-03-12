package com.sergisabate.fittrack.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileData(
    val name: String = "",
    val birthDate: Long = 0L,
    val gender: GenderOption = GenderOption.None,
    val height: Int = 0,
)

enum class GenderOption(val value: Int, val displayName: Int) {
    None(0, 0),
    Male(1, R.string.male),
    Female(2, R.string.female),
    Other(3, R.string.other)
}

class ProfileViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    fun setProfileData(
        profileData: ProfileData,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setName(profileData.name)
                userPreferencesRepository.setBirthDate(profileData.birthDate)
                userPreferencesRepository.setGender(profileData.gender.value)
                userPreferencesRepository.setHeight(profileData.height)
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    fun getProfileData(): StateFlow<ProfileData> {
        val profile = ProfileData(
            name = userPreferencesRepository.nameFlow.value,
            birthDate = userPreferencesRepository.birthDateFlow.value,
            gender = GenderOption.entries[userPreferencesRepository.genderFlow.value],
            height = userPreferencesRepository.heightFlow.value
        )
        return MutableStateFlow(profile)
    }
}

class ProfileViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return ProfileViewModel(userPreferencesRepository) as T
    }
}