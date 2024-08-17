package com.sergisabate.fittrack.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import java.io.IOException

private const val MENU1_JSON_KEY = "menu1_json"
private const val MENU2_JSON_KEY = "menu2_json"
private const val MENU3_JSON_KEY = "menu3_json"
private const val MENU4_JSON_KEY = "menu4_json"
private const val MENU5_JSON_KEY = "menu5_json"
private const val TOTAL_MENUS_KEY = "total_menus"
private const val MENU_SELECTED_KEY = "menu_selected"
private const val DEFAULT_STRING = ""
private const val DEFAULT_INT = 0

private val Context.dataStore by preferencesDataStore(name = "menus")

class MenusRepository private constructor(context: Context) {

    private object PreferencesKeys {
        val MENU1_JSON = stringPreferencesKey(MENU1_JSON_KEY)
        val MENU2_JSON = stringPreferencesKey(MENU2_JSON_KEY)
        val MENU3_JSON = stringPreferencesKey(MENU3_JSON_KEY)
        val MENU4_JSON = stringPreferencesKey(MENU4_JSON_KEY)
        val MENU5_JSON = stringPreferencesKey(MENU5_JSON_KEY)
        val TOTAL_MENUS = intPreferencesKey(TOTAL_MENUS_KEY)
        val MENU_SELECTED = stringPreferencesKey(MENU_SELECTED_KEY)
    }

    private val dataStore = context.applicationContext.dataStore

    private val _menu1JsonFlow = MutableStateFlow(runBlocking { getMenuJson(PreferencesKeys.MENU1_JSON) })
    val menu1JsonFlow: StateFlow<String> = _menu1JsonFlow

    private val _menu2JsonFlow = MutableStateFlow(runBlocking { getMenuJson(PreferencesKeys.MENU2_JSON) })
    val menu2JsonFlow: StateFlow<String> = _menu2JsonFlow

    private val _menu3JsonFlow = MutableStateFlow(runBlocking { getMenuJson(PreferencesKeys.MENU3_JSON) })
    val menu3JsonFlow: StateFlow<String> = _menu3JsonFlow

    private val _menu4JsonFlow = MutableStateFlow(runBlocking { getMenuJson(PreferencesKeys.MENU4_JSON) })
    val menu4JsonFlow: StateFlow<String> = _menu4JsonFlow

    private val _menu5JsonFlow = MutableStateFlow(runBlocking { getMenuJson(PreferencesKeys.MENU5_JSON) })
    val menu5JsonFlow: StateFlow<String> = _menu5JsonFlow

    private val _totalMenusFlow = MutableStateFlow(runBlocking { getTotalMenus() })
    val totalMenusFlow: StateFlow<Int> = _totalMenusFlow

    private val _menuSelectedFlow = MutableStateFlow(runBlocking { getMenuSelected() })
    val menuSelectedFlow: StateFlow<String> = _menuSelectedFlow

    init {
        initParameter(PreferencesKeys.MENU1_JSON, DEFAULT_STRING, _menu1JsonFlow)
        initParameter(PreferencesKeys.MENU2_JSON, DEFAULT_STRING, _menu2JsonFlow)
        initParameter(PreferencesKeys.MENU3_JSON, DEFAULT_STRING, _menu3JsonFlow)
        initParameter(PreferencesKeys.MENU4_JSON, DEFAULT_STRING, _menu4JsonFlow)
        initParameter(PreferencesKeys.MENU5_JSON, DEFAULT_STRING, _menu5JsonFlow)
        initParameter(PreferencesKeys.TOTAL_MENUS, DEFAULT_INT, _totalMenusFlow)
        initParameter(PreferencesKeys.MENU_SELECTED, DEFAULT_STRING, _menuSelectedFlow)
    }

    private fun <T> initParameter(key: Preferences.Key<T>, default: T, mutable: MutableStateFlow<T>) {
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: default
            }
            .onEach { value ->
                mutable.value = value
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    suspend fun setMenuJson(menuJson: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MENU5_JSON] = preferences[PreferencesKeys.MENU4_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.MENU4_JSON] = preferences[PreferencesKeys.MENU3_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.MENU3_JSON] = preferences[PreferencesKeys.MENU2_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.MENU2_JSON] = preferences[PreferencesKeys.MENU1_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.MENU1_JSON] = menuJson
        }
        updateTotalMenus()
    }

    suspend fun setMenuSelected(index: Int) {
        val indexModule = if (index % 5 == 0) 5 else index % 5

        val selectedKey = stringPreferencesKey("menu${indexModule}_json")

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MENU_SELECTED] = preferences[selectedKey] ?: DEFAULT_STRING
        }
    }

    private suspend fun getMenuJson(preferencesKey: Preferences. Key<String>): String {
        return dataStore.data
            .map { preferences ->
                preferences[preferencesKey] ?: DEFAULT_STRING
            }
            .first()
    }

    private suspend fun getTotalMenus(): Int {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.TOTAL_MENUS] ?: DEFAULT_INT
            }
            .first()
    }

    private suspend fun getMenuSelected(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.MENU_SELECTED] ?: DEFAULT_STRING
            }
            .first()
    }

    private suspend fun updateTotalMenus() {
        val keys = listOf(
            PreferencesKeys.MENU1_JSON,
            PreferencesKeys.MENU2_JSON,
            PreferencesKeys.MENU3_JSON,
            PreferencesKeys.MENU4_JSON,
            PreferencesKeys.MENU5_JSON
        )

        val filledMenus = keys.count { key ->
            runBlocking { getMenuJson(key) != DEFAULT_STRING }
        }

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOTAL_MENUS] = filledMenus
        }
        _totalMenusFlow.value = filledMenus
    }

    companion object {
        @Volatile
        private var INSTANCE: MenusRepository? = null

        fun getInstance(context: Context): MenusRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MenusRepository(context).also { INSTANCE = it }
            }
        }
    }
}
