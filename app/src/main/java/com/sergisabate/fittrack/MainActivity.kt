package com.sergisabate.fittrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.data.repositories.ProfileData
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModelFactory
import com.sergisabate.fittrack.ui.screens.MainScreen
import com.sergisabate.fittrack.ui.screens.OnboardingScreen
import com.sergisabate.fittrack.ui.theme.FitTrackTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantiene la splash screen visible hasta que se complete la inicialización
        var keepSplashScreenVisible = true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreenVisible }

        setContent {
            FitTrackTheme {
                // Obtiene el ViewModel de perfil
                val profileViewModel: ProfileViewModel = getProfileViewModel()
                val currentProfileData by profileViewModel.getProfileData().collectAsState()

                // Controla si se ha completado el onboarding
                var isOnboardingComplete by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                // Estado para manejar el Snackbar
                val snackbarHostState = remember { SnackbarHostState() }
                var showErrorSnackbar by remember { mutableStateOf(false) }

                // Desactiva la splash screen cuando lo anterior esté listo
                LaunchedEffect(currentProfileData) {
                    keepSplashScreenVisible = false
                }

                // Mostrar el Snackbar si hay un error
                LaunchedEffect(showErrorSnackbar) {
                    if (showErrorSnackbar) {
                        snackbarHostState.showSnackbar("Error al guardar los datos del perfil")
                        showErrorSnackbar = false
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    // Verifica si se debe mostrar el onboarding o la pantalla principal
                    if (currentProfileData != ProfileData() || isOnboardingComplete) {
                        MainScreen(modifier = Modifier.padding(innerPadding))
                    } else {
                        OnboardingScreen(
                            modifier = Modifier.padding(innerPadding)
                        ) { name, age, weight, height ->
                            scope.launch {
                                // Guarda los datos del usuario y actualiza el estado
                                val profileData = ProfileData(name, age, weight, height)

                                profileViewModel.setProfileData(
                                    profileData,
                                    onSuccess = {
                                        isOnboardingComplete = true
                                    },
                                    onError = { e ->
                                        Log.e("MainActivity", "Error al guardar los datos del perfil", e)
                                        showErrorSnackbar = true
                                    }
                                )
                            }
                        }
                    }
                }

            }
        }
    }

    @Composable
    private fun getProfileViewModel(): ProfileViewModel {
        val context = LocalContext.current
        val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
        return viewModel(factory = ProfileViewModelFactory(userPreferencesRepository))
    }
}
