mkdir release
copy build\outputs\apk\release release/app-release.apk
adb uninstall com.nstudio.calc.casio
adb install -r release/app-release.apk
adb shell am start -n "com.nstudio.calc.casio/com.duy.natural.calc.calculator.CalculatorActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
exit