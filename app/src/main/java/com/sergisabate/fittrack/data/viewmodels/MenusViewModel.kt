package com.sergisabate.fittrack.data.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sergisabate.fittrack.data.repositories.MenusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Keep
data class MenuData(
    val title: String,
    val date: String,
    val breakfast: Meals,
    val lunch: Meals,
    val snack: Meals,
    val dinner: Meals
)

@Keep
data class Meals(
    val meals: List<Meal>
)

@Keep
data class Meal(
    val description: String,
    val ingredients: List<String>,
    val instructions: String
)

class MenusViewModel(private val menusRepository: MenusRepository) : ViewModel() {

    fun setMenuJson(
        menuJson: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                menusRepository.setMenuJson(menuJson)
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    fun setMenuSelected(
        index: Int,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                menusRepository.setMenuSelected(index)
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    fun getMenuData(number: Int): StateFlow<MenuData> {
        val menuJson = when (number) {
            1 -> menusRepository.menu1JsonFlow
            2 -> menusRepository.menu2JsonFlow
            3 -> menusRepository.menu3JsonFlow
            4 -> menusRepository.menu4JsonFlow
            5 -> menusRepository.menu5JsonFlow
            else -> {menusRepository.menu1JsonFlow}
        }
        val menuData: MenuData = Gson().fromJson(menuJson.value, MenuData::class.java)
        return MutableStateFlow(menuData)
    }

    fun getTotalMenus(): StateFlow<Int> {
        return menusRepository.totalMenusFlow
    }

    fun getMenuSelected(): StateFlow<MenuData> {
        val menuSelectedFlow = menusRepository.menuSelectedFlow
        val menuData: MenuData = Gson().fromJson(menuSelectedFlow.value, MenuData::class.java)
        return MutableStateFlow(menuData)
    }
}

class MenusViewModelFactory(
    private val menusRepository: MenusRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MenusViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return MenusViewModel(menusRepository) as T
    }
}
