package com.sergisabate.fittrack.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.api.getGPTContent
import com.sergisabate.fittrack.data.api.isInternetAvailable
import com.sergisabate.fittrack.data.helpers.getMenusViewModel
import com.sergisabate.fittrack.data.helpers.getProfileViewModel
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.MenuData
import com.sergisabate.fittrack.data.viewmodels.MenusViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.components.FitTrackTitleDescriptionCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun GenerateMenuScreen(navController: NavHostController) {
    val context = LocalContext.current
    val locale = Locale(context.resources.configuration.locales[0].language)
    val keyboardController = LocalSoftwareKeyboardController.current

    var preferences by remember { mutableStateOf("") }
    var totalMenus by remember { mutableIntStateOf(0) }
    var menusList by remember { mutableStateOf(emptyList<MenuData>()) }
    var isLoading by remember { mutableStateOf(false) }

    val profileViewModel: ProfileViewModel = getProfileViewModel()
    val menusViewModel: MenusViewModel = getMenusViewModel()
    val weightsViewModel: WeightsViewModel = getWeightsViewModel()

    val profileData by profileViewModel.getProfileData().collectAsState()
    val currentWeight by weightsViewModel.getCurrentWeight().collectAsState()

    val systemMessage = stringResource(id = R.string.chatgpt_system_message)
    val userMessageFormat = stringResource(id = R.string.chatgpt_menu_message)
    val userMessage = String.format(
        userMessageFormat,
        preferences.replace(Regex("[^a-zA-Z0-9,;\\s]"), ""),
        "${locale.language}-${locale.country}",
        stringResource(profileData.gender.displayName),
        ChronoUnit.YEARS.between(LocalDate.ofEpochDay(profileData.birthDate), LocalDate.now()).toInt(),
        currentWeight.roundToInt(),
        profileData.height,
        LocalDateTime.now(ZoneId.systemDefault()).toString()
    )

    LaunchedEffect(Unit) {
        menusViewModel.getTotalMenus().collect { total ->
            totalMenus = total
            val updatedMenus = (1..totalMenus).mapNotNull { number ->
                menusViewModel.getMenuData(number).value
            }
            menusList = updatedMenus
        }
    }

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
                    Spacer(modifier = Modifier)

                    OutlinedTextField(
                        value = preferences,
                        onValueChange = { preferences = it },
                        label = { Text(
                            text = stringResource(R.string.enter_preferences),
                            style = MaterialTheme.typography.titleMedium
                        )},
                        placeholder = { Text(
                            text = stringResource(id = R.string.placeholder_preferences_menu),
                            style = MaterialTheme.typography.titleMedium
                        )},
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        textStyle = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val errorMessage = stringResource(R.string.error_saving_menu)
                    val noInternetMessage = stringResource(R.string.no_internet_connection)
                    Button(
                        onClick = {
                            keyboardController?.hide()

                            if (isInternetAvailable(context)) {
                                isLoading = true
                                getGPTContent(
                                    systemMessage = systemMessage,
                                    userMessage = userMessage
                                ) { newMenu ->
                                    if (newMenu.isEmpty()){
                                        CoroutineScope(Dispatchers.Main).launch {
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                            isLoading = false
                                        }
                                    }
                                    else{
                                        menusViewModel.setMenuJson(
                                            menuJson = newMenu,
                                            onSuccess = {
                                                totalMenus = menusViewModel.getTotalMenus().value
                                                menusList = (1..totalMenus).mapNotNull { number ->
                                                    menusViewModel.getMenuData(number).value
                                                }
                                                isLoading = false
                                            },
                                            onError = {
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                                isLoading = false
                                            }
                                        )
                                    }
                                }
                            } else {
                                Toast.makeText(context, noInternetMessage, Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(stringResource(id = R.string.generate_menu))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.total_menus_saved, totalMenus),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(menusList) { index, menuData ->
                    val errorMessage = stringResource(R.string.error_selecting_menu)
                    FitTrackTitleDescriptionCard(
                        title = menuData.title,
                        description = dateFormatter(menuData.date, locale),
                        modifier = Modifier.clickable {
                            menusViewModel.setMenuSelected(
                                index = index + 1,
                                onSuccess = {
                                    navController.navigate("ViewMenu")
                                },
                                onError = {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        isClickable = true
                    )
                }

                item {
                    Spacer(modifier = Modifier)
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.generating_menu),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

private fun dateFormatter(date: String, locale: Locale): String {
    val dateTime = LocalDateTime.parse(date)

    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)
        .withLocale(locale)

    return dateTime.format(dateTimeFormatter)
}