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
cp -r $SWE_DIR/special_files/default/ $SWE_DIR/src/swe/channels/default/
#cp $SWE_DIR/special_files/raw/web_defender_conf $SWE_DIR/src/chrome/android/java/res_chromium/raw
cp $SWE_DIR/special_files/raw/web_refiner_conf $SWE_DIR/src/chrome/android/java/res_chromium/raw
cp $SWE_DIR/special_files/browser/ChromeApplication.java $SWE_DIR/src/chrome/android/java/src/org/chromium/chrome/browser
cp $SWE_DIR/special_files/SWE/SWEBrowserSwitches.java $SWE_DIR/src/base/android/java/src/org/codeaurora/swe
cp $SWE_DIR/special_files/SWE/SWECommandLine.java $SWE_DIR/src/base/android/java/src/org/codeaurora/swe
#cp $SWE_DIR/special_files/SWE/SelectFileDialog.java $SWE_DIR/src/ui/android/java/src/org/chromium/ui/base
echo "Files copied"

echo "gclient runhooks"
time gclient runhooks -v || exit


cd $SWE_DIR/src
. build/android/envsetup.sh
echo "Building apk"
ninja -C out/Release swe_browser_apk
