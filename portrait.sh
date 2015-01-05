adb shell content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0 > /dev/null
adb shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:0 > /dev/null
