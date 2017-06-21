package com.ss.editor.ui.control.model.tree.dialog.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The implementation of node selector dialog to select a geometry.
 *
 * @author JavaSaBr
 */
public class GeometrySelectorDialog extends NodeSelectorDialog<Geometry> {

    /**
     * Instantiates a new Geometry selector dialog.
     *
     * @param model   the model
     * @param handler the handler
     */
    public GeometrySelectorDialog(@NotNull final Spatial model, @NotNull final Consumer<Geometry> handler) {
        super(model, Geometry.class, handler);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.GEOMETRY_SELECTOR_DIALOG_TITLE;
    }
}
