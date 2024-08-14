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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.data.repositories.ProfileData
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModelFactory
import com.sergisabate.fittrack.ui.forms.ProfileForm

@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userPreferencesRepository)
    )

    val currentProfile by profileViewModel.getProfileData().collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        EditProfileContent(
            currentProfile = currentProfile,
            onSaveForm = { name, age, weight, height ->
                val profileData = ProfileData(name, age, weight, height)
                profileViewModel.setProfileData(
                    profileData,
                    onSuccess = {
                        snackbarMessage = "Actualizado con éxito"
                        showSnackbar = true
                    },
                    onError = { e ->
                        Log.e("EditProfileScreen", "Error al guardar los datos del perfil", e)
                        snackbarMessage = "Error al actualizar"
                        showSnackbar = true
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
    onSaveForm: (String, Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileForm(
            profile = currentProfile,
            onSubmit = onSaveForm
        )
    }
}
