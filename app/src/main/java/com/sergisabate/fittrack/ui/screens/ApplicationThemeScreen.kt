package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.ThemeViewModel
import com.sergisabate.fittrack.data.viewmodels.ThemeViewModelFactory
import com.sergisabate.fittrack.ui.theme.ThemeOption

@Composable
fun ApplicationThemeScreen() {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModelFactory(userPreferencesRepository)
    )

    val themeSetting by themeViewModel.getDarkTheme().collectAsState()
    val initialOption = enumValues<ThemeOption>().getOrElse(themeSetting) { ThemeOption.SystemDefault }

    var selectedOption by remember { mutableStateOf(initialOption) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = 6.dp, horizontal = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ThemeOption.entries.forEach { option ->
                ThemeOptionRow(
                    option = option,
                    isSelected = option == selectedOption,
                    onOptionSelected = {
                        themeViewModel.setDarkTheme(option.value)
                        selectedOption = option
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    option: ThemeOption,
    isSelected: Boolean,
    onOptionSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOptionSelected() }
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onOptionSelected() }
        )
        Text(
            text = when (option) {
                ThemeOption.DarkMode -> stringResource(R.string.dark_mode)
                ThemeOption.LightMode -> stringResource(R.string.light_mode)
                ThemeOption.SystemDefault -> stringResource(R.string.system_default)
            },
            modifier = Modifier.padding(start = 7.dp)
        )
    }
}
