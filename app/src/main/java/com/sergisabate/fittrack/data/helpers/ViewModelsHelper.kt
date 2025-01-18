package com.sergisabate.fittrack.data.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.data.repositories.ExerciseRoutinesRepository
import com.sergisabate.fittrack.data.repositories.MenusRepository
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.repositories.WeightsRepository
import com.sergisabate.fittrack.data.viewmodels.ExerciseRoutinesViewModel
import com.sergisabate.fittrack.data.viewmodels.ExerciseRoutinesViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.MenusViewModel
import com.sergisabate.fittrack.data.viewmodels.MenusViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModelFactory

@Composable
fun getExerciseRoutinesViewModel(): ExerciseRoutinesViewModel {
    val context = LocalContext.current
    val exerciseRoutinesRepository = remember { ExerciseRoutinesRepository.getInstance(context) }
    return viewModel(factory = ExerciseRoutinesViewModelFactory(exerciseRoutinesRepository))
}

@Composable
fun getMenusViewModel(): MenusViewModel {
    val context = LocalContext.current
    val menusRepository = remember { MenusRepository.getInstance(context) }
    return viewModel(factory = MenusViewModelFactory(menusRepository))
}

@Composable
fun getPreferencesViewModel(): PreferencesViewModel {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    return viewModel(factory = PreferencesViewModelFactory(userPreferencesRepository))
}

@Composable
fun getProfileViewModel(): ProfileViewModel {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    return viewModel(factory = ProfileViewModelFactory(userPreferencesRepository))
}

@Composable
fun getWeightsViewModel(): WeightsViewModel {
    val context = LocalContext.current
    val weightsRepository = remember { WeightsRepository.getInstance(context) }
    return viewModel(factory = WeightsViewModelFactory(weightsRepository))
}
