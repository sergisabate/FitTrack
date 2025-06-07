package com.sergisabate.fittrack.ui.forms

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.viewmodels.GenderOption
import com.sergisabate.fittrack.data.viewmodels.ProfileData
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

@Composable
fun ProfileForm(profile: ProfileData? = null, weightData: Float? = null, onSubmit: (String, Long, GenderOption, Float, Int) -> Unit) {
    val currentName = profile?.name ?: ""
    val currentBirthDate = profile?.birthDate
    val currentGender = profile?.gender
    val currentWeight = weightData?.let { "%.2f".format(it) } ?: ""
    val currentHeight = profile?.height?.toString() ?: ""

    var name by remember { mutableStateOf(currentName) }
    var birthDate by remember { mutableStateOf(currentBirthDate) }
    var gender by remember { mutableStateOf(currentGender) }
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
            text = stringResource(R.string.placeholder_name_example),
            style = MaterialTheme.typography.titleMedium
        )},
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        textStyle = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(1.dp))

    DatePickerField(birthDate) { birthDate = it }

    Spacer(modifier = Modifier.height(1.dp))

    GenderDropdownField(gender) { gender = it }

    Spacer(modifier = Modifier.height(1.dp))

    OutlinedTextField(
        value = weight,
        onValueChange = { weight = it },
        label = { Text(
            text = stringResource(R.string.weight),
            style = MaterialTheme.typography.titleMedium
        )},
        placeholder = { Text(
            text = stringResource(R.string.placeholder_weight_example),
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
                    text = stringResource(R.string.invalid_weight),
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
            text = stringResource(R.string.placeholder_height_example),
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
                    text = stringResource(R.string.invalid_height),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        textStyle = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier)

    Button(
        onClick = {
            isLoading = true
            onSubmit(name, birthDate!!, gender!!, weight.replace(",", ".").toFloatOrNull() ?: 0f, height.toIntOrNull() ?: 0)
            isLoading = false
        },
        enabled = isValidForm(name, birthDate, gender, weight, height) && !isLoading,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(selectedDate: Long?, onDateSelected: (Long?) -> Unit) {
    var showModal by remember { mutableStateOf(false) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val now = LocalDate.now()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.toUtcMillis() ?: now.minusYears(16).toEpochMilli(),
        selectableDates = FutureSelectableDates()
    )

    LaunchedEffect(isLandscape) {
        datePickerState.displayMode = if (isLandscape) DisplayMode.Input else DisplayMode.Picker
    }

    OutlinedTextField(
        value = selectedDate?.toFormattedDate() ?: "",
        onValueChange = {},
        label = { Text(stringResource(R.string.date_of_birth), style = MaterialTheme.typography.titleMedium) },
        placeholder = { Text(stringResource(R.string.placeholder_date_example), style = MaterialTheme.typography.titleMedium) },
        readOnly = true,
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) {
                    showModal = true
                }
            }
    )

    if (showModal) {
        val datePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)

        DatePickerDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis?.toEpochDay())
                    showModal = false
                }) {
                    Text(stringResource(R.string.accept))
                }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = stringResource(R.string.date_of_birth),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(datePickerTitlePadding)
                    )
                },
                showModeToggle = true,
                colors = DatePickerDefaults.colors(
                    currentYearContentColor = DatePickerDefaults.colors().disabledYearContentColor,
                    todayContentColor = Color.Transparent,
                    todayDateBorderColor = Color.Transparent
                )
            )
        }
    }
}

private fun LocalDate.toEpochMilli(): Long = this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
private fun Long.toEpochDay(): Long = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()
private fun Long.toUtcMillis(): Long = LocalDate.ofEpochDay(this).toEpochMilli()
private fun Long.toFormattedDate(): String {
    val locale = Locale.getDefault()
    val pattern = when (locale.language) {
        "en" -> "MM/dd/yyyy"
        else -> "dd/MM/yyyy"
    }
    return SimpleDateFormat(pattern, locale).format(Date(this.toUtcMillis()))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownField(
    selectedGender: GenderOption?,
    onGenderSelected: (GenderOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedGender?.let { stringResource(it.displayName) } ?: "",
            onValueChange = {},
            label = {
                Text(
                    text = stringResource(R.string.gender),
                    style = MaterialTheme.typography.titleMedium
                )},
            readOnly = true,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            GenderOption.entries.filter { it != GenderOption.None }.forEach { gender ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(gender.displayName),
                            style = MaterialTheme.typography.titleMedium
                        )},
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

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

private fun isValidForm(name: String, birthday: Long?, gender: GenderOption?, weight: String, height: String) =
    name.isNotEmpty() && birthday != null && gender != null && weightIsValid(weight) && heightIsValid(height)


@OptIn(ExperimentalMaterial3Api::class)
private class FutureSelectableDates : SelectableDates {
    private val minSelectableDate: Long
    private val minSelectableYear: Int

    private val maxSelectableDate: Long
    private val maxSelectableYear: Int

    init {
        val now = LocalDate.now()

        minSelectableDate = now.minusYears(16)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        minSelectableYear = now.year - 16

        maxSelectableDate = now.minusYears(90)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        maxSelectableYear = now.year - 90
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis in maxSelectableDate..minSelectableDate
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year in maxSelectableYear..minSelectableYear
    }
}
