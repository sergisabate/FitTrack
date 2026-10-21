package com.sergisabate.fittrack.data.helpers

import com.sergisabate.fittrack.data.viewmodels.ActivityLevel
import com.sergisabate.fittrack.data.viewmodels.CalorieGoal
import com.sergisabate.fittrack.data.viewmodels.GenderOption
import com.sergisabate.fittrack.data.viewmodels.MacroDistribution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class NutritionHelperTest {

    private val thirtyYearsOld = LocalDate.now().minusYears(30).toEpochDay()

    private fun recommendations(
        gender: GenderOption = GenderOption.Male,
        activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
        calorieGoal: CalorieGoal = CalorieGoal.MAINTENANCE,
        macroDistribution: MacroDistribution = MacroDistribution.MODERATE_CARB
    ) = calculateRecommendations(thirtyYearsOld, gender, 80f, 180, activityLevel, calorieGoal, macroDistribution)

    @Test
    fun `bulking recommends more of every macro than cutting`() {
        val cutting = recommendations(calorieGoal = CalorieGoal.CUTTING)
        val bulking = recommendations(calorieGoal = CalorieGoal.BULKING)
        assertTrue(bulking.proteinGrams > cutting.proteinGrams)
        assertTrue(bulking.carbGrams > cutting.carbGrams)
        assertTrue(bulking.fatGrams > cutting.fatGrams)
    }

    @Test
    fun `macros add up to the same daily calories`() {
        val r = recommendations()
        val kcalFromMacros = r.proteinGrams * 4 + r.carbGrams * 4 + r.fatGrams * 9
        val kcalMaintenanceRange = 2000..3500
        assertTrue("$kcalFromMacros kcal", kcalFromMacros in kcalMaintenanceRange)
    }

    @Test
    fun `macro split follows the selected distribution`() {
        val r = recommendations(macroDistribution = MacroDistribution.LOWER_CARB)
        assertEquals(2.0, r.proteinGrams.toDouble() / r.carbGrams, 0.02)
    }

    @Test
    fun `higher activity never lowers water intake`() {
        val sedentary = recommendations(activityLevel = ActivityLevel.SEDENTARY)
        val athlete = recommendations(activityLevel = ActivityLevel.ATHLETE)
        assertTrue(athlete.recommendedWaterIntakeMl > sedentary.recommendedWaterIntakeMl)
        assertTrue(sedentary.recommendedWaterIntakeMl > sedentary.minimumWaterIntakeMl)
    }

    @Test
    fun `sleep range stays within clamped bounds`() {
        ActivityLevel.entries.forEach { activity ->
            val r = recommendations(gender = GenderOption.Female, activityLevel = activity, calorieGoal = CalorieGoal.BULKING)
            assertTrue(r.minimumSleepHours in 360..600)
            assertTrue(r.maximumSleepHours in 390..720)
            assertTrue(r.minimumSleepHours <= r.maximumSleepHours)
        }
    }
}
