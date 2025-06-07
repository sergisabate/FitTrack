package com.sergisabate.fittrack.data.helpers

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.core.content.edit
import java.util.Locale

const val PREF_NAME = "locale_prefs"
private const val KEY_LOCALE = "selected_locale"

fun saveLocale(context: Context, locale: Locale) {
    val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit { putString(KEY_LOCALE, locale.language) }
}

fun getSavedLocale(context: Context): Locale {
    val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val language = sharedPreferences.getString(KEY_LOCALE, Locale.getDefault().language)
    return Locale.forLanguageTag(language ?: Locale.getDefault().language)
}

fun applyLocale(context: Context, locale: Locale): Context {
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocales(LocaleList(locale))
    return context.createConfigurationContext(config)
}
