package com.bintianqi.nip.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {
    override fun onHook() = encase {
        loadZygote{
            "android.os.Build".toClass().method {
                name = "isBuildConsistent"
                emptyParam()
                returnType = BooleanType
            }.hook {
                after {
                    result = true
                }
            }
        }
    }
}