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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.ui.theme.LocalThemeViewModel

enum class ThemeOption {
    DarkMode,
    LightMode,
    SystemDefault
}

@Composable
fun ApplicationThemeScreen() {
    val themeViewModel = LocalThemeViewModel.current
    val themeSetting = themeViewModel.getDarkTheme()

    val initialOption = when (themeSetting) {
        true -> ThemeOption.DarkMode
        false -> ThemeOption.LightMode
        null -> ThemeOption.SystemDefault
    }

    var selectedOption by remember { mutableStateOf(initialOption) }

    val onOptionSelected: (ThemeOption) -> Unit = { option ->
        when (option) {
            ThemeOption.DarkMode -> themeViewModel.setDarkTheme(true)
            ThemeOption.LightMode -> themeViewModel.setDarkTheme(false)
            ThemeOption.SystemDefault -> themeViewModel.setDarkTheme(null)
        }
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
fun RadioButtonGroup(
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
