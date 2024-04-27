package com.bintianqi.nip.hook

import com.bintianqi.nip.R
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.core.annotation.LegacyResourcesHook
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(isUsingResourcesHook = true)
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        // Your code here.
    }

    @OptIn(LegacyResourcesHook::class)
    override fun onHook() = encase {
        loadZygote{
            resources().hook{
                injectResource{
                    conditions{
                        name = "system_error_manufacturer"
                        string()
                    }
                    replaceToModuleResource(R.string.welcome_text)
                }
            }
        }
    }
}