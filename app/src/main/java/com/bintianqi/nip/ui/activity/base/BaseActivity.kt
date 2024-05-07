@file:Suppress("MemberVisibilityCanBePrivate")

package com.bintianqi.nip.ui.activity.base

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = current().generic()?.argument()?.method {
            name = "inflate"
            param(LayoutInflaterClass)
        }?.get()?.invoke<VB>(layoutInflater) ?: error("binding failed")
        setContentView(binding.root)
        val isSystemInDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isSystemInDarkMode
            isAppearanceLightNavigationBars = !isSystemInDarkMode
        }
        window?.statusBarColor = 0
        window?.navigationBarColor = 0
        onCreate()
    }
    abstract fun onCreate()
}