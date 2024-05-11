# No internal Problem

[简体中文](Readme.md)

## Introduction

This a Xposed module to disable the _"There's an internal problem with your device"_ dialog.

Enable this module in LSPosed manager, select System framework scope and reboot

This module is made with [YukiHook-API](https://github.com/HighCapable/YukiHookAPI)

## How does it work

I compiled a kernel integrated KernelSU for my OnePlus7. I flashed this kernel to my device. After it boot complete, it shows me a dialog.

> ### Android System
> There's an internal problem with your device. Contact your manufacturer for details.

So, I searched and finally find this string in [strings.xml of framework-res](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/values/strings.xml)

```xml
<!--  Error message shown when there is a system error which can be solved by the manufacturer. [CHAR LIMIT=NONE]  -->
<string name="system_error_manufacturer">There\'s an internal problem with your device. Contact your manufacturer for details.</string>
```

And found this string's only usage in [ActivityTaskManagerService.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java).

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

I hook this method and make it always return true. Then, this dialog won't show anymore.

## License

The MIT License

> Copyright <2024> Bin Tianqi
>
> Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
