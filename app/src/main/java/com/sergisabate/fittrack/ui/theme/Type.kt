package com.sergisabate.fittrack.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sergisabate.fittrack.R

val googleSansFamily = FontFamily(
        Font(R.font.productsans_black, FontWeight.Black),
        Font(R.font.productsans_blackitalic, FontWeight.Black, FontStyle.Italic),
        Font(R.font.productsans_bold, FontWeight.Bold),
        Font(R.font.productsans_bolditalic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.productsans_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.productsans_light, FontWeight.Light),
        Font(R.font.productsans_lightitalic, FontWeight.Light, FontStyle.Italic),
        Font(R.font.productsans_medium, FontWeight.Medium),
        Font(R.font.productsans_regular, FontWeight.Normal),
        Font(R.font.productsans_thin, FontWeight.Light),
        Font(R.font.productsans_thinitalic, FontWeight.Thin, FontStyle.Italic),
)

private val defaultTypography = Typography()

val Typography = Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = googleSansFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = googleSansFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = googleSansFamily),

        headlineLarge = TextStyle(
                fontFamily = googleSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.0.sp
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = googleSansFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = googleSansFamily),

        titleLarge = TextStyle(
                fontFamily = googleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.0.sp
        ),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = googleSansFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = googleSansFamily),

        bodyLarge = TextStyle(
                fontFamily = googleSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = googleSansFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = googleSansFamily),

        labelLarge = defaultTypography.labelLarge.copy(fontFamily = googleSansFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = googleSansFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = googleSansFamily)
)
