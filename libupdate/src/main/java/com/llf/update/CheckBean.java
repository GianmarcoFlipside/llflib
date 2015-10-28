package com.llf.update;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONObject;

public class CheckBean implements Parcelable {
        String title;
        String message;
        String loadUrl;
        int versionCode;
        String versionName;

        boolean downInMoblie;

        private CheckBean(Parcel parcel) {
            title = parcel.readString();
            message = parcel.readString();
            loadUrl = parcel.readString();
            versionName = parcel.readString();
            versionCode = parcel.readInt();
            downInMoblie = parcel.readInt() != 0;
        }

        public CheckBean(JSONObject json) {
            parse(json);
        }

        public boolean isNeedUpdate() {
            return !TextUtils.isEmpty(loadUrl);
        }

        void parse(JSONObject json) {
            title = json.optString("title");
            message = json.optString("msg");
            loadUrl = json.optString("url");
            versionCode = json.optInt("versionCode");
            versionName = json.optString("versionName");
        }

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title == null ? "" : title);
            dest.writeString(message == null ? "" : message);
            dest.writeString(loadUrl == null ? "" : loadUrl);
            dest.writeString(versionName == null ? "" : versionName);
            dest.writeInt(versionCode);
            dest.writeInt(downInMoblie ? 1 : 0);
        }

        public static final Creator<CheckBean> CREATOR = new Creator<CheckBean>() {
            @Override public CheckBean createFromParcel(Parcel source) {
                return new CheckBean(source);
            }

            @Override public CheckBean[] newArray(int size) {
                return new CheckBean[size];
            }
        };
    }