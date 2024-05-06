@file:Suppress("unused")

package com.bintianqi.nip.utils.factory

import android.content.Context
import android.content.res.Configuration

val Context.isSystemInDarkMode get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

inline val Context.isNotSystemInDarkMode get() = isSystemInDarkMode.not()

fun Number.dp(context: Context) = dpFloat(context).toInt()

fun Number.dpFloat(context: Context) = toFloat() * context.resources.displayMetrics.density