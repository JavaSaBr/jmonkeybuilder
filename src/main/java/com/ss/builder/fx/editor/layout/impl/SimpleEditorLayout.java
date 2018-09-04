package com.ss.builder.fx.editor.layout.impl;

import com.ss.builder.annotation.FromAnyThread;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * The simple editor layout.
 *
 * @author JavaSaBr
 */
public class SimpleEditorLayout extends AbstractEditorLayout<VBox, VBox> {

    public SimpleEditorLayout() {
    }

    @Override
    @FromAnyThread
    public void build() {
        super.build();
    }

    @Override
    @FromAnyThread
    protected @NotNull VBox createContainer() {
        return rootPage;
    }


    @Override
    @FromAnyThread
    protected @NotNull VBox createRootPage() {
        return new VBox();
    }
}
