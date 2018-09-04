package com.ss.builder.fx.editor.layout.impl;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.editor.layout.EditorLayout;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of the {@link EditorLayout}
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorLayout<R extends Parent, C extends Parent> implements EditorLayout {

    /**
     * The root page.
     */
    @NotNull
    protected final R rootPage;

    /**
     * The container.
     */
    @NotNull
    protected final C container;

    protected AbstractEditorLayout() {
        this.rootPage = createRootPage();
        this.container = createContainer();
    }

    @FromAnyThread
    public void build() {
    }

    @FromAnyThread
    protected abstract @NotNull C createContainer();

    @FromAnyThread
    protected abstract @NotNull R createRootPage();

    @Override
    @FromAnyThread
    public @NotNull Parent getRootPage() {
        return rootPage;
    }

    @Override
    @FromAnyThread
    public @NotNull Parent getContainer() {
        return container;
    }
}
