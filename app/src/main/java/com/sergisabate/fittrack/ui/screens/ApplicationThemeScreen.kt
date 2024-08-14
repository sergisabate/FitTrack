package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    val initialOption = remember(themeSetting) { enumValues<ThemeOption>().getOrElse(themeSetting) { ThemeOption.SystemDefault } }
    var selectedOption by remember { mutableStateOf(initialOption) }

    val onOptionSelected: (ThemeOption) -> Unit = { option ->
        themeViewModel.setDarkTheme(option.value)
        selectedOption = option
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = 6.dp, horizontal = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            RadioButtonGroup(
                options = ThemeOption.entries.toTypedArray(),
                selectedOption = selectedOption,
                onOptionSelected = onOptionSelected
            )
        }
    }
}

@Composable
private fun RadioButtonGroup(
    options: Array<ThemeOption>,
    selectedOption: ThemeOption,
    onOptionSelected: (ThemeOption) -> Unit
) {
    options.forEach { option ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOptionSelected(option) }
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (option == selectedOption),
                onClick = { onOptionSelected(option) }
            )
            Text(
                text = option.name,
                modifier = Modifier.padding(start = 7.dp)
            )
        }
    }
}
