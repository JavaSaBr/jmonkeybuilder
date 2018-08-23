package com.ss.builder.fx.editor.layout;

import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement layout container of Editor.
 *
 * @author JavaSaBr
 */
public interface EditorLayout {

    @NotNull Parent getRootPage();

    @NotNull Parent getContainer();
}
