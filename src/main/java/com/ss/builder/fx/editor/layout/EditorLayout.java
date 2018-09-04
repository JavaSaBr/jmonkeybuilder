package com.ss.builder.fx.editor.layout;

import com.ss.builder.annotation.FromAnyThread;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement layout container of Editor.
 *
 * @author JavaSaBr
 */
public interface EditorLayout {

    @FromAnyThread
    @NotNull Parent getRootPage();

    @FromAnyThread
    @NotNull Parent getContainer();
}
