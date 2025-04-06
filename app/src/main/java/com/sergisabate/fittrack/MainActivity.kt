package com.sergisabate.fittrack

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.sergisabate.fittrack.data.helpers.applyLocale
import com.sergisabate.fittrack.data.helpers.getProfileViewModel
import com.sergisabate.fittrack.data.helpers.getSavedLocale
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileData
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.screens.MainScreen
import com.sergisabate.fittrack.ui.screens.OnboardingScreen
import com.sergisabate.fittrack.ui.theme.FitTrackTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        var context = newBase
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val locale = getSavedLocale(newBase)
            context = applyLocale(newBase, locale)
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val savedLocale = getSavedLocale(this)
            Locale.setDefault(savedLocale)
        }

        // Mantiene la splash screen visible hasta que se complete la inicialización
        var keepSplashScreenVisible = true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreenVisible }

        setContent {
            FitTrackTheme {
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
                        MainScreen(modifier = Modifier.consumeWindowInsets(innerPadding))
                    } else {
                        // Obtiene el ViewModel de perfil
                        val weightsViewModel: WeightsViewModel = getWeightsViewModel()

                        OnboardingScreen(
                            modifier = Modifier.consumeWindowInsets(innerPadding)
                        ) { name, birthdate, gender, weight, height ->
                            scope.launch {
                                // Guarda los datos del usuario y actualiza el estado
                                val profileData = ProfileData(name, birthdate, gender, height)

                                profileViewModel.setProfileData(
                                    profileData,
                                    onSuccess = {
                                        weightsViewModel.addOrUpdateLastWeightRecord(
                                            weight,
                                            onSuccess = {
                                                isOnboardingComplete = true
                                            },
                                            onError = { e ->
                                                Log.e("MainActivity", "Error al guardar los datos del perfil", e)
                                                showErrorSnackbar = true
                                            }
                                        )
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
}
