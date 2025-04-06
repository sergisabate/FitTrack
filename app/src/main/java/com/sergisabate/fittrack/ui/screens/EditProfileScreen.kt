package com.sergisabate.fittrack.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.GenderOption
import com.sergisabate.fittrack.data.viewmodels.ProfileData
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.forms.ProfileForm

@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userPreferencesRepository)
    )
    val weightsViewModel: WeightsViewModel = getWeightsViewModel()

    val currentWeight by weightsViewModel.getCurrentWeight().collectAsStateWithLifecycle()
    val currentProfile by profileViewModel.getProfileData().collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var snackbarMessage by remember { mutableStateOf("") }

    val successMessage = stringResource(R.string.updated_successfully)
    val errorMessage = stringResource(R.string.error_saving_profile_data)

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            snackbarMessage = ""
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        EditProfileContent(
            currentProfile = currentProfile,
            weight = currentWeight,
            onSaveForm = { name, birthdate, gender, weight, height ->
                val profileData = ProfileData(name, birthdate, gender, height)
                profileViewModel.setProfileData(
                    profileData,
                    onSuccess = {
                        weightsViewModel.addOrUpdateLastWeightRecord(
                            weight,
                            onSuccess = {
                                snackbarMessage = successMessage
                            },
                            onError = { e ->
                                Log.e("EditProfileScreen", "Error al guardar los datos del perfil", e)
                                snackbarMessage = errorMessage
                            }
                        )
                    },
                    onError = { e ->
                        Log.e("EditProfileScreen", "Error al guardar los datos del perfil", e)
                        snackbarMessage = errorMessage
                    }
                )
            },
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        )
    }
}

@Composable
fun EditProfileContent(
    currentProfile: ProfileData?,
    weight: Float?,
    onSaveForm: (String, Long, GenderOption, Float, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileForm(
            profile = currentProfile,
            weightData = weight,
            onSubmit = onSaveForm
        )
    }
}
