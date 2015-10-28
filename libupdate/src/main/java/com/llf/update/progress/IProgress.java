package com.llf.update.progress;


import com.llf.update.CheckBean;

import java.io.File;

/**
 * Created by llf on 2015/10/28.
 *
 * @email llfer2006@gmail.com
 */
public interface IProgress {

    void show();

    void updateProgress(int progress);

    void updateRetry(CheckBean bean);

    void updateFinish(File apk);

    void dismiss();
}
