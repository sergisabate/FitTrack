package com.sergisabate.fittrack.data.helpers

import com.sergisabate.fittrack.data.viewmodels.ActivityLevel
import com.sergisabate.fittrack.data.viewmodels.CalorieGoal
import com.sergisabate.fittrack.data.viewmodels.GenderOption
import com.sergisabate.fittrack.data.viewmodels.MacroDistribution
import java.time.LocalDate
import java.time.Period

data class FitTrackRecommendations(
    val proteinGrams: Int,
    val carbGrams: Int,
    val fatGrams: Int,
    val minimumWaterIntakeMl: Int,
    val recommendedWaterIntakeMl: Int,
    val minimumSleepHours: Int,
    val maximumSleepHours: Int
)

fun calculateRecommendations(
    birthDateEpochDay: Long,
    gender: GenderOption,
    weightKg: Float,
    heightCm: Int,
    activityLevel: ActivityLevel,
    calorieGoal: CalorieGoal,
    macroDistribution: MacroDistribution
): FitTrackRecommendations {
    val age = calculateAge(birthDateEpochDay)
    val bmr = calculateAverageBMR(gender, weightKg, heightCm, age)
    val tdee = bmr * activityLevel.multiplier + calorieGoal.offset

    val proteinCalories = tdee * macroDistribution.proteinPct
    val fatCalories = tdee * macroDistribution.fatPct
    val carbCalories = tdee * macroDistribution.carbPct

    val minWaterIntake = ((weightKg * 30) + tdee) / 2
    val recommendedWaterIntake = ((weightKg * 35) + tdee) / 2

    val (minSleep, maxSleep) = calculateSleepHours(age, gender, activityLevel, calorieGoal)

    return FitTrackRecommendations(
        proteinGrams = (proteinCalories / 4).toInt(),
        fatGrams = (fatCalories / 9).toInt(),
        carbGrams = (carbCalories / 4).toInt(),
        minimumWaterIntakeMl = minWaterIntake.toInt(),
        recommendedWaterIntakeMl = recommendedWaterIntake.toInt(),
        minimumSleepHours = (minSleep * 60).toInt(),
        maximumSleepHours = (maxSleep * 60).toInt()
    )
}

private fun calculateAge(birthDateEpochDay: Long): Int {
    return Period.between(LocalDate.ofEpochDay(birthDateEpochDay), LocalDate.now()).years
}

private fun calculateAverageBMR(gender: GenderOption, weightKg: Float, heightCm: Int, age: Int): Double {
    val harrisBenedict = if (gender == GenderOption.Female) {
        655.0955 + (9.5634 * weightKg) + (1.8496 * heightCm) - (4.6756 * age)
    } else {
        66.4730 + (13.7516 * weightKg) + (5.0033 * heightCm) - (6.7550 * age)
    }

    val rozaShizgal = if (gender == GenderOption.Female) {
        447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age)
    } else {
        88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age)
    }

    val mifflinStJeor = if (gender == GenderOption.Female) {
        (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
    } else {
        (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
    }

    val katchMcArdle = if (gender == GenderOption.Female) {
        ((0.252 * weightKg + 0.473 * heightCm - 48.3) * 21.6 + 370)
    } else {
        ((0.407 * weightKg + 0.267 * heightCm - 19.2) * 21.6 + 370)
    }

    return (harrisBenedict + rozaShizgal + mifflinStJeor + katchMcArdle) / 4
}

private fun calculateSleepHours(
    age: Int,
    gender: GenderOption,
    activityLevel: ActivityLevel,
    calorieGoal: CalorieGoal
): Pair<Double, Double> {
    var (min, max) = when (age) {
        in 0..12 -> 9.0 to 12.0
        in 13..17 -> 8.0 to 10.0
        in 18..25 -> 7.5 to 9.0
        in 26..64 -> 7.0 to 8.5
        else -> 7.0 to 8.0
    }

    val activityAdjustment = when (activityLevel) {
        ActivityLevel.SEDENTARY -> 0.0 to 0.0
        ActivityLevel.LIGHTLY_ACTIVE -> 0.1 to 0.2
        ActivityLevel.MODERATELY_ACTIVE -> 0.25 to 0.4
        ActivityLevel.VERY_ACTIVE -> 0.4 to 0.6
        ActivityLevel.EXTRA_ACTIVE -> 0.5 to 0.75
        ActivityLevel.ATHLETE -> 0.75 to 1.0
    }

    val goalAdjustment = when (calorieGoal) {
        CalorieGoal.CUTTING -> 0.1 to 0.25
        CalorieGoal.MAINTENANCE -> 0.0 to 0.0
        CalorieGoal.BULKING -> 0.25 to 0.5
    }

    val genderAdjustment = if (gender == GenderOption.Female) 0.15 to 0.25 else 0.0 to 0.0

    min += activityAdjustment.first + goalAdjustment.first + genderAdjustment.first
    max += activityAdjustment.second + goalAdjustment.second + genderAdjustment.second

    return min.coerceIn(6.0, 10.0) to max.coerceIn(6.5, 12.0)
}
