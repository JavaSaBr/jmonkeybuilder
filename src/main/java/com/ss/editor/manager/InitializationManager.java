package com.ss.editor.manager;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The class to manage initialization of some classes.
 *
 * @author JavaSaBr
 */
@Deprecated
public class InitializationManager {

    @NotNull
    private static final InitializationManager INSTANCE = new InitializationManager();

    public static @NotNull InitializationManager getInstance() {
        return INSTANCE;
    }

    /**
     * The list of actions to execute after initialization all managers.
     */
    @NotNull
    private final Array<Runnable> onAfterInitializeManagers;

    /**
     * The list of actions to execute before creating jME context.
     */
    @NotNull
    private final Array<Runnable> onBeforeCreateJmeContext;

    /**
     * The list of actions to execute after creating jME context.
     */
    @NotNull
    private final Array<Runnable> onAfterCreateJmeContext;

    /**
     * The list of actions to execute before creating JavaFX context.
     */
    @NotNull
    private final Array<Runnable> onBeforeCreateJavaFxContext;

    /**
     * The list of actions to execute after creating JavaFX context.
     */
    @NotNull
    private final Array<Runnable> onAfterCreateJavaFxContext;

    /**
     * The list of actions to execute when the editor finish loading.
     */
    @NotNull
    private final Array<Runnable> onFinishLoading;

    private InitializationManager() {
        this.onBeforeCreateJmeContext = ArrayFactory.newArray(Runnable.class);
        this.onAfterCreateJmeContext = ArrayFactory.newArray(Runnable.class);
        this.onBeforeCreateJavaFxContext = ArrayFactory.newArray(Runnable.class);
        this.onAfterCreateJavaFxContext = ArrayFactory.newArray(Runnable.class);
        this.onFinishLoading = ArrayFactory.newArray(Runnable.class);
        this.onAfterInitializeManagers = ArrayFactory.newArray(Runnable.class);
    }

    /**
     * Do something after when {@link com.ss.rlib.common.manager.InitializeManager} creates all managers.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnAfterInitializeManagers(@NotNull Runnable runnable) {
        onAfterCreateJavaFxContext.add(runnable);
    }

    /**
     * Do some things before when jME context will be created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnBeforeCreateJmeContext(@NotNull final Runnable runnable) {
        this.onBeforeCreateJmeContext.add(runnable);
    }

    /**
     * Do some things after when jME context was created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnAfterCreateJmeContext(@NotNull final Runnable runnable) {
        this.onAfterCreateJmeContext.add(runnable);
    }

    /**
     * Do some things before when JavaFX context will be created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnBeforeCreateJavaFxContext(@NotNull final Runnable runnable) {
        this.onBeforeCreateJavaFxContext.add(runnable);
    }

    /**
     * Do some things after when JavaFX context was created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnAfterCreateJavaFxContext(@NotNull final Runnable runnable) {
        this.onAfterCreateJavaFxContext.add(runnable);
    }

    /**
     * Do some things before when the editor is ready to work.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnFinishLoading(@NotNull final Runnable runnable) {
        this.onFinishLoading.add(runnable);
    }

    /**
     * Execute all actions before when jME context will be created.
     */
    @FromAnyThread
    public synchronized void onAfterInitializeManagers() {
        onAfterInitializeManagers.forEach(Runnable::run);
    }

    /**
     * Execute all actions before when jME context will be created.
     */
    @JmeThread
    public synchronized void onBeforeCreateJmeContext() {
        onBeforeCreateJmeContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions after when jME context was created.
     */
    @JmeThread
    public synchronized void onAfterCreateJmeContext() {
        onAfterCreateJmeContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions before when JavaFX context will be created.
     */
    @FxThread
    public synchronized void onBeforeCreateJavaFxContext() {
        onBeforeCreateJavaFxContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions after when JavaFX context was created.
     */
    @FxThread
    public synchronized void onAfterCreateJavaFxContext() {
        onAfterCreateJavaFxContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions before when the editor is ready to work.
     */
    @FxThread
    public synchronized void onFinishLoading() {
        onFinishLoading.forEach(Runnable::run);
    }
}
