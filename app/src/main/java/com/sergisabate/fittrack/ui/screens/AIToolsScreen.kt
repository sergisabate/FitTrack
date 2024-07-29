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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R

@Composable
fun AIToolsScreen() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tools Header
            ToolsHeader(
                title = "Herramientas de IA",
                description = "Utiliza herramientas impulsadas por IA para mejorar tu rendimiento y bienestar."
            )

            // AI Tools List
            AI_ToolsList()
        }
    }
}

@Composable
fun ToolsHeader(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun AI_ToolsList() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AI_ToolCard(
            title = "Análisis de Rendimiento",
            description = "Recibe un análisis detallado de tu rendimiento en los entrenamientos y recomendaciones para mejorar.",
            icon = painterResource(id = R.drawable.ic_launcher_foreground)
        )
        AI_ToolCard(
            title = "Recomendaciones Personalizadas",
            description = "Obtén recomendaciones personalizadas de ejercicios y planes de entrenamiento basados en tu historial y objetivos.",
            icon = painterResource(id = R.drawable.ic_launcher_foreground)
        )
        AI_ToolCard(
            title = "Asistente Nutricional",
            description = "Consulta con un asistente nutricional que ofrece sugerencias y planes de dieta adaptados a tus necesidades.",
            icon = painterResource(id = R.drawable.ic_launcher_foreground)
        )
    }
}

@Composable
fun AI_ToolCard(title: String, description: String, icon: Painter) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
