package com.sergisabate.fittrack.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.api.getGPTContent
import com.sergisabate.fittrack.data.api.isInternetAvailable
import com.sergisabate.fittrack.data.repositories.MenusRepository
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.MenuData
import com.sergisabate.fittrack.data.viewmodels.MenusViewModel
import com.sergisabate.fittrack.data.viewmodels.MenusViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModelFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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

    val profileData by profileViewModel.getProfileData().collectAsState()

    val systemMessage = stringResource(id = R.string.chatgpt_system_message)
    val userMessageFormat = stringResource(id = R.string.chatgpt_user_message)
    val userMessage = String.format(
        userMessageFormat,
        preferences.replace(Regex("[^a-zA-Z0-9,;\\s]"), ""),
        "${locale.language}-${locale.country}",
        profileData.age,
        profileData.weight,
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
            ) {
                item {
                    TextField(
                        value = preferences,
                        onValueChange = { preferences = it },
                        label = { Text(stringResource(id = R.string.enter_preferences)) },
                        placeholder = { Text(stringResource(id = R.string.placeholder_preferences)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
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
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                        isLoading = false
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
                        enabled = !isLoading
                    ) {
                        Text(stringResource(id = R.string.generate_menu))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(id = R.string.total_menus_saved, totalMenus))
                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(menusList) { index, menuData ->
                    MenuCard(index = index, menuData = menuData, locale = locale, menusViewModel = menusViewModel, navController = navController)
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
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
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    index: Int,
    menuData: MenuData,
    locale: Locale,
    menusViewModel: MenusViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val errorMessage = stringResource(R.string.error_selecting_menu)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                menusViewModel.setMenuSelected(
                    index = index + 1,
                    onSuccess = {
                        navController.navigate("ViewMenu")
                    },
                    onError = {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = menuData.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dateFormatter(menuData.date, locale),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ir al menú",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}

@Composable
private fun getProfileViewModel(): ProfileViewModel {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    return viewModel(factory = ProfileViewModelFactory(userPreferencesRepository))
}

@Composable
private fun getMenusViewModel(): MenusViewModel {
    val context = LocalContext.current
    val menusRepository = remember { MenusRepository.getInstance(context) }
    return viewModel(factory = MenusViewModelFactory(menusRepository))
}

@Composable
private fun dateFormatter(date: String, locale: Locale): String {
    val dateTime = LocalDateTime.parse(date)

    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)
        .withLocale(locale)

    return dateTime.format(dateTimeFormatter)
}