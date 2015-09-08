package timber.log;

import android.util.Log;

/**
 * Created by llf on 2015/8/11.
 */
public class ReleaseTree extends Timber.Tree{

    public ReleaseTree(){
    }

    @Override protected void log(int priority, String tag, String message, Throwable t) {
        if(priority == Log.ASSERT || priority == Log.DEBUG || priority == Log.VERBOSE)
            return;
        Log.println(priority,tag,message +Log.getStackTraceString(t));
    }
}
