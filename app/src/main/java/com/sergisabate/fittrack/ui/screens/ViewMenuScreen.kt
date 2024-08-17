package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.repositories.MenusRepository
import com.sergisabate.fittrack.data.viewmodels.Meal
import com.sergisabate.fittrack.data.viewmodels.MenusViewModel
import com.sergisabate.fittrack.data.viewmodels.MenusViewModelFactory

@Composable
fun ViewMenuScreen(menusViewModel: MenusViewModel = getMenusViewModel()) {
    val menuData by menusViewModel.getMenuSelected().collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            menuData.let {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        MenuSection(stringResource(R.string.breakfast), it.breakfast.meals)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        MenuSection(stringResource(id = R.string.lunch), it.lunch.meals)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        MenuSection(stringResource(id = R.string.snack), it.snack.meals)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        MenuSection(stringResource(id = R.string.dinner), it.dinner.meals)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuSection(mealType: String, meals: List<Meal>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = mealType,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        meals.forEach { meal ->
            MealCard(meal)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MealCard(meal: Meal) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = meal.description,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(top = 4.dp, end = 8.dp, bottom = 4.dp)
                    .weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) "Contraer" else "Expandir",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.ingredients),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            meal.ingredients.forEach { ingredient ->
                Text(
                    text = "- $ingredient",
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.preparation_instructions),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Text(
                text = meal.instructions,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

// Helper function to get the MenusViewModel
@Composable
private fun getMenusViewModel(): MenusViewModel {
    val context = LocalContext.current
    val menusRepository = remember { MenusRepository.getInstance(context) }
    return viewModel(factory = MenusViewModelFactory(menusRepository))
}
