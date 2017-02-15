package com.ss.editor.ui.component.editing.terrain;

import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.ui.component.editing.impl.AbstractEditingComponent;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a terrain editor.
 *
 * @author JavaSaBr
 */
public class TerrainEditingComponent extends AbstractEditingComponent<TerrainQuad> {

    /**
     * The button to enable/disable raise terrain mode.
     */
    @Nullable
    private ToggleButton raiseTerrainButton;

    @Override
    protected void createComponents() {
        super.createComponents();

        raiseTerrainButton = new ToggleButton("Raise");
        raiseTerrainButton.setOnAction(this::switchMode);

        final GridPane buttonsContainer = new GridPane();
        buttonsContainer.add(raiseTerrainButton, 0, 0);

        FXUtils.addToPane(buttonsContainer, this);
    }

    private void switchMode(@NotNull final ActionEvent event) {

    }

    @Override
    public boolean isSupport(@NotNull final Object object) {
        return object instanceof TerrainQuad;
    }

    @NotNull
    @Override
    public String getName() {
        return "Terrain editor";
    }
}
