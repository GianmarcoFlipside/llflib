package com.llflib.cm.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.llflib.cm.R;

import java.util.Arrays;

import timber.log.Timber;

/**
 * Created by llf on 2015/8/31.
 */
public class Views {
    private static final CharSequence[] EDIT_FORMATS = {" ", "-"};
    private static final int[][] EDIT_POS ={
            {3,8,13},
            {4,6},
    };

    public static int getDimenPx(Context ctx, int value) {
        return getDimenPx(ctx, value, TypedValue.COMPLEX_UNIT_DIP);
    }

    public static int getDimenPx(Context ctx, int value, int unit) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(unit, value, metrics);
    }

    public static int getResourceId(Context ctx, String name, String type) {
        Resources res = ctx.getResources();
        return res.getIdentifier(name, type, ctx.getPackageName());
    }

    public static Drawable getStateDrawable(int focused, int normal) {
        return getStateDrawable(new ColorDrawable(focused), new ColorDrawable(normal));
    }

    /**
     * 设置格式
     *
     * @param tv
     * @param type 0:手机号;1:日期
     */
    public static void setEditTextFormat(final EditText tv, int type) {
        final CharSequence fix = EDIT_FORMATS[type];
        final int[] pos = EDIT_POS[type];
        tv.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    int length = s.toString().length();
                    if(Arrays.binarySearch(pos,0,pos.length,length) >= 0){
                        tv.append(fix);
                        tv.setSelection(length +fix.length());
                    }
                }
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });
    }

    public static Drawable getStateDrawable(Drawable focused, Drawable normal) {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_focused}, focused);
        sd.addState(new int[]{android.R.attr.state_selected}, focused);
        sd.addState(new int[]{android.R.attr.state_pressed}, focused);
        sd.addState(new int[]{}, normal);
        return sd;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public static void setupFullScreen(Activity act) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < 14)
            act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else if (sdk < 16) {
            View root = act.getWindow().getDecorView();
            root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            View root = act.getWindow().getDecorView();
            root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public static void setApplyWindowInserts(final Activity act) {
        final ViewGroup parent = (ViewGroup) act.findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT < 21 || parent == null || parent.getChildCount() == 0)
            return;
        View child = parent.getChildAt(0);
        if (!child.getFitsSystemWindows())
            return;
        final Resources.Theme theme = act.getTheme();
        final TypedValue outValue = new TypedValue();
        theme.resolveAttribute(android.R.attr.windowDrawsSystemBarBackgrounds, outValue, true);
        if (outValue.data == 0)
            return;
        theme.resolveAttribute(android.R.attr.statusBarColor, outValue, true);
        if (outValue.data != 0)
            return;
        theme.resolveAttribute(R.attr.colorPrimaryDark, outValue, true);
        if (outValue.data == 0 || (outValue.data & 0xFFFFFF) == 0)
            return;
        if (child.getClass().getName().contains("android.support.design"))
            return;
        parent.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                if (insets == null)
                    return null;
                Timber.i("WindowInserts top:" + insets.getSystemWindowInsetTop() + ",bottom:" +
                        insets.getSystemWindowInsetBottom());
                View statusBar = parent.findViewById(android.R.id.statusBarBackground);
                if (statusBar == null) {
                    statusBar = new View(act);
                    statusBar.setId(android.R.id.statusBarBackground);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -2);
                    lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    statusBar.setLayoutParams(lp);
                    parent.addView(statusBar);
                }
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) statusBar.getLayoutParams();
                params.height = insets.getSystemWindowInsetTop();
                ColorDrawable drawable = new ColorDrawable(outValue.data);
                statusBar.setBackground(drawable);
                statusBar.setVisibility(insets.getSystemWindowInsetTop() > 0 ? View.VISIBLE : View.GONE);
                View content = parent.getChildAt(0);
                params = (FrameLayout.LayoutParams) content.getLayoutParams();
                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                params.topMargin = insets.getSystemWindowInsetTop();
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                return insets.consumeSystemWindowInsets();
            }
        });
        parent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public static void setApplyWindowInsertEmpty(Activity act) {
        final ViewGroup parent = (ViewGroup) act.findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT < 21 || parent == null || parent.getChildCount() == 0)
            return;
        View child = parent.getChildAt(0);
        if (!child.getFitsSystemWindows())
            return;
        final Resources.Theme theme = act.getTheme();
        final TypedValue outValue = new TypedValue();
        theme.resolveAttribute(android.R.attr.windowDrawsSystemBarBackgrounds, outValue, true);
        if (outValue.data == 0)
            return;
        theme.resolveAttribute(android.R.attr.statusBarColor, outValue, true);
        if (outValue.data != 0)
            return;
        theme.resolveAttribute(R.attr.colorPrimaryDark, outValue, true);
        if (outValue.data == 0 || (outValue.data & 0xFFFFFF) == 0)
            return;
        if (child.getClass().getName().contains("android.support.design"))
            return;
        View view = parent.findViewById(android.R.id.statusBarBackground);
        if (view != null) {
            parent.removeView(view);
            View content = parent.getChildAt(0);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
            lp.topMargin = 0;
        }
    }
}
