package com.ss.editor.util;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.Config;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The time tracker.
 *
 * @author JavaSaBr
 */
public class TimeTracker {

    private static final Logger LOGGER = LoggerManager.getLogger(TimeTracker.class);

    public static final int STARTPUL_LEVEL_1 = 0;
    public static final int STARTPUL_LEVEL_2 = 1;
    public static final int STARTPUL_LEVEL_3 = 2;
    public static final int STARTPUL_LEVEL_4 = 3;
    public static final int STARTPUL_LEVEL_5 = 4;
    public static final int STARTPUL_LEVEL_6 = 5;

    private static final TimeTracker[] STARTUP_TRACKERS = new TimeTracker[10];

    static {
        ArrayUtils.fill(STARTUP_TRACKERS, () -> new TimeTracker(() -> Config.DEV_DEBUG_STARTUP));
    }

    @FromAnyThread
    public static @NotNull TimeTracker getStartupTracker() {
        return getStartupTracker(0);
    }

    @FromAnyThread
    public static @NotNull TimeTracker getStartupTracker(int level) {
        return STARTUP_TRACKERS[level];
    }

    @NotNull
    private final BooleanSupplier condition;

    private volatile long time;

    private TimeTracker(@NotNull BooleanSupplier condition) {
        this.condition = condition;
    }

    @FromAnyThread
    public void start() {
        if (condition.getAsBoolean()) {
            time = System.currentTimeMillis();
        }
    }

    @FromAnyThread
    public void finish(@NotNull Supplier<String> messageFactory) {
        if (condition.getAsBoolean()) {
            LOGGER.info(messageFactory.get() + ": " + (System.currentTimeMillis() - time) + "ms.");
        }
    }

    @FromAnyThread
    public void finishAndStart(@NotNull Supplier<String> messageFactory) {
        if (condition.getAsBoolean()) {
            var currentTime = System.currentTimeMillis();
            LOGGER.info(messageFactory.get() + ": " + (currentTime - time) + "ms.");
            time = currentTime;
        }
    }
}
