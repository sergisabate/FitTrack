package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getPreferencesViewModel
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.components.FitTrackTitleCard
import com.sergisabate.fittrack.ui.components.FitTrackTitleDescriptionCard
import com.sergisabate.fittrack.ui.theme.FitTrackTheme

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val context = LocalContext.current
        val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
        val profileViewModel: ProfileViewModel = viewModel(
            factory = ProfileViewModelFactory(userPreferencesRepository)
        )
        val weightsViewModel: WeightsViewModel = getWeightsViewModel()

        val currentWeight by weightsViewModel.getCurrentWeight().collectAsState()
        val currentProfileData by profileViewModel.getProfileData().collectAsState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier)

            // Profile Header
            ProfileHeader(
                profilePicture = painterResource(id = R.drawable.ic_launcher_foreground),
                name = currentProfileData.name
            )

            // Body Statistics
            BodyStatisticsCard(
                age = currentProfileData.age,
                weight = "%.2f".format(currentWeight) + " kg",
                height = currentProfileData.height.toString() + " cm",
                bodyFat = "15%"
            )

            LinkedDevicesCard(navController)

            Spacer(modifier = Modifier)
        }
    }
}

@Composable
fun ProfileHeader(profilePicture: Painter, name: String) {
    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = profilePicture,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun BodyStatisticsCard(age: Int, weight: String, height: String, bodyFat: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FitTrackTitleDescriptionCard(
                title = stringResource(R.string.age),
                description = age.toString(),
                modifier = Modifier.weight(1f)
            )
            FitTrackTitleDescriptionCard(
                title = stringResource(R.string.weight),
                description = weight,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FitTrackTitleDescriptionCard(
                title = stringResource(R.string.height),
                description = height,
                modifier = Modifier.weight(1f)
            )
            FitTrackTitleDescriptionCard(
                title = stringResource(R.string.body_fat),
                description = bodyFat,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LinkedDevicesCard(navController: NavController) {
    val preferencesViewModel: PreferencesViewModel = getPreferencesViewModel()
    val currentLinkedDevice by preferencesViewModel.getBluetoothDevice().collectAsState()

    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.device_paired),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                if (currentLinkedDevice.isEmpty()) {
                    IconButton(
                        onClick = { navController.navigate("Bluetooth") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar dispositivo",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
            if (currentLinkedDevice.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getOfficialName(currentLinkedDevice),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    IconButton(
                        onClick = { preferencesViewModel.deleteBluetoothDevice() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar dispositivo",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }

    if (currentLinkedDevice.isNotEmpty()) {
        FitTrackTitleCard(
            title = "Pesar",
            isClickable = true,
            modifier = Modifier.clickable { navController.navigate("WeighScreen") }
        )
    }
}

fun getOfficialName(deviceName : String) : String {
    return if (deviceName == "MI SCALE2") {
        return "Xiaomi Mi Smart Scale 2"
    }
    else {
        deviceName
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    FitTrackTheme {
        ProfileScreen(rememberNavController())
    }
}
