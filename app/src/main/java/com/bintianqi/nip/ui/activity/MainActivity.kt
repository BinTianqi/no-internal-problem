@file:Suppress("SetTextI18n")

package com.bintianqi.nip.ui.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.core.view.isVisible
import com.bintianqi.nip.databinding.ActivityMainBinding
import com.bintianqi.nip.ui.activity.base.BaseActivity
import com.bintianqi.nip.R
import com.highcapable.yukihookapi.YukiHookAPI

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate() {
        refreshModuleStatus()
        binding.mainTextVersion.text = getString(R.string.module_version, "v1.0")
        binding.hideIconInLauncherSwitch.isChecked = isLauncherIconShowing.not()
        binding.hideIconInLauncherSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed) hideOrShowLauncherIcon(isChecked)
        }
    }

    private fun hideOrShowLauncherIcon(isShow: Boolean) {
        packageManager?.setComponentEnabledSetting(
            ComponentName(packageName, "${packageName}.Home"),
            if (isShow) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private val isLauncherIconShowing
        get() = packageManager?.getComponentEnabledSetting(
            ComponentName(packageName, "${packageName}.Home")
        ) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    /**
     * Refresh module status
     *
     * 刷新模块状态
     */

    private fun refreshModuleStatus() {
        if (YukiHookAPI.Status.isXposedModuleActive) {
            binding.mainLinStatus.setBackgroundResource(R.drawable.ui_card_activated)
            binding.mainIconStatus.setImageResource(R.drawable.ic_check_circle)
            binding.mainIconStatus.setColorFilter(getColor(R.color.reversedBaseTexture))
            binding.mainTextStatus.setTextColor(getColor(R.color.reversedBaseTexture))
            binding.mainTextVersion.alpha = 0.6F
            binding.mainTextVersion.setTextColor(getColor(R.color.reversedBaseTexture))
            binding.mainTextApiWay.alpha = 0.5F
            binding.mainTextApiWay.setTextColor(getColor(R.color.reversedBaseTexture))
        }
        binding.mainTextStatus.text = getString(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.string.module_is_activated
                else -> R.string.module_not_activated
            }
        )
        binding.mainTextApiWay.isVisible = YukiHookAPI.Status.isXposedModuleActive
        binding.mainTextApiWay.text = if (YukiHookAPI.Status.Executor.apiLevel > 0)
            "${getString(R.string.activateApiWayTextBefore)} ${YukiHookAPI.Status.Executor.name} API ${YukiHookAPI.Status.Executor.apiLevel} ${getString(R.string.activateApiWayTextAfter)}"
        else "${getString(R.string.activateApiWayTextBefore)} ${YukiHookAPI.Status.Executor.name} ${getString(R.string.activateApiWayTextAfter)}"
    }
}