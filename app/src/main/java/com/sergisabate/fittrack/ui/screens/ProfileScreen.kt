package com.sergisabate.fittrack.ui.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getPreferencesViewModel
import com.sergisabate.fittrack.data.helpers.getProfileViewModel
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.components.FitTrackTitleDescriptionCard
import java.time.LocalDate
import java.time.Period
import kotlin.math.pow

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val profileViewModel: ProfileViewModel = getProfileViewModel()
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

            ProfileHeader(
                profilePicture = painterResource(id = R.drawable.ic_launcher_foreground),
                name = currentProfileData.name,
                gender = stringResource(currentProfileData.gender.displayName)
            )

            BodyStatisticsCard(
                age = Period.between(LocalDate.ofEpochDay(currentProfileData.birthDate), LocalDate.now()).years,
                weight = "%.2f".format(currentWeight) + " kg",
                height = currentProfileData.height.toString() + " cm",
                bmi = "%.2f".format(currentWeight / (currentProfileData.height / 100.0).pow(2))
            )

            LinkedDevicesCard(navController)

            Spacer(modifier = Modifier)
        }
    }
}

@Composable
private fun ProfileHeader(profilePicture: Painter, name: String, gender: String) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
            Text(
                text = gender,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
private fun BodyStatisticsCard(age: Int, weight: String, height: String, bmi: String) {
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
                title = stringResource(R.string.bmi),
                description = bmi,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LinkedDevicesCard(navController: NavController) {
    val context = LocalContext.current
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
                if (currentLinkedDevice.isEmpty()) {
                    Text(
                        text = stringResource(R.string.pair_scale),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    IconButton(
                        onClick = { navController.navigate("PairScale") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar dispositivo",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.scale_paired_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
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
                        onClick = {
                            preferencesViewModel.deleteBluetoothDevice(
                                onSuccess = {
                                    Toast.makeText(context, context.getString(R.string.scale_unpaired), Toast.LENGTH_SHORT).show()
                                },
                                onError = { e ->
                                    Toast.makeText(context, context.getString(R.string.error_unpairing_scale), Toast.LENGTH_SHORT).show()
                                    Log.e("ProfileScreen", context.getString(R.string.error_unpairing_scale), e)
                                }
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar dispositivo",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun getOfficialName(deviceName : String) : String {
    return if (deviceName == "MI SCALE2") {
        return "Xiaomi Mi Smart Scale 2"
    }
    else {
        deviceName
    }
}
