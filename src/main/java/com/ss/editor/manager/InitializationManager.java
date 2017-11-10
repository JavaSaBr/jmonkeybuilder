package com.ss.editor.manager;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JMEThread;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The class to manage initialization of some classes.
 *
 * @author JavaSaBr
 */
public class InitializationManager {

    private static InitializationManager instance;

    public static @NotNull InitializationManager getInstance() {
        if (instance == null) instance = new InitializationManager();
        return instance;
    }

    /**
     * The list of actions to execute before creating jME context.
     */
    @NotNull
    private final Array<Runnable> onBeforeCreateJMEContext;

    /**
     * The list of actions to execute after creating jME context.
     */
    @NotNull
    private final Array<Runnable> onAfterCreateJMEContext;

    /**
     * The list of actions to execute before creating JavaFX context.
     */
    @NotNull
    private final Array<Runnable> onBeforeCreateJavaFXContext;

    /**
     * The list of actions to execute after creating JavaFX context.
     */
    @NotNull
    private final Array<Runnable> onAfterCreateJavaFXContext;

    /**
     * The list of actions to execute when the editor finish loading.
     */
    @NotNull
    private final Array<Runnable> onFinishLoading;

    private InitializationManager() {
        InitializeManager.valid(getClass());

        this.onBeforeCreateJMEContext = ArrayFactory.newArray(Runnable.class);
        this.onAfterCreateJMEContext = ArrayFactory.newArray(Runnable.class);
        this.onBeforeCreateJavaFXContext = ArrayFactory.newArray(Runnable.class);
        this.onAfterCreateJavaFXContext = ArrayFactory.newArray(Runnable.class);
        this.onFinishLoading = ArrayFactory.newArray(Runnable.class);
    }

    /**
     * Do some things before when JME context will be created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnBeforeCreateJMEContext(@NotNull final Runnable runnable) {
        this.onBeforeCreateJMEContext.add(runnable);
    }

    /**
     * Do some things after when JME context was created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnAfterCreateJMEContext(@NotNull final Runnable runnable) {
        this.onAfterCreateJMEContext.add(runnable);
    }

    /**
     * Do some things before when JavaFX context will be created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnBeforeCreateJavaFXContext(@NotNull final Runnable runnable) {
        this.onBeforeCreateJavaFXContext.add(runnable);
    }

    /**
     * Do some things after when JavaFX context was created.
     *
     * @param runnable the action.
     */
    @FromAnyThread
    public synchronized void addOnAfterCreateJavaFXContext(@NotNull final Runnable runnable) {
        this.onAfterCreateJavaFXContext.add(runnable);
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
     * Execute all actions before when JME context will be created.
     */
    @JMEThread
    public synchronized void onBeforeCreateJMEContext() {
        onBeforeCreateJavaFXContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions after when JME context was created.
     */
    @JMEThread
    public synchronized void onAfterCreateJMEContext() {
        onAfterCreateJMEContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions before when JavaFX context will be created.
     */
    @FXThread
    public synchronized void onBeforeCreateJavaFXContext() {
        onBeforeCreateJavaFXContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions after when JavaFX context was created.
     */
    @FXThread
    public synchronized void onAfterCreateJavaFXContext() {
        onAfterCreateJavaFXContext.forEach(Runnable::run);
    }

    /**
     * Execute all actions before when the editor is ready to work.
     */
    @FXThread
    public synchronized void onFinishLoading() {
        onFinishLoading.forEach(Runnable::run);
    }
}
