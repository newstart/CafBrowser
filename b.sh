#!/bin/bash
SWE_DIR=/home/a/CafBrowser
cd $SWE_DIR
echo "Cleaning"
#rm -fr /src/out/
#rm -fr /src/swe/android/support/src
#rm -fr /build/third_party/cbuildbot_chromite
echo "Syncing"
gclient sync  --nohooks -v --no-nag-max  --delete_unversioned_trees --force --reset || exit
gclient recurse git clean -fdx .
export GYP_DEFINES="OS=android clang=0 remove_webcore_debug_symbols=1" .

echo "Play Services"
$SWE_DIR/src/build/install-android-sdks.sh

echo "Copying Unique files"
cp '/home/a/CafBrowser/special_files/default/branding/BRANDING' '/home/a/CafBrowser/src/swe/channels/default/branding/'
cp '/home/a/CafBrowser/special_files/default/res/mipmap-hdpi/app_icon.png' '/home/a/CafBrowser/src/swe/channels/default/res/mipmap-hdpi/'
cp '/home/a/CafBrowser/special_files/default/res/mipmap-mdpi/app_icon.png' '/home/a/CafBrowser/src/swe/channels/default/res/mipmap-mdpi/'
cp '/home/a/CafBrowser/special_files/default/res/mipmap-xhdpi/app_icon.png' '/home/a/CafBrowser/src/swe/channels/default/res/mipmap-xhdpi/'
cp '/home/a/CafBrowser/special_files/default/res/mipmap-xxhdpi/app_icon.png' '/home/a/CafBrowser/src/swe/channels/default/res/mipmap-xxhdpi/'
cp '/home/a/CafBrowser/special_files/default/res/mipmap-xxxhdpi/app_icon.png' '/home/a/CafBrowser/src/swe/channels/default/res/mipmap-xxxhdpi/'
cp '/home/a/CafBrowser/special_files/raw/web_defender_conf' '/home/a/CafBrowser/src/chrome/android/java/res_chromium/raw'
cp '/home/a/CafBrowser/special_files/raw/web_refiner_conf' '/home/a/CafBrowser/src/chrome/android/java/res_chromium/raw'
cp '/home/a/CafBrowser/special_files/browser/ChromeApplication.java' '/home/a/Desktop/CafBrowser/src/chrome/android/java/src/org/chromium/chrome/browser'
echo "Files copied"

echo "gclient runhooks"
time gclient runhooks -v || exit


cd $SWE_DIR/src
. build/android/envsetup.sh
echo "Building apk"
ninja -C out/Release swe_browser_apk
