package com.bintianqi.nip.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        isDebug = false
    }

    override fun onHook() = encase {
        loadSystem {
            "android.os.Build".toClass().apply {
                method {
                    name = "isBuildConsistent"
                    emptyParam()
                }.hook {
                    replaceTo(any = true)
                    YLog.info(msg = "android.os.Build successfully hooked.")
                }
            }
        }
    }
}