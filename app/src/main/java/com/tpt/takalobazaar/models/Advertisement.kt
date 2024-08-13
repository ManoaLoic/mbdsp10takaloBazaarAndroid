package com.tpt.takalobazaar.models

import androidx.annotation.DrawableRes

data class Advertisement(
    val title: String,
    val subtitle: String,
    @DrawableRes val image: Int
)