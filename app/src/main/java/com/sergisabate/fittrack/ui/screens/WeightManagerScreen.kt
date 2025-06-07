package com.sergisabate.fittrack.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.components.FitTrackTitleDescriptionClickableIconCard
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

@Composable
fun WeightManagerScreen() {
    val context = LocalContext.current
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()

    val weightsViewModel: WeightsViewModel = getWeightsViewModel()
    val weightRecords by weightsViewModel.weightHistory.collectAsState()

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    var weight by remember { mutableStateOf("") }
                    var date by remember { mutableStateOf<Long?>(null) }

                    Spacer(modifier = Modifier.height(8.dp))

                    DatePickerField(
                        selectedDate = date,
                        onDateSelected = { date = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = {
                            Text(
                                text = stringResource(R.string.weight),
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.placeholder_weight_example),
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val weightValue = weight.replace(",", ".").toFloatOrNull()
                            if (isValidWeight(weight, date)) {
                                weightsViewModel.addOrUpdateWeightRecord(
                                    weight = weightValue!!,
                                    date = date!!,
                                    onSuccess = {
                                        Toast.makeText(context, context.getString(R.string.weight_added), Toast.LENGTH_SHORT).show()
                                    },
                                    onError = {
                                        Toast.makeText(context, context.getString(R.string.error_saving_weight), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        enabled = isValidWeight(weight, date),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(stringResource(R.string.add_weight))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.total_weights_saved, weightRecords.size),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(weightRecords.reversed()) { _, weightRecord ->
                    FitTrackTitleDescriptionClickableIconCard(
                        title = "%.2f kg".format(weightRecord.weight),
                        description = dateFormatter(weightRecord.date, locale),
                        icon = Icons.Default.Delete,
                        onClick = {
                            if (weightRecords.size == 1) {
                                Toast.makeText(context, context.getString(R.string.last_weight_cannot_be_deleted), Toast.LENGTH_SHORT).show()
                            } else {
                                weightsViewModel.deleteWeightRecord(
                                    weightRecord = weightRecord,
                                    onSuccess = {
                                        Toast.makeText(context, context.getString(R.string.weight_deleted), Toast.LENGTH_SHORT).show()
                                    },
                                    onError = {
                                        Toast.makeText(context, context.getString(R.string.error_deleting_weight), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier)
                }
            }
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
        initialSelectedDateMillis = selectedDate?.toUtcMillis() ?: now.toEpochMilli(),
        selectableDates = FutureSelectableDates()
    )

    LaunchedEffect(isLandscape) {
        datePickerState.displayMode = if (isLandscape) DisplayMode.Input else DisplayMode.Picker
    }

    OutlinedTextField(
        value = selectedDate?.toFormattedDate() ?: "",
        onValueChange = {},
        label = { Text(stringResource(R.string.date), style = MaterialTheme.typography.titleMedium) },
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
                        text = stringResource(R.string.date),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(datePickerTitlePadding)
                    )
                },
                showModeToggle = true
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

private fun dateFormatter(epochDate: Long, locale: Locale): String {
    val date = LocalDate.ofEpochDay(epochDate)

    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        .withLocale(locale)

    return date.format(dateTimeFormatter)
}

private fun weightIsValid(weight: String): Boolean {
    val weightWithDot = weight.replace(",", ".")
    val weightFloat = weightWithDot.toFloatOrNull() ?: return false

    var validLength = true
    if (weightWithDot.contains(".")) {
        validLength = weightWithDot.substringAfter(".").length in 1..2
    }

    return weightFloat in 30f..300f && validLength
}

private fun isValidWeight(weight: String, date: Long?) = weightIsValid(weight) && date != null

@OptIn(ExperimentalMaterial3Api::class)
private class FutureSelectableDates : SelectableDates {
    private val minSelectableDate: Long
    private val minSelectableYear: Int

    private val maxSelectableDate: Long
    private val maxSelectableYear: Int

    init {
        val now = LocalDate.now()

        minSelectableDate = now
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        minSelectableYear = now.year

        maxSelectableDate = now.minusYears(5)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        maxSelectableYear = now.year - 5
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis in maxSelectableDate..minSelectableDate
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year in maxSelectableYear..minSelectableYear
    }
}
