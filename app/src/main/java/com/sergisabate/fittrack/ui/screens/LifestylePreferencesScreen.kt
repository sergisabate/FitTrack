package com.sergisabate.fittrack.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.LifestyleData
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.ui.forms.LifestyleForm

@Composable
fun LifestylePreferencesScreen() {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = getProfileViewModel()
    val currentLifestyleData by profileViewModel.getLifestyleData().collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier)

            LifestyleForm(
                lifestyleData = currentLifestyleData,
                onSubmit = { activityLevel, calorieGoal, macroDistribution ->
                    val lifestyleData = LifestyleData(activityLevel, calorieGoal, macroDistribution)
                    profileViewModel.setLifestyleData(
                        lifestyleData = lifestyleData,
                        onSuccess = {
                            Toast.makeText(context, context.getString(R.string.lifestyle_updated), Toast.LENGTH_SHORT).show()
                        },
                        onError = { e ->
                            Toast.makeText(context, context.getString(R.string.error_saving_lifestyle_data), Toast.LENGTH_SHORT).show()
                            Log.e("LifestylePreferencesScreen", context.getString(R.string.error_saving_lifestyle_data), e)
                        }
                    )
                }
            )

            Spacer(modifier = Modifier)
        }
    }
}
