package com.sergisabate.fittrack.ui.forms

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.viewmodels.ProfileData

@Composable
fun ProfileForm(profile: ProfileData? = null, weightData: Float? = null, onSubmit: (String, Int, Float, Int) -> Unit) {
    val currentName = profile?.name ?: ""
    val currentAge = profile?.age?.toString() ?: ""
    val currentWeight = if (weightData != null) "%.2f".format(weightData) else ""
    val currentHeight = profile?.height?.toString() ?: ""

    var name by remember { mutableStateOf(currentName) }
    var age by remember { mutableStateOf(currentAge) }
    var weight by remember { mutableStateOf(currentWeight) }
    var height by remember { mutableStateOf(currentHeight) }
    var isLoading by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text(
            text = stringResource(R.string.full_name),
            style = MaterialTheme.typography.titleMedium
        )},
        placeholder = { Text(
            text = "Ejemplo: Andrea García Arias",
            style = MaterialTheme.typography.titleMedium
        )},
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        textStyle = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(1.dp))

    OutlinedTextField(
        value = age,
        onValueChange = { age = it },
        label = { Text(
            text = stringResource(R.string.age),
            style = MaterialTheme.typography.titleMedium
        )},
        placeholder = { Text(
            text = "Ejemplo: 45",
            style = MaterialTheme.typography.titleMedium
        )},
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        isError = age.isNotEmpty() && !ageIsValid(age),
        supportingText = {
            if (age.isNotEmpty() && !ageIsValid(age)) {
                Text(
                    text = "Edad no válida",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        textStyle = MaterialTheme.typography.titleMedium
    )

    OutlinedTextField(
        value = weight,
        onValueChange = { weight = it },
        label = { Text(
            text = stringResource(R.string.weight),
            style = MaterialTheme.typography.titleMedium
        )},
        placeholder = { Text(
            text = "Ejemplo: 80",
            style = MaterialTheme.typography.titleMedium
        )},
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Text(
                text = "kg",
                style = MaterialTheme.typography.titleMedium
            )
        },
        isError = weight.isNotEmpty() && !weightIsValid(weight),
        supportingText = {
            if (weight.isNotEmpty() && !weightIsValid(weight)) {
                Text(
                    text = "Peso no válido",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        textStyle = MaterialTheme.typography.titleMedium
    )

    OutlinedTextField(
        value = height,
        onValueChange = { height = it },
        label = { Text(
            text = stringResource(R.string.height),
            style = MaterialTheme.typography.titleMedium
        )},
        placeholder = { Text(
            text = "Ejemplo: 170",
            style = MaterialTheme.typography.titleMedium
        )},
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Text(
                text = "cm",
                style = MaterialTheme.typography.titleMedium
            )
        },
        isError = height.isNotEmpty() && !heightIsValid(height),
        supportingText = {
            if (height.isNotEmpty() && !heightIsValid(height)) {
                Text(
                    text = "Altura no válida",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        textStyle = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            isLoading = true
            onSubmit(name, age.toIntOrNull() ?: 0, weight.replace(",", ".").toFloatOrNull() ?: 0f, height.toIntOrNull() ?: 0)
            isLoading = false
        },
        enabled = isValidForm(name, age, weight, height) && !isLoading,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = Color.Gray,
            disabledContentColor = MaterialTheme.colorScheme.onSecondary
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(24.dp)
            )
        } else {
            Text(text = stringResource(R.string.submit))
        }
    }
}

private fun ageIsValid(age: String) = age.toIntOrNull()?.let { it in 16..120 } == true
private fun heightIsValid(height: String) = height.toIntOrNull()?.let { it in 50..300 } == true
private fun weightIsValid(weight: String): Boolean {
    val weightWithDot = weight.replace(",", ".")
    val weightFloat = weightWithDot.toFloatOrNull() ?: return false

    var validLength = true
    if (weightWithDot.contains(".")) {
        validLength = weightWithDot.substringAfter(".").length in 1..2
    }

    return weightFloat in 30f..300f && validLength
}

private fun isValidForm(name: String, age: String, weight: String, height: String) =
    name.isNotEmpty() && ageIsValid(age) && weightIsValid(weight) && heightIsValid(height)
