package com.sergisabate.fittrack.ui.screens

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.data.helpers.saveLocale
import java.util.Locale

enum class LanguageOption(val value: String, val displayName: String) {
    Spanish("es", "Español"),
    Catalan("ca", "Català"),
    English("en", "English")
}

@Composable
fun LanguageScreen() {
    val context = LocalContext.current
    val currentLanguage = LocalConfiguration.current.locales[0].language
    val initialOption = LanguageOption.entries.firstOrNull { it.value == currentLanguage } ?: LanguageOption.Spanish
    var selectedOption by remember { mutableStateOf(initialOption) }

    val onOptionSelected: (LanguageOption) -> Unit = { option ->
        localeSelection(option.value, context)
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
                text = option.displayName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 7.dp)
            )
        }
    }
}

fun localeSelection(localeTag: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags(localeTag)
    } else {
        val activity = context as ComponentActivity
        saveLocale(activity, Locale.forLanguageTag(localeTag))

        val intent = Intent(activity, activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }
}
