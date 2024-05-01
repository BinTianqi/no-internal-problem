@file:Suppress("SetTextI18n")

package com.bintianqi.nip.ui.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.YukiHookAPI
import com.bintianqi.nip.R
import com.bintianqi.nip.databinding.ActivityMainBinding
import com.bintianqi.nip.ui.activity.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate() {
        refreshModuleStatus()
        binding.mainTextVersion.text = getString(R.string.module_version, "v1.0")
        binding.hideIconInLauncherSwitch.isChecked = isLauncherIconShowing.not()
        binding.hideIconInLauncherSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed) hideOrShowLauncherIcon(isChecked)
        }
        // Your code here.
    }

    /**
     * Hide or show launcher icons
     *
     * - You may need the latest version of LSPosed to enable the function of hiding launcher
     *   icons in higher version systems
     *
     * 隐藏或显示启动器图标
     *
     * - 你可能需要 LSPosed 的最新版本以开启高版本系统中隐藏 APP 桌面图标功能
     * @param isShow Whether to display / 是否显示
     */
    private fun hideOrShowLauncherIcon(isShow: Boolean) {
        packageManager?.setComponentEnabledSetting(
            ComponentName(packageName, "${packageName}.Home"),
            if (isShow) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    /**
     * Get launcher icon state
     *
     * 获取启动器图标状态
     * @return [Boolean] Whether to display / 是否显示
     */
    private val isLauncherIconShowing
        get() = packageManager?.getComponentEnabledSetting(
            ComponentName(packageName, "${packageName}.Home")
        ) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    /**
     * Get current night mode settings
     *
     * 获取当前夜间模式设置状态
     * @return [Boolean]
     */

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