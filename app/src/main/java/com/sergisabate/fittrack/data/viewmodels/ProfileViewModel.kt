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

data class LifestyleData(
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val calorieGoal: CalorieGoal = CalorieGoal.MAINTENANCE,
    val macroDistribution: MacroDistribution = MacroDistribution.LOWER_CARB
)

enum class GenderOption(val value: Int, val displayName: Int) {
    None(0, 0),
    Male(1, R.string.male),
    Female(2, R.string.female),
    Other(3, R.string.other)
}

enum class ActivityLevel(val displayName: Int, val multiplier: Float) {
    SEDENTARY(R.string.activity_sedentary, 1.2f),
    LIGHTLY_ACTIVE(R.string.activity_lightly_active, 1.375f),
    MODERATELY_ACTIVE(R.string.activity_moderately_active, 1.55f),
    VERY_ACTIVE(R.string.activity_very_active, 1.725f),
    EXTRA_ACTIVE(R.string.activity_extra_active, 1.9f),
    ATHLETE(R.string.activity_athlete, 2.3f)
}

enum class CalorieGoal(val displayName: Int, val offset: Int) {
    CUTTING(R.string.goal_cutting, -500),
    MAINTENANCE(R.string.goal_maintenance, 0),
    BULKING(R.string.goal_bulking, 500)
}

enum class MacroDistribution(val displayName: Int, val proteinPct: Float, val fatPct: Float, val carbPct: Float) {
    LOWER_CARB(R.string.macro_lower_carb, 0.40f, 0.40f, 0.20f),
    MODERATE_CARB(R.string.macro_moderate_carb, 0.30f, 0.35f, 0.35f),
    HIGHER_CARB(R.string.macro_higher_carb, 0.30f, 0.20f, 0.50f)
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

    fun setActivityLevel(activityLevel: ActivityLevel) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setActivityLevel(activityLevel.ordinal)
            } catch (_: Exception) {}
        }
    }

    fun setCalorieGoal(calorieGoal: CalorieGoal) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setCalorieGoal(calorieGoal.ordinal)
            } catch (_: Exception) {}
        }
    }

    fun setMacroDistribution(macroDistribution: MacroDistribution) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setMacroDistribution(macroDistribution.ordinal)
            } catch (_: Exception) {}

        }
    }

    fun getLifestyleData(): StateFlow<LifestyleData> {
        val lifestyle = LifestyleData(
            activityLevel = ActivityLevel.entries[userPreferencesRepository.activityLevelFlow.value],
            calorieGoal = CalorieGoal.entries[userPreferencesRepository.calorieGoalFlow.value],
            macroDistribution = MacroDistribution.entries[userPreferencesRepository.macroDistributionFlow.value]
        )
        return MutableStateFlow(lifestyle)
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