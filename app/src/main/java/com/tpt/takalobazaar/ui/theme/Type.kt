package com.tpt.takalobazaar.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tpt.takalobazaar.R

val font = FontFamily(
    Font(resId = R.font.asul_black, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(resId = R.font.asul_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(resId = R.font.autour_one, weight = FontWeight.Normal, style = FontStyle.Normal),
)

val Typography = Typography(
    h1 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Black,
        fontSize = 35.sp
    ),
    h2 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    h3 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    h4 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp
    ),
    h5 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    body1 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp
    ),
    body2 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp
    ),
    caption = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    ),
    defaultFontFamily = font,
)
