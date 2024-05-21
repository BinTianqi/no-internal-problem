# No internal Problem

[English](Readme-en.md)

## 简介

这是一个用于关闭 _“您的设备内部出现了问题，请联系您的设备制造商了解详情”_ 对话框的Xposed模块。

在LSPosed管理器中启用此模块，勾选 _系统框架_ 作用域，重启。这个对话框就消失了。

这个模块使用[YukiHook-API](https://github.com/HighCapable/YukiHookAPI)制作

## 工作原理

我给我的OnePlus7编译了一个集成了KernelSU的内核。我把这个内核刷进去之后开机，出现了这样的对话框
 
> ### Android系统
> 您的设备内部出现了问题。请联系您的设备制造商了解详情。

所以，我在 [framework-res 的 strings.xml](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/values/strings.xml) 中搜索并找到了这个字符串。

```xml
<!--  Error message shown when there is a system error which can be solved by the manufacturer. [CHAR LIMIT=NONE]  -->
<string name="system_error_manufacturer">There\'s an internal problem with your device. Contact your manufacturer for details.</string>
```

我在 [ActivityTaskManagerService.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java) 中找到了唯一一个使用此字符串资源的方法。

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

然后，我在 [Build.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/os/Build.java) 中找到了 `isBuildConsistent()` 方法。

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

这个方法检查 `system`, `vendor` 和 `boot` 分区的指纹，如果三者的指纹不一样，就返回 `false`

我hook了这个方法，让它始终返回 `true` ，这个对话框就再也不会显示了。

## 许可证

The MIT License

> Copyright <2024> Bin Tianqi
>
> Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
