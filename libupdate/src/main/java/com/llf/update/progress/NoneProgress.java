package com.llf.update.progress;

import com.llf.update.CheckBean;

import java.io.File;

/**
 * Created by llf on 2015/10/28.
 *
 * @email llfer2006@gmail.com
 */
public class NoneProgress implements IProgress {
    @Override public void show() {

    }

    @Override public void updateProgress(int progress) {

    }

    @Override public void updateRetry(CheckBean bean) {

    }

    @Override public void updateFinish(File apk) {

    }

    @Override public void dismiss() {

    }
}
