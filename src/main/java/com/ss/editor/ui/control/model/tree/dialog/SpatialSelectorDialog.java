package com.ss.editor.ui.control.model.tree.dialog;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The implementation of the Node Selector dialog to select a spatial.
 *
 * @param <T> the type of light
 * @author JavaSaBr
 */
public class SpatialSelectorDialog<T extends Spatial> extends NodeSelectorDialog<T> {

    /**
     * Instantiates a new spatial selector dialog.
     *
     * @param model   the model
     * @param type    the type
     * @param handler the handler
     */
    public SpatialSelectorDialog(@NotNull final Spatial model, @NotNull final Class<T> type,
                                 @NotNull final Consumer<T> handler) {
        super(model, type, handler);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.NODE_SELECTOR_DIALOG_TITLE;
    }
}
