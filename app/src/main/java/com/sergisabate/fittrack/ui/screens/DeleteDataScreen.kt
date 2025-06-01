package com.sergisabate.fittrack.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.sergisabate.fittrack.MainActivity
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.PREF_NAME
import com.sergisabate.fittrack.data.repositories.exerciseRoutinesDataStore
import com.sergisabate.fittrack.data.repositories.menusDataStore
import com.sergisabate.fittrack.data.repositories.userPreferencesDataStore
import com.sergisabate.fittrack.data.repositories.weightsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DeleteDataScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var confirmationText by remember { mutableStateOf("") }
    val isInputValid = confirmationText == stringResource(R.string.i_want_to_delete_my_data)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier)

            Text(
                text = stringResource(R.string.this_action_is_irreversible),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = stringResource(R.string.delete_data_warning),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.i_want_to_delete_my_data),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmationText,
                onValueChange = { confirmationText = it },
                label = {
                    Text(
                        text = stringResource(R.string.confirmation_phrase),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleMedium,
                singleLine = true
            )

            Spacer(modifier = Modifier)

            Button(
                onClick = { deleteDataAndRestart(scope, context) },
                enabled = isInputValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(stringResource(R.string.delete_data))
            }

            Spacer(modifier = Modifier)
        }
    }
}

private fun deleteDataAndRestart(scope: CoroutineScope, context: Context) {
    scope.launch {
        context.userPreferencesDataStore.edit { it.clear() }
        context.exerciseRoutinesDataStore.edit { it.clear() }
        context.menusDataStore.edit { it.clear() }
        context.weightsDataStore.edit { it.clear() }

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            ?.edit()?.clear()?.apply()

        context.filesDir?.listFiles()?.forEach { it.deleteRecursively() }

        val activity = context as ComponentActivity

        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        activity.startActivity(intent)
        activity.finish()
    }
}
