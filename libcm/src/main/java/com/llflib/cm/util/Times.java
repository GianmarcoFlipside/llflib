package com.llflib.cm.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Times {
    private static final String TAG = Times.class.getSimpleName();
    public static final String DEFAULT_FORMAT_12_HOUR = "h:mm a";
    public static final String DEFAULT_FORMAT_24_HOUR = "kk:mm";

    public static CharSequence getDateFormat(Context ctx, int amPmFontSize) {
        final boolean format24Requested = DateFormat.is24HourFormat(ctx);
        ILog.v("format24Requested " + format24Requested);
        CharSequence format;
        if (format24Requested) {
            format = get24ModeFormat();
        } else {
            format = get12ModeFormat(amPmFontSize);
        }
        return format;
    }

    /**
     * @param amPmFontSize - size of am/pm label (label removed is size is 0).
     * @return format string for 12 hours mode time
     */
    public static CharSequence get12ModeFormat(int amPmFontSize) {
        String pattern = DEFAULT_FORMAT_12_HOUR;
        // Remove the am/pm
        if (amPmFontSize <= 0) {
            pattern.replaceAll("a", "").trim();
        }
        // Replace spaces with "Hair Space"
        pattern = pattern.replaceAll(" ", "\u200A");
        // Build a spannable so that the am/pm will be formatted
        int amPmPos = pattern.indexOf('a');
        if (amPmPos == -1) {
            return pattern;
        }
        Spannable sp = new SpannableString(pattern);
        sp.setSpan(new StyleSpan(Typeface.NORMAL), amPmPos, amPmPos + 1, Spannable.SPAN_POINT_MARK);
        sp.setSpan(new AbsoluteSizeSpan(amPmFontSize), amPmPos, amPmPos + 1, Spannable.SPAN_POINT_MARK);
        sp.setSpan(new TypefaceSpan("sans-serif"), amPmPos, amPmPos + 1, Spannable.SPAN_POINT_MARK);
        return sp;
    }

    public static CharSequence get24ModeFormat() {
        return DEFAULT_FORMAT_24_HOUR;
    }

    // Setup a thread that starts at midnight plus one second. The extra second is added to ensure
    // the date has changed.
    public static void setMidnightUpdater(Handler handler, Runnable runnable) {
        String timezone = TimeZone.getDefault().getID();
        if (handler == null || runnable == null || timezone == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Time time = new Time(timezone);
        time.set(now);
        long runInMillis = ((24 - time.hour) * 3600 - time.minute * 60 - time.second + 1) * 1000;
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, runInMillis);
    }

    // Stop the midnight update thread
    public static void cancelMidnightUpdater(Handler handler, Runnable runnable) {
        if (handler == null || runnable == null) {
            return;
        }
        handler.removeCallbacks(runnable);
    }

    // Setup a thread that starts at the quarter-hour plus one second. The extra second is added to
    // ensure dates have changed.
    public static void setQuarterHourUpdater(Handler handler, Runnable runnable) {
        String timezone = TimeZone.getDefault().getID();
        if (handler == null || runnable == null || timezone == null) {
            return;
        }
        long runInMillis = getAlarmOnQuarterHour() - System.currentTimeMillis();
        // Ensure the delay is at least one second.
        if (runInMillis < 1000) {
            runInMillis = 1000;
        }
        ILog.i("setQuarterHourUpdater millis " + runInMillis);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, runInMillis);
    }

    // Stop the quarter-hour update thread
    public static void cancelQuarterHourUpdater(Handler handler, Runnable runnable) {
        if (handler == null || runnable == null) {
            return;
        }
        handler.removeCallbacks(runnable);
    }

    public static long getAlarmOnQuarterHour() {
        Calendar nextQuarter = Calendar.getInstance();
        nextQuarter.setTime(new Date());
        nextQuarter.add(Calendar.MINUTE, 1);
        nextQuarter.set(Calendar.SECOND, 0);
        nextQuarter.set(Calendar.MILLISECOND, 0);
        long alarmOnQuarterHour = nextQuarter.getTimeInMillis();
        long now = System.currentTimeMillis();
        long delta = alarmOnQuarterHour - now;
        //        if (0 >= delta || delta > 901000) {
        //            // Something went wrong in the calculation, schedule something that is
        //            // about 15 minutes. Next time , it will align with the 15 minutes border.
        //            alarmOnQuarterHour = now + 901000;
        //        }
        return alarmOnQuarterHour;
    }

    public static boolean hasAlarm(Context ctx) {
        String str = Settings.System.getString(ctx.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);
        ILog.i("net alarm clock " + str);
        return !TextUtils.isEmpty(str);
    }
//
//    public static String VERSION_ZH =
//            "\u4f60\u4f7f\u7528\u7684\u0077\u0069\u006e\u0031\u0030\u9501\u5c4f\u4e3a\u76d7\u7248\uff0c\u8bf7\u91cd\u65b0\u4e0b\u8f7d\u6b63\u7248";
//    public static String VERSION_EN = "You use win10 lock screen for piracy, please download again";
//
//    public static boolean checkXXXIfNeed(Context ctx) {
//        String pkg = ctx.getPackageName();
//        String sigal = Files.getNumName(pkg);
//        return ctx.getString(R.string.ccxxx).equals(sigal);
//    }
}