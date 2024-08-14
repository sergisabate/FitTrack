package com.sergisabate.fittrack.ui.screens

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat


enum class LanguageOption(val value: String) {
    Español("es"),
    Català("ca"),
    English("en")
}

@Composable
fun LanguageScreen() {
    val context = LocalContext.current
    val currentLanguage = context.resources.configuration.locales[0].language
    val initialOption = LanguageOption.entries.firstOrNull { it.value == currentLanguage } ?: LanguageOption.Español
    var selectedOption by remember { mutableStateOf(initialOption) }

    val onOptionSelected: (LanguageOption) -> Unit = { option ->
        localeSelection(context, option.value)
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
                options = LanguageOption.entries.toTypedArray(),
                selectedOption = selectedOption,
                onOptionSelected = onOptionSelected
            )
        }
    }
}

@Composable
private fun RadioButtonGroup(
    options: Array<LanguageOption>,
    selectedOption: LanguageOption,
    onOptionSelected: (LanguageOption) -> Unit
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

fun localeSelection(context: Context, localeTag: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(localeTag)
    } else {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(localeTag)
        )
    }
}
