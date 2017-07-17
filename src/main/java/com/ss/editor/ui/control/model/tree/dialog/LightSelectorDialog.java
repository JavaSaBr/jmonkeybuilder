package com.ss.editor.ui.control.model.tree.dialog;

import com.jme3.light.Light;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The implementation of the Node Selector dialog to select a light.
 *
 * @param <T> the type of light
 * @author JavaSaBr
 */
public class LightSelectorDialog<T extends Light> extends NodeSelectorDialog<T> {

    /**
     * Instantiates a new Light selector dialog.
     *
     * @param model   the model
     * @param type    the type
     * @param handler the handler
     */
    public LightSelectorDialog(@NotNull final Spatial model, @NotNull final Class<T> type,
                               @NotNull final Consumer<T> handler) {
        super(model, type, handler);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.LIGHT_SELECTOR_DIALOG_TITLE;
    }
}
