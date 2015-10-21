package com.llflib.cm.ui.pullview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * Created by llf on 2015/8/25.
 */
public class PullRecycleView extends ViewGroup implements NestedScrollingParent {
    /**
     * Pull down to refresh
     * */
    public static final int MODE_PULL_DOWN = 0x1;
    /**
     * Pull up to load more
     * */
    public static final int MODE_PULL_UP = 0x2;
    /**
     * Pull down and Pull up
     * */
    public static final int MODE_BOTH = 0x3;

    public interface OnLoadListener {
        void onRefresh(PullRecycleView view);

        void onLoadMore(PullRecycleView view);
    }

    private RecyclerView mListView;
    private PullHeaderAndFooter mHeader, mFooter;

    private OnLoadListener mLoadListener;
    private Scroller mScroller;
    private Rect mTmp;
    private int mMode;

    public PullRecycleView(Context context) {
        this(context, null);
    }

    public PullRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRecycleWithAttrs(context, attrs, defStyleAttr);
    }

    void initRecycleWithAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        mScroller = new Scroller(context);
        mListView = new RecyclerView(context, attrs, defStyleAttr);
        addView(mListView, new LayoutParams(-1, -1));

        mHeader = new PullHeaderAndFooter(this);
        mFooter = new PullHeaderAndFooter(this);
        mHeader.setView(new PullHeaderView(context));
        mFooter.setView(new PullFooterView(context));

        //加载失败，重新加载。
        mFooter.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (mFooter.isError()) {
                    mFooter.changeToLoading();
                    notifyLoadMore();
                }
            }
        });
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkRelease();
                }
            }
        });
        mTmp = new Rect();
        mMode = MODE_BOTH;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mListView.setLayoutManager(layoutManager);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        mListView.addItemDecoration(decoration);
    }

    public void setLoadListener(OnLoadListener l) {
        mLoadListener = l;
    }

    public void setPullMode(int mode) {
        if (mMode != mode) {
            mMode = mode;
        }
    }

    public void setRefreshHeader(View header) {
        if (!(header instanceof IPull))
            throw new IllegalArgumentException("The header must implement IPull interface!");
        mHeader.setView(header);
    }

    public void setLoadFooter(View footer) {
        if (!(footer instanceof IPull))
            throw new IllegalArgumentException("The footer must implement IPull interface!");
        mFooter.setView(footer);
    }

    public void onComplete() {
        if (mHeader.isWorking()) {
            mHeader.changeToNormal();
        }
        if (mFooter.isWorking()) {
            mFooter.changeToNormal();
        }
        releaseToNormal();
    }

    public void onCompleteError() {
        if (mFooter.isWorking())
            mFooter.changeToError();
        else
            releaseToNormal();
    }

    public void setRefresh() {
        if(getScrollY() != 0)
            return;
        post(new Runnable() {
            @Override public void run() {
                if (mListView.getChildCount() > 0) {
                    View firstChild = mListView.getChildAt(0);
                    int pos = mListView.getChildAdapterPosition(firstChild);
                    if (pos == 0 && firstChild.getTop() >= 0 && firstChild.getLeft() >= 0) {
                        if (!mScroller.isFinished())
                            mScroller.abortAnimation();
                        mScroller.startScroll(0, 0, 0, -mHeader.getNormalHeight(), 2000);
                        mHeader.changeToLoading();
                        invalidate();
                    }
                }
            }
        });
    }

    public RecyclerView getListView() {
        return mListView;
    }

    void notifyLoadMore() {
        if (mLoadListener != null)
            mLoadListener.onLoadMore(this);
    }

    void notifyRefresh() {
        if (mLoadListener != null)
            mLoadListener.onRefresh(this);
    }

    void releaseToNormal() {
        int scrollY = getScrollY();
        if (scrollY == 0) {
            return;
        }
        if (!mScroller.isFinished())
            mScroller.abortAnimation();
        mScroller.startScroll(0, scrollY, 0, 0 - scrollY, 250);
        invalidate();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        measureChildren(widthMeasureSpec, childSpec);
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), resolveSize(
                getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        //header
        mHeader.layout(mTmp, true);
        //Content
        mTmp.set(0, 0, mListView.getMeasuredWidth(), mListView.getMeasuredHeight());
        mTmp.offsetTo(left, top);
        mListView.layout(mTmp.left, mTmp.top, mTmp.right, mTmp.bottom);
        //footer
        mFooter.layout(mTmp, false);
    }

    @Override public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            overScrollBy(0, mScroller.getCurrY() - getScrollY(), 0, getScrollY(), 0, 0, 0,
                    computeVerticalScrollExtent(), false);
            invalidate();
        } else {
            super.computeScroll();
        }
    }

    void offsetChildrenTopAndBottom(int offset) {
        int scrollY = getScrollY();
        if (offset == 0 || (offset > 0 && scrollY >= 0 && (mHeader.isWorking() || mMode == MODE_PULL_DOWN)) || //Pull up
                (offset < 0 && scrollY <= 0 && //Pull down
                        (mFooter.isWorking() || mFooter.isError() || mMode == MODE_PULL_UP))) {
            return;
        }
        int maxOver = scrollY > 0 ? mFooter.getMaxHeight() : mHeader.getMaxHeight();
        overScrollBy(0, offset, 0, scrollY, 0, 0, 0, maxOver, true);
        scrollY = getScrollY();
        if (scrollY < 0) {
            if (Math.abs(scrollY) >= maxOver) {
                mHeader.changeToReadyIfNeed();
            } else {
                mHeader.changeToNormalIfNeed();
            }
        } else {
            if (scrollY >= maxOver) {
                mFooter.changeToReadyIfNeed();
            } else {
                mHeader.changeToNormalIfNeed();
            }
        }
        invalidate();
    }

    void checkRelease() {
        if (mHeader.isReady()) {
            mHeader.changeToLoading();
            notifyRefresh();
        }
        if (mFooter.isReady()) {
            mFooter.changeToLoading();
            notifyLoadMore();
        }
        //check scroll
        int available = 0;
        if (mHeader.isWorking()) {
            available = -mHeader.getNormalHeight();
        }
        if (mFooter.isWorking() || mFooter.isError()) {
            available = mFooter.getNormalHeight();
        }
        int scrollY = getScrollY();
        if (scrollY == 0 || scrollY == available)
            return;
        if (!mScroller.isFinished())
            mScroller.abortAnimation();
        mScroller.startScroll(0, scrollY, 0, available - scrollY, 250);
        invalidate();
    }

    @Override protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        scrollTo(scrollX, scrollY);
    }

    @Override protected int computeVerticalScrollExtent() {
        return getScrollY() > 0 ? mFooter.getMaxHeight() : mHeader.getMaxHeight();
    }

    @Override public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override public void onStopNestedScroll(View child) {
    }

    @Override public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override public void onNestedScrollAccepted(View child, View target, int axes) {
    }

    @Override public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        int scrollY = getScrollY();
        if (scrollY != 0) {
            if (scrollY < 0 && dy > 0) {
                consumed[1] = Math.min(dy, -scrollY);
            } else if (scrollY > 0 && dy < 0) {
                consumed[1] = Math.max(dy, -scrollY);

            }
            offsetChildrenTopAndBottom(consumed[1]);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        offsetChildrenTopAndBottom(dyUnconsumed);
    }

    @Override public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    class PullHeaderAndFooter {
        static final int STATE_NORMAL = 0x1;
        static final int STATE_READY = 0x2;
        static final int STATE_LOADING = 0x3;
        static final int STATE_FAILED = 0x4;

        View mItem;
        IPull mPullItem;
        OnClickListener mViewClick;
        PullRecycleView mPullContainer;

        int mState;

        public PullHeaderAndFooter(PullRecycleView recycleView) {
            mPullContainer = recycleView;
            changeToNormal();
        }

        public void setView(View view) {
            if (mItem != view) {
                if (mItem != null)
                    mPullContainer.removeView(mItem);
                mItem = view;
                mPullItem = (IPull) view;
                mItem.setOnClickListener(mViewClick);
                mPullContainer.addView(mItem);
                changeState(mState, false);
                requestLayout();
            }
        }

        public void setOnClickListener(OnClickListener l) {
            mViewClick = l;
            if (mItem != null)
                mItem.setOnClickListener(l);
        }

        public void layout(Rect rect, boolean header) {
            if (mItem == null)
                return;
            if (header) {
                rect.set(0, -mItem.getMeasuredHeight(), mItem.getMeasuredWidth(), 0);
            } else {
                rect.set(0, 0, mItem.getMeasuredWidth(), mItem.getMeasuredHeight());
                rect.offsetTo(0, mPullContainer.getMeasuredHeight());
            }
            mItem.layout(rect.left, rect.top, rect.right, rect.bottom);
        }

        public int getMaxHeight() {
            return (int) (getNormalHeight() * 1.2f);
        }

        public int getNormalHeight() {
            return mItem == null ? 0 : mItem.getHeight();
        }

        public boolean isReady() {
            return mState == STATE_READY;
        }

        public boolean isWorking() {
            return mState == STATE_LOADING;
        }

        public boolean isError() {
            return mState == STATE_FAILED;
        }

        public void changeToNormal() {
            changeState(STATE_NORMAL, true);
        }

        public void changeToReady() {
            changeState(STATE_READY, true);
        }

        public void changeToLoading() {
            changeState(STATE_LOADING, true);
        }

        public void changeToError() {
            changeState(STATE_FAILED, true);
        }

        public boolean changeToNormalIfNeed() {
            if (mState != STATE_LOADING && mState != STATE_FAILED) {
                changeState(STATE_NORMAL, true);
                return true;
            }
            return false;
        }

        public boolean changeToReadyIfNeed() {
            if (mState != STATE_LOADING && mState != STATE_FAILED) {
                changeState(STATE_READY, true);
                return true;
            }
            return false;
        }

        protected void changeState(int newState, boolean checkValue) {
            if (!checkValue || mState != newState) {
                mState = newState;
                if (mItem == null)
                    return;
                switch (newState) {
                    case STATE_NORMAL:
                        mPullItem.setNormalHit();
                        break;
                    case STATE_READY:
                        mPullItem.setReadyHit();
                        break;
                    case STATE_LOADING:
                        mPullItem.setLoadingHit();
                        break;
                    case STATE_FAILED:
                        mPullItem.setErrorHit();
                        break;
                }
            }
        }
    }
}
