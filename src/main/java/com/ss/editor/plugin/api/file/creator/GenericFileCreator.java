package com.ss.editor.plugin.api.file.creator;

import static com.ss.editor.plugin.api.property.control.PropertyEditorControlFactory.build;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.plugin.api.property.control.PropertyEditorControl;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The generic implementation of file creator dialog.
 *
 * @author JavaSaBr
 */
public class GenericFileCreator extends AbstractFileCreator {

    @NotNull
    private static final Array<PropertyDefinition> EMPTY_ARRAY = ArrayFactory.asArray();

    /**
     * The settings container.
     */
    @Nullable
    private GridPane settingsContainer;

    /**
     * The result vars of the creator.
     */
    @Nullable
    private VarTable vars;

    public GenericFileCreator() {
    }

    @Override
    protected void createSettings(@NotNull GridPane root) {
        super.createSettings(root);

        this.settingsContainer = root;
        this.vars = VarTable.newInstance();

        var rowIndex = 1;

        for (var definition : getPropertyDefinitions()) {
            var control = build(vars, definition, this::validateFileName);
            control.prefWidthProperty().bind(widthProperty());
            root.add(control, 0, rowIndex++, 2, 1);
        }
    }

    @Override
    @FxThread
    public void show(@NotNull Window owner) {
        super.show(owner);
        validateFileName();
    }

    /**
     * @return the result vars of the creator.
     */
    @FxThread
    protected @NotNull VarTable getVars() {
        return notNull(vars);
    }

    /**
     * Get the settings container.
     *
     * @return the settings container.
     */
    @FxThread
    private @NotNull GridPane getSettingsContainer() {
        return notNull(settingsContainer);
    }

    @Override
    @FxThread
    protected void validateFileName() {
        super.validateFileName();

        var settingsContainer = getSettingsContainer();
        settingsContainer.getChildren().stream()
                .filter(PropertyEditorControl.class::isInstance)
                .map(PropertyEditorControl.class::cast)
                .forEach(PropertyEditorControl::checkDependency);

        var okButton = getOkButton();
        if (okButton == null) {
            return;
        }

        var result = validate(getVars());

        if (!okButton.isDisabled()) {
            okButton.setDisable(!result);
        }
    }

    /**
     * Validate variables.
     *
     * @param vars the variables.
     * @return true if the all variables are valid.
     */
    @FxThread
    protected boolean validate(@NotNull VarTable vars) {
        return true;
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull Path resultFile) throws IOException {
        writeData(getVars(), resultFile);
    }

    /**
     * Write created data to the created file.
     *
     * @param vars       the available variables.
     * @param resultFile the result file.
     * @throws IOException if was some problem with writing to the result file.
     */
    @BackgroundThread
    protected void writeData(@NotNull VarTable vars, @NotNull Path resultFile) throws IOException {
    }

    /**
     * Get the list of property definitions.
     *
     * @return the list of property definitions.
     */
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        return EMPTY_ARRAY;
    }
}
