# No internal Problem

## Introduction / 简介

This a xposed module to disable the _"There's an internal problem with your device"_ dialog.

Enable this module in LSPosed manager, select System framework scope and reboot

> 这是一个用于关闭 _“您的设备内部出现了问题，请联系您的设备制造商了解详情”_ 对话框的xposed模块。
> 
> 在LSPosed管理器中启用此模块，勾选 _系统框架_ 作用域，重启。这个对话框就消失了。

## How does it work / 工作原理

I compiled a kernel integrated KernelSU for my OnePlus7. I flashed this kernel to my device. After it boot complete, it shows me a dialog.

> ### Android System
> There's an internal problem with your device. Contact your manufacturer for details.

> 我给我的OnePlus7编译了一个集成了KernelSU的内核。我把这个内核刷进去之后开机，出现了这样的对话框
> 
>> ### Android系统
>> 您的设备内部出现了问题。请联系您的设备制造商了解详情。

So, I searched and finally find this string in [strings.xml of framework-res](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/values/strings.xml)

```xml
<!--  Error message shown when there is a system error which can be solved by the manufacturer. [CHAR LIMIT=NONE]  -->
<string name="system_error_manufacturer">There\'s an internal problem with your device. Contact your manufacturer for details.</string>
```

>所以，我在 [framework-res 的 strings.xml](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/values/strings.xml) 中搜索并找到了这个字符串。
> 
> ```xml
> <string name="system_error_manufacturer" msgid="703545241070116315">"您的设备内部出现了问题。请联系您的设备制造商了解详情。"</string>
> ```

And found this string's only usage in [ActivityTaskManagerService.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java).

> 我在 [ActivityTaskManagerService.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java) 中找到了唯一一个使用此字符串资源的方法。

```java
@Override
public void showSystemReadyErrorDialogsIfNeeded() {
    //...
    boolean isBuildConsistent = Build.isBuildConsistent();
    //...
    if (!isBuildConsistent) {
        //...
        AlertDialog d = new BaseErrorDialog(mUiContext);
        d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        d.setCancelable(false);
        d.setTitle(mUiContext.getText(R.string.android_system_label));
        d.setMessage(mUiContext.getText(R.string.system_error_manufacturer));
        d.setButton(DialogInterface.BUTTON_POSITIVE,
                mUiContext.getText(R.string.ok),
                mUiHandler.obtainMessage(DISMISS_DIALOG_UI_MSG, d));
        d.show();
        //...
    }
    //...
}
```

Then I found `isBuildConsistent()` in [Build.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/os/Build.java)

> 然后，我在 [Build.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/os/Build.java) 中找到了 `isBuildConsistent()` 方法。

```java
public static boolean isBuildConsistent() {
    // Don't care on eng builds.  Incremental build may trigger false negative.
    if (IS_ENG) return true;
    if (IS_TREBLE_ENABLED) {
        // If we can run this code, the device should already pass AVB.
        // So, we don't need to check AVB here.
        int result = VintfObject.verifyWithoutAvb();
        if (result != 0) {
            Slog.e(TAG, "Vendor interface is incompatible, error="
                    + String.valueOf(result));
        }
        return result == 0;
    }
    final String system = SystemProperties.get("ro.system.build.fingerprint");
    final String vendor = SystemProperties.get("ro.vendor.build.fingerprint");
    final String bootimage = SystemProperties.get("ro.bootimage.build.fingerprint");
    final String requiredBootloader = SystemProperties.get("ro.build.expect.bootloader");
    final String currentBootloader = SystemProperties.get("ro.bootloader");
    final String requiredRadio = SystemProperties.get("ro.build.expect.baseband");
    final String currentRadio = joinListOrElse(
            TelephonyProperties.baseband_version(), "");
    if (TextUtils.isEmpty(system)) {
        Slog.e(TAG, "Required ro.system.build.fingerprint is empty!");
        return false;
    }
    if (!TextUtils.isEmpty(vendor)) {
        if (!Objects.equals(system, vendor)) {
            Slog.e(TAG, "Mismatched fingerprints; system reported " + system
                    + " but vendor reported " + vendor);
            return false;
        }
    }
    return true;
}
```

This method checks the fingerprint of `system`, `vendor` and `boot` partition. If their fingerprints are inconsistent, return `false`.

> 这个方法检查 `system`, `vendor` 和 `boot` 分区的指纹，如果三者的指纹不一样，就返回 `false`

I hook this method and make it always return true. Then, this dialog won't show anymore.

> 我hook了这个方法，让它始终返回 `true` ，这个对话框就再也不会显示了。
