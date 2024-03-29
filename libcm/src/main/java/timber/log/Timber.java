package timber.log;

import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logging for lazy people.
 */
public final class Timber {
    /**
     * Log a verbose message with optional format args.
     */
    public static void v(String message, Object... args) {
        TREE_OF_SOULS.v(message, args);
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    public static void v(Throwable t, String message, Object... args) {
        TREE_OF_SOULS.v(t, message, args);
    }

    /**
     * Log a debug message with optional format args.
     */
    public static void d(String message, Object... args) {
        TREE_OF_SOULS.d(message, args);
    }

    /**
     * Log a debug exception and a message with optional format args.
     */
    public static void d(Throwable t, String message, Object... args) {
        TREE_OF_SOULS.d(t, message, args);
    }

    /**
     * Log an info message with optional format args.
     */
    public static void i(String message, Object... args) {
        TREE_OF_SOULS.i(message, args);
    }

    /**
     * Log an info exception and a message with optional format args.
     */
    public static void i(Throwable t, String message, Object... args) {
        TREE_OF_SOULS.i(t, message, args);
    }

    /**
     * Log a warning message with optional format args.
     */
    public static void w(String message, Object... args) {
        TREE_OF_SOULS.w(message, args);
    }

    /**
     * Log a warning exception and a message with optional format args.
     */
    public static void w(Throwable t, String message, Object... args) {
        TREE_OF_SOULS.w(t, message, args);
    }

    /**
     * Log an error message with optional format args.
     */
    public static void e(String message, Object... args) {
        TREE_OF_SOULS.e(message, args);
    }

    /**
     * Log an error exception and a message with optional format args.
     */
    public static void e(Throwable t, String message, Object... args) {
        TREE_OF_SOULS.e(t, message, args);
    }

    /**
     * Log an assert message with optional format args.
     */
    public static void wtf(String message, Object... args) {
        TREE_OF_SOULS.wtf(message, args);
    }

    /**
     * Log an assert exception and a message with optional format args.
     */
    public static void wtf(Throwable t, String message, Object... args) {
        TREE_OF_SOULS.wtf(t, message, args);
    }

    /**
     * A view into Timber's planted trees as a tree itself. This can be used for injecting a logger
     * instance rather than using static methods or to facilitate testing.
     */
    public static Tree asTree() {
        return TREE_OF_SOULS;
    }

    /**
     * Set a one-time tag for use on the next logging call.
     */
    public static Tree tag(String tag) {
        List<Tree> forest = FOREST;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, count = forest.size(); i < count; i++) {
            forest.get(i).explicitTag.set(tag);
        }
        return TREE_OF_SOULS;
    }

    /**
     * Add a new logging tree.
     */
    public static void plant(Tree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        if (tree == TREE_OF_SOULS) {
            throw new IllegalArgumentException("Cannot plant Timber into itself.");
        }
        FOREST.add(tree);
    }

    /**
     * Remove a planted tree.
     */
    public static void uproot(Tree tree) {
        if (!FOREST.remove(tree)) {
            throw new IllegalArgumentException("Cannot uproot tree which is not planted: " + tree);
        }
    }

    /**
     * Remove all planted trees.
     */
    public static void uprootAll() {
        FOREST.clear();
    }

    private static final List<Tree> FOREST = new CopyOnWriteArrayList<Tree>();

    /**
     * A {@link Tree} that delegates to all planted trees in the {@linkplain #FOREST forest}.
     */
    private static final Tree TREE_OF_SOULS = new Tree() {
        @Override public void v(String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).v(message, args);
            }
        }

        @Override public void v(Throwable t, String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).v(t, message, args);
            }
        }

        @Override public void d(String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).d(message, args);
            }
        }

        @Override public void d(Throwable t, String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).d(t, message, args);
            }
        }

        @Override public void i(String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).i(message, args);
            }
        }

        @Override public void i(Throwable t, String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).i(t, message, args);
            }
        }

        @Override public void w(String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).w(message, args);
            }
        }

        @Override public void w(Throwable t, String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).w(t, message, args);
            }
        }

        @Override public void e(String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).e(message, args);
            }
        }

        @Override public void e(Throwable t, String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).e(t, message, args);
            }
        }

        @Override public void wtf(String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).wtf(message, args);
            }
        }

        @Override public void wtf(Throwable t, String message, Object... args) {
            List<Tree> forest = FOREST;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.size(); i < count; i++) {
                forest.get(i).wtf(t, message, args);
            }
        }

        @Override protected void log(int priority, String tag, String message, Throwable t) {
            throw new AssertionError("Missing override for log method.");
        }
    };

    private Timber() {
        throw new AssertionError("No instances.");
    }

    /**
     * A facade for handling logging calls. Install instances via {@link #plant Timber.plant()}.
     */
    public static abstract class Tree {
        private final ThreadLocal<String> explicitTag = new ThreadLocal<String>();

        String getTag() {
            String tag = explicitTag.get();
            if (tag != null) {
                explicitTag.remove();
            }
            return tag;
        }

        /**
         * Log a verbose message with optional format args.
         */
        public void v(String message, Object... args) {
            prepareLog(Log.VERBOSE, null, message, args);
        }

        /**
         * Log a verbose exception and a message with optional format args.
         */
        public void v(Throwable t, String message, Object... args) {
            prepareLog(Log.VERBOSE, t, message, args);
        }

        /**
         * Log a debug message with optional format args.
         */
        public void d(String message, Object... args) {
            prepareLog(Log.DEBUG, null, message, args);
        }

        /**
         * Log a debug exception and a message with optional format args.
         */
        public void d(Throwable t, String message, Object... args) {
            prepareLog(Log.DEBUG, t, message, args);
        }

        /**
         * Log an info message with optional format args.
         */
        public void i(String message, Object... args) {
            prepareLog(Log.INFO, null, message, args);
        }

        /**
         * Log an info exception and a message with optional format args.
         */
        public void i(Throwable t, String message, Object... args) {
            prepareLog(Log.INFO, t, message, args);
        }

        /**
         * Log a warning message with optional format args.
         */
        public void w(String message, Object... args) {
            prepareLog(Log.WARN, null, message, args);
        }

        /**
         * Log a warning exception and a message with optional format args.
         */
        public void w(Throwable t, String message, Object... args) {
            prepareLog(Log.WARN, t, message, args);
        }

        /**
         * Log an error message with optional format args.
         */
        public void e(String message, Object... args) {
            prepareLog(Log.ERROR, null, message, args);
        }

        /**
         * Log an error exception and a message with optional format args.
         */
        public void e(Throwable t, String message, Object... args) {
            prepareLog(Log.ERROR, t, message, args);
        }

        /**
         * Log an assert message with optional format args.
         */
        public void wtf(String message, Object... args) {
            prepareLog(Log.ASSERT, null, message, args);
        }

        /**
         * Log an assert exception and a message with optional format args.
         */
        public void wtf(Throwable t, String message, Object... args) {
            prepareLog(Log.ASSERT, t, message, args);
        }

        /**
         * Return whether a message at {@code priority} should be logged.
         */
        protected boolean isLoggable(int priority) {
            return true;
        }

        private void prepareLog(int priority, Throwable t, String message, Object... args) {
            if (!isLoggable(priority)) {
                return;
            }
            if (message != null && message.length() == 0) {
                message = null;
            }
            if (message == null) {
                if (t == null) {
                    return; // Swallow message if it's null and there's no throwable.
                }
                message = Log.getStackTraceString(t);
            } else {
                if (args.length > 0) {
                    message = String.format(message, args);
                }
                if (t != null) {
                    message += "\n" + Log.getStackTraceString(t);
                }
            }

            log(priority, getTag(), message, t);
        }

        /**
         * Write a log message to its destination. Called for all level-specific methods by default.
         *
         * @param priority Log level. See {@link Log} for constants.
         * @param tag      Explicit or inferred tag. May be {@code null}.
         * @param message  Formatted log message. May be {@code null}, but then {@code t} will not be.
         * @param t        Accompanying exceptions. May be {@code null}, but then {@code message} will not be.
         */
        protected abstract void log(int priority, String tag, String message, Throwable t);
    }

    /**
     * A {@link Tree Tree} for debug builds. Automatically infers the tag from the calling class.
     */
    public static class DebugTree extends Tree {
        private static final int MAX_LOG_LENGTH = 4000;
        private static final int CALL_STACK_INDEX = 5;
        private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

        /**
         * Extract the tag which should be used for the message from the {@code element}. By default
         * this will use the class name without any anonymous class suffixes (e.g., {@code Foo$1}
         * becomes {@code Foo}).
         * <p>
         * Note: This will not be called if a {@linkplain #tag(String) manual tag} was specified.
         */
        protected String createStackElementTag(StackTraceElement element) {
            String tag = element.getClassName();
            Matcher m = ANONYMOUS_CLASS.matcher(tag);
            if (m.find()) {
                tag = m.replaceAll("");
            }
            return tag.substring(tag.lastIndexOf('.') + 1);
        }

        @Override final String getTag() {
            String tag = super.getTag();
            if (tag != null) {
                return tag;
            }

            // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
            // because Robolectric runs them on the JVM but on Android the elements are different.
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace.length <= CALL_STACK_INDEX) {
                throw new IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using proguard?");
            }
            return createStackElementTag(stackTrace[CALL_STACK_INDEX]);
        }

        /**
         * Break up {@code message} into maximum-length chunks (if needed) and send to either
         * {@link Log#println(int, String, String) Log.println()} or
         * {@link Log#wtf(String, String) Log.wtf()} for logging.
         * <p>
         * {@inheritDoc}
         */
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (message.length() < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, message);
                } else {
                    Log.println(priority, tag, message);
                }
                return;
            }

            // Split by line, then ensure each line can fit into Log's maximum length.
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + MAX_LOG_LENGTH);
                    String part = message.substring(i, end);
                    if (priority == Log.ASSERT) {
                        Log.wtf(tag, part);
                    } else {
                        Log.println(priority, tag, part);
                    }
                    i = end;
                } while (i < newline);
            }
        }
    }
}
