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
        Font(R.font.google_sans_flex_regular, FontWeight.Normal),
        Font(R.font.google_sans_flex_regular_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.google_sans_flex_medium, FontWeight.Medium),
        Font(R.font.google_sans_flex_medium_italic, FontWeight.Medium, FontStyle.Italic),
        Font(R.font.google_sans_flex_semibold, FontWeight.SemiBold),
        Font(R.font.google_sans_flex_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
        Font(R.font.google_sans_flex_bold, FontWeight.Bold),
        Font(R.font.google_sans_flex_bold_italic, FontWeight.Bold, FontStyle.Italic),
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
                fontWeight = FontWeight.SemiBold,
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
