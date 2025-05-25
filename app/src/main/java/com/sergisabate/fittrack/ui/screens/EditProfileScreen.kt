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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getProfileViewModel
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileData
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.forms.ProfileForm

@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = getProfileViewModel()
    val weightsViewModel: WeightsViewModel = getWeightsViewModel()

    val currentWeight by weightsViewModel.getCurrentWeight().collectAsStateWithLifecycle()
    val currentProfile by profileViewModel.getProfileData().collectAsStateWithLifecycle()

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

            ProfileForm(
                profile = currentProfile,
                weightData = currentWeight,
                onSubmit = { name, birthdate, gender, weight, height ->
                    val profileData = ProfileData(name, birthdate, gender, height)
                    profileViewModel.setProfileData(
                        profileData,
                        onSuccess = {
                            weightsViewModel.addOrUpdateLastWeightRecord(
                                weight,
                                onSuccess = {
                                    Toast.makeText(context, context.getString(R.string.profile_successfully_updated), Toast.LENGTH_SHORT).show()
                                },
                                onError = { e ->
                                    Toast.makeText(context, context.getString(R.string.error_saving_profile_data), Toast.LENGTH_SHORT).show()
                                    Log.e("EditProfileScreen", context.getString(R.string.error_saving_profile_data), e)
                                }
                            )
                        },
                        onError = { e ->
                            Toast.makeText(context, context.getString(R.string.error_saving_profile_data), Toast.LENGTH_SHORT).show()
                            Log.e("EditProfileScreen", context.getString(R.string.error_saving_profile_data), e)
                        }
                    )
                }
            )

            Spacer(modifier = Modifier)
        }
    }
}
