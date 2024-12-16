package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.ui.forms.ProfileForm
import kotlinx.coroutines.delay

@Composable
fun TypingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        text.forEach { char ->
            delay(40)
            displayedText += char
        }
    }

    Text(text = displayedText, style = style, color = color, modifier = modifier)
}

@Composable
fun OnboardingScreen(modifier: Modifier, onSubmit: (String, Int, Float, Int) -> Unit) {
    var showWelcome by remember { mutableStateOf(false) }
    var showExcitement by remember { mutableStateOf(false) }
    var showIntroduction by remember { mutableStateOf(false) }
    var showDataAdvise by remember { mutableStateOf(false) }
    var showPrompt by remember { mutableStateOf(false) }
    var skipAnimation by remember { mutableStateOf(false) }

    // Control del orden de aparición de los textos
    LaunchedEffect(Unit) {
        delay(1500)
        showWelcome = true
        delay(2500)
        showExcitement = true
        delay(5000)
        showIntroduction = true
        delay(6500)
        showDataAdvise = true
        delay(5500)
        showPrompt = true
        delay(3500)
        skipAnimation = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!skipAnimation) {
                if (showWelcome) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        TypingText(
                            text = stringResource(R.string.welcome_message),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = modifier.weight(1f)
                        )
                        Button(
                            onClick = { skipAnimation = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(0.dp),
                            modifier = modifier.align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = "Omitir",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
                if (showExcitement) {
                    TypingText(
                        text = stringResource(R.string.excited_message),
                        modifier = modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (showIntroduction) {
                    TypingText(
                        text = stringResource(R.string.claim_data_message),
                        modifier = modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (showDataAdvise) {
                    TypingText(
                        text = stringResource(R.string.local_data_message),
                        modifier = modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (showPrompt) {
                    TypingText(
                        text = stringResource(R.string.complete_form_message),
                        modifier = modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                DisplayAllTexts()
                Spacer(modifier = modifier.height(16.dp))
                ProfileForm(null, null, onSubmit)
            }
        }
    }
}

@Composable
fun DisplayAllTexts() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.welcome_message),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Button(
            onClick = { },
            enabled = false,
            colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Transparent, disabledContentColor = Color.Transparent)
        ) {
            Text(text = "")
        }
    }
    Text(
        text = stringResource(R.string.excited_message),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = stringResource(R.string.claim_data_message),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = stringResource(R.string.local_data_message),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = stringResource(R.string.complete_form_message),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
