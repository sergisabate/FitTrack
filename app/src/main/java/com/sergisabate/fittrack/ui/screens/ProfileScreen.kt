package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R

@Composable
fun ProfileScreen() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profile Header
            ProfileHeader(
                profilePicture = painterResource(id = R.drawable.ic_launcher_foreground),
                name = "Juan Pérez"
            )

            // Body Statistics
            BodyStatisticsCard(
                age = 28,
                weight = "75 kg",
                height = "175 cm",
                bodyFat = "15%",
                muscleMass = "65 kg"
            )

            // Linked Devices
            LinkedDevicesCard(
                devices = listOf("Xiaomi Scale 2")
            )
        }
    }
}

@Composable
fun ProfileHeader(profilePicture: Painter, name: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = profilePicture,
                contentDescription = stringResource(R.string.profile_picture),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }
    }
}

@Composable
fun BodyStatisticsCard(age: Int, weight: String, height: String, bodyFat: String, muscleMass: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BodyStatisticCard(label = stringResource(R.string.age), value = age.toString(), modifier = Modifier.weight(1f).padding(16.dp, 0.dp, 0.dp, 0.dp))
            BodyStatisticCard(label = stringResource(R.string.weight), value = weight, modifier = Modifier.weight(1f))
            BodyStatisticCard(label = stringResource(R.string.height), value = height, modifier = Modifier.weight(1f).padding(0.dp, 0.dp, 16.dp, 0.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BodyStatisticCard(label = stringResource(R.string.muscle_mass), value = muscleMass, modifier = Modifier.weight(1f).padding(16.dp, 0.dp, 0.dp, 0.dp))
            BodyStatisticCard(label = stringResource(R.string.body_fat), value = bodyFat, modifier = Modifier.weight(1f).padding(0.dp, 0.dp, 16.dp, 0.dp))
        }
    }
}

@Composable
fun BodyStatisticCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 16.dp, 0.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LinkedDevicesCard(devices: List<String>) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.linked_devices),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            devices.forEach { device ->
                Text(
                    text = device,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
