package com.llflib.cm.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.llflib.cm.R;

/**
 * Created by llf on 2015/7/10.
 */
public class ItemLayout extends ViewGroup {
    private TextView mText, mHint;
    private int mTextAppearance, mHintAppearance;
    private Drawable mLeftDrawable, mRightDrawable;
    private int mDrawablePadding;

    public ItemLayout(Context context) {
        this(context, null);
    }

    public ItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs,R.attr.ItemLayoutStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemLayout,R.attr.ItemLayoutStyle,0);
        mTextAppearance = a.getResourceId(R.styleable.ItemLayout_android_textAppearance, 0);
        mHintAppearance = a.getResourceId(R.styleable.ItemLayout_subTitleAppearance, 0);
        mDrawablePadding = a.getDimensionPixelOffset(R.styleable.ItemLayout_android_drawablePadding,0);
        Drawable d = a.getDrawable(R.styleable.ItemLayout_android_drawableLeft);
        setLeft(d);
        d = a.getDrawable(R.styleable.ItemLayout_android_drawableRight);
        setRight(d);
        String txt = a.getString(R.styleable.ItemLayout_android_text);
        setText(txt);
        txt = a.getString(R.styleable.ItemLayout_android_hint);
        setHint(txt);
        a.recycle();
    }

    public void setLeft(Drawable drawable) {
        if (mLeftDrawable != drawable) {
            if (mLeftDrawable != null) {
                mLeftDrawable.setCallback(null);
                unscheduleDrawable(mLeftDrawable);
            }
            if (drawable != null) {
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
            }
            mLeftDrawable = drawable;
            invalidate();
        }
    }

    public void setRight(Drawable drawable) {
        if (mRightDrawable != drawable) {
            if (mRightDrawable != null) {
                mRightDrawable.setCallback(null);
                unscheduleDrawable(mRightDrawable);
            }
            if (drawable != null) {
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
            }
            mRightDrawable = drawable;
            invalidate();
        }
    }

    @Override protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || mLeftDrawable == who || mRightDrawable == who;
    }

    public void setText(String title) {
        if (!TextUtils.isEmpty(title)) {
            if (mText == null) {
                mText = new TextView(getContext());
                mText.setGravity(Gravity.CENTER_VERTICAL);
                mText.setSingleLine();
                if (mTextAppearance != 0) {
                    mText.setTextAppearance(getContext(), mTextAppearance);
                }
            }
            if (mText.getParent() == null) {
                LayoutParams lp = generateDefaultLayoutParams();
                addView(mText, -1, lp);
            }
        } else if (mText != null && mText.getParent() != null) {
            removeView(mText);
        }
        if (mText != null) {
            mText.setText(title);
        }
    }

    public void setHint(String title) {
        if (!TextUtils.isEmpty(title)) {
            if (mHint == null) {
                mHint = new TextView(getContext());
                mHint.setGravity(Gravity.CENTER_VERTICAL);
                mHint.setSingleLine();
                if (mHintAppearance != 0) {
                    mHint.setTextAppearance(getContext(), mHintAppearance);
                }
            }
            if (mHint.getParent() == null) {
                addView(mHint, -1, generateDefaultLayoutParams());
            }
        } else if (mHint != null && mHint.getParent() != null) {
            removeView(mHint);
        }
        if (mHint != null) {
            mHint.setText(title);
        }
    }

    @Override protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mLeftDrawable != null) {
            mLeftDrawable.draw(canvas);
        }
        if (mRightDrawable != null) {
            mRightDrawable.draw(canvas);
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int wm = MeasureSpec.getMode(widthMeasureSpec);
        int wh = MeasureSpec.getMode(heightMeasureSpec);
        int wp = getPaddingLeft() + getPaddingRight();
        int desiredH = 0;
        if (mLeftDrawable != null) {
            wp += mLeftDrawable.getIntrinsicWidth() + mDrawablePadding;
            desiredH = Math.max(mLeftDrawable.getIntrinsicHeight(), desiredH);
        }
        if (mRightDrawable != null) {
            wp += mRightDrawable.getIntrinsicWidth();
            desiredH = Math.max(mRightDrawable.getIntrinsicHeight(), desiredH);
        }
        //measure the minum size
        if (mText != null) {
            measureInnnerChild(mText, width - wp,MeasureSpec.AT_MOST, height,MeasureSpec.AT_MOST);
            desiredH = Math.max(mText.getMeasuredHeight(), desiredH);
            wp += mText.getMeasuredWidth();
        }
        if (mHint != null) {
            measureInnnerChild(mHint, width - wp,MeasureSpec.AT_MOST, height,MeasureSpec.AT_MOST);
            desiredH = Math.max(mText.getMeasuredHeight(), desiredH);
            wp += mHint.getMeasuredWidth();
        }
        desiredH = Math.max(desiredH +getPaddingBottom() +getPaddingTop(),getSuggestedMinimumHeight());
        height = resolveSize(desiredH,heightMeasureSpec);
        //set the really size
        desiredH = height - getPaddingTop() - getPaddingBottom();
        if(mText != null){
            int tw = mText.getMeasuredWidth();
            if (wm == MeasureSpec.EXACTLY && wp < width && mText != null) {
                tw += width - wp;
            }
            measureInnnerChild(mText,tw,MeasureSpec.EXACTLY,desiredH,MeasureSpec.EXACTLY);
        }
        if(mHint != null){
            int tw = mHint.getMeasuredWidth();
            measureInnnerChild(mHint,tw,MeasureSpec.EXACTLY,desiredH,MeasureSpec.EXACTLY);
        }

        setMeasuredDimension(resolveSize(wp, widthMeasureSpec),height);
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (mLeftDrawable != null) {
            int lt = computeTop(h, mLeftDrawable.getIntrinsicHeight(), top);
            mLeftDrawable.setBounds(left, lt, left + mLeftDrawable.getIntrinsicWidth(),
                    lt + mLeftDrawable.getIntrinsicHeight());
            left += mLeftDrawable.getIntrinsicWidth() + mDrawablePadding;
        }
        if (mRightDrawable != null) {
            int lt = computeTop(h, mRightDrawable.getIntrinsicHeight(), top);
            mRightDrawable.setBounds(w - getPaddingRight() - mRightDrawable.getIntrinsicWidth(), lt,
                    w - getPaddingRight(), lt + mRightDrawable.getIntrinsicHeight());
        }
        if (mText != null) {
            mText.layout(left, top, left + mText.getMeasuredWidth(), top + mText.getMeasuredHeight());
            left += mText.getMeasuredWidth();
        }
        if (mHint != null) {
            mHint.layout(left, top, left + mHint.getMeasuredWidth(), top + mHint.getMeasuredHeight());
        }
    }

    int computeTop(int ph, int h, int top) {
        return Math.max(top, (ph - h) / 2);
    }

    void measureInnnerChild(View child, int width,int wm, int height,int hm) {
        int wc = MeasureSpec.makeMeasureSpec(width,wm);
        int wh = MeasureSpec.makeMeasureSpec(height,hm);
        child.measure(wc, wh);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        private boolean mainTxt;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams lp) {
            super(lp);
            mainTxt = lp.mainTxt;
        }
    }
}
