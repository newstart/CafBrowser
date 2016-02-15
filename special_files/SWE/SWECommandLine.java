/*
 * Copyright (c) 2016, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.codeaurora.swe;

import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;
import android.os.Build;

import org.chromium.base.CommandLine;

public final class SWECommandLine {

    private static SWECommandLine sSWECommandLine;
    private Context mContext;
    private String mFileManagerIntent;
    private String mFileManagerResult;
    private boolean mEstoreEnabled;
    private String mEstoreHomepage;
    private String mEstoreDownloadAppMessage;

    private SWECommandLine(Context context) {
        mContext = context;
    }

    public static SWECommandLine getInstance(Context context) {
        if (sSWECommandLine == null)
            sSWECommandLine = new SWECommandLine(context);
        return sSWECommandLine;
    }

    public void initSWECommandLine() {
        overrideUserAgent();
        setExtraHTTPRequestHeaders();
        overrideMediaDownload();
        setDownloadPathSelection();
        setEstoreConfig();
    }

    private void overrideUserAgent() {
        // Check if the UA is already present using command line file
        CommandLine cl = CommandLine.getInstance();
        if (cl.hasSwitch(SWEBrowserSwitches.OVERRIDE_USER_AGENT)) {
            return;
        }

        final int id = mContext.getResources().getIdentifier("swe_def_ua_override", "string",
                mContext.getPackageName());

        if (id == 0) return;

        String ua = mContext.getResources().getString(id);

        if (TextUtils.isEmpty(ua))
            return;

        ua = constructUserAgent(ua);

        if (!TextUtils.isEmpty(ua)){
            cl.appendSwitchWithValue(SWEBrowserSwitches.OVERRIDE_USER_AGENT, ua);
        }
    }

    private String constructUserAgent(String userAgent) {
        try {
            userAgent = userAgent.replaceAll("<%build_model>", Build.MODEL);
            userAgent = userAgent.replaceAll("<%build_version>", Build.VERSION.RELEASE);
            userAgent = userAgent.replaceAll("<%build_id>", Build.ID);
            userAgent = userAgent.replaceAll("<%language>", Locale.getDefault().getLanguage());
            userAgent = userAgent.replaceAll("<%country>", Locale.getDefault().getCountry());
            return userAgent;
        } catch (Exception ex) {
            return null;
        }
    }

    private void setDownloadPathSelection() {
        final int intent_id = mContext.getResources().getIdentifier(
                "swe_downloadpath_activity_intent",
                "string",
                mContext.getPackageName());
        final int result_id = mContext.getResources().getIdentifier(
                "swe_downloadpath_activity_result_selection", "string",
                mContext.getPackageName());

        if (intent_id == 0 || result_id == 0) return;

        mFileManagerIntent = mContext.getResources().getString(intent_id);
        mFileManagerResult = mContext.getResources().getString(result_id);
        if (!TextUtils.isEmpty(mFileManagerIntent)
            && !TextUtils.isEmpty(mFileManagerResult)) {
            CommandLine cl = CommandLine.getInstance();
            cl.appendSwitch(SWEBrowserSwitches.DOWNLOAD_PATH_SELECTION);
        }
    }

    public String getDownloadFileMgrIntent() {
        return mFileManagerIntent;
    }

    public String getDownloadFileMgrResult() {
        return mFileManagerResult;
    }

    private void setExtraHTTPRequestHeaders() {
        CommandLine cl = CommandLine.getInstance();
        final int id = mContext.getResources().getIdentifier("swe_def_extra_http_headers", "string",
                mContext.getPackageName());

        if (id == 0) return;

        String headers = mContext.getResources().getString(id);
        if (!TextUtils.isEmpty(headers))
            cl.appendSwitchWithValue(SWEBrowserSwitches.HTTP_HEADERS, headers);
    }

    private void overrideMediaDownload() {
        CommandLine cl = CommandLine.getInstance();
        final int id = mContext.getResources().getIdentifier("swe_feature_allow_media_downloads",
                "bool",
                mContext.getPackageName());

        if (id == 0) return;

        boolean defaultAllowMediaDownloadsValue = mContext.getResources().getBoolean(id);
        if (defaultAllowMediaDownloadsValue)
            cl.appendSwitchWithValue(SWEBrowserSwitches.OVERRIDE_MEDIA_DOWNLOAD, "1");
    }


    private void setEstoreConfig() {
        final int estoreHomepageId = mContext.getResources().getIdentifier(
                                       "swe_estore_homepage",
                                       "string",
                                       mContext.getPackageName());
        final int estoreDownloadEstoreAppId = mContext.getResources().getIdentifier(
                                       "swe_download_estore_app",
                                       "string",
                                       mContext.getPackageName());

        if (estoreHomepageId == 0 || estoreDownloadEstoreAppId == 0) return;

        mEstoreHomepage = mContext.getResources().getString(estoreHomepageId);
        mEstoreDownloadAppMessage = mContext.getResources().getString(estoreDownloadEstoreAppId);
        if (!TextUtils.isEmpty(mEstoreHomepage)) {
            mEstoreEnabled = true;
        } else {
            mEstoreEnabled = false;
        }
    }

    public boolean getEstoreEnabled() {
        return mEstoreEnabled;
    }

    public String getEstoreHomepage() {
        return mEstoreHomepage;
    }

    public String getEstoreDownloadAppMessage() {
        return mEstoreDownloadAppMessage;
    }

}
