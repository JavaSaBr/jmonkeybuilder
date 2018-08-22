package com.ss.builder.ui.dialog.node.selector;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The implementation of the Node Selector dialog to select a spatial.
 *
 * @param <T> the type of light
 * @author JavaSaBr
 */
public class SpatialSelectorDialog<T extends Spatial> extends NodeSelectorDialog<T> {

    public SpatialSelectorDialog(@NotNull final Spatial model, @NotNull final Class<T> type,
                                 @NotNull final Consumer<T> handler) {
        super(model, type, handler);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.NODE_SELECTOR_DIALOG_TITLE;
    }
}
