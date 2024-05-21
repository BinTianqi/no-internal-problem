package com.bintianqi.nip.application

import androidx.appcompat.app.AppCompatDelegate
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class DefaultApplication : ModuleApplication() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}