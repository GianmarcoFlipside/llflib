package com.llflib.cm.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MyItemAnimator extends RecyclerView.ItemAnimator {
    List<RecyclerView.ViewHolder> mAnimationAddViewHolders = new ArrayList<>();

    //需要执行动画时会系统会调用，用户无需手动调用
    @Override public void runPendingAnimations() {
        Timber.i("ITemAnimator runPendingAnimations");
        if (!mAnimationAddViewHolders.isEmpty()) {
            View target;
            AnimatorSet animator;
            for (final RecyclerView.ViewHolder viewHolder : mAnimationAddViewHolders) {
                target = viewHolder.itemView;
                animator = new AnimatorSet();
                animator.playTogether(ObjectAnimator.ofFloat(target, "translationX", -target.getMeasuredWidth(), 0.0f),
                        ObjectAnimator.ofFloat(target, "alpha", target.getAlpha(), 1.0f));
                animator.setTarget(target);
                animator.setDuration(100);
                animator.start();
            }
        }
    }

    //remove时系统会调用，返回值表示是否需要执行动画
    @Override public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        Timber.i("ITemAnimator animateRemove " + viewHolder);
        return false;
    }

    //viewholder添加时系统会调用
    @Override public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        Timber.i("ITemAnimator animateAdd " + viewHolder);

        return mAnimationAddViewHolders.add(viewHolder);
    }

    @Override public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        Timber.i("ITemAnimator animateMove " + viewHolder);

        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft,
            int fromTop, int toLeft, int toTop) {
        Timber.i("ITemAnimator animateChange ");

        return false;
    }

    @Override public void endAnimation(RecyclerView.ViewHolder viewHolder) {
        Timber.i("ITemAnimator endAnimation " + viewHolder);
        mAnimationAddViewHolders.remove(viewHolder);
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    @Override public void endAnimations() {
        Timber.i("ITemAnimator endAnimations");

    }

    @Override public boolean isRunning() {
        Timber.i("ITemAnimator isRunning");
        return !mAnimationAddViewHolders.isEmpty();
    }
}