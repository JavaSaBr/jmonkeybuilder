package com.ss.builder.plugin.api.file.creator;

import static com.ss.builder.plugin.api.property.control.PropertyEditorControlFactory.build;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.plugin.api.property.control.PropertyEditorControl;
import com.ss.builder.plugin.api.property.control.PropertyEditorControlFactory;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.plugin.api.property.control.PropertyEditorControl;
import com.ss.builder.fx.component.creator.impl.AbstractFileCreator;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
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

    /**
     * The result vars of the creator.
     */
    @NotNull
    protected final VarTable vars;

    /**
     * The settings container.
     */
    @Nullable
    private GridPane settingsContainer;

    public GenericFileCreator() {
        this.vars = VarTable.newInstance();
    }

    @Override
    protected void createSettings(@NotNull GridPane root) {
        super.createSettings(root);

        this.settingsContainer = root;

        var rowIndex = 1;

        for (var definition : getPropertyDefinitions()) {

            var control = PropertyEditorControlFactory.build(vars, definition, this::validateFileName);
            control.prefWidthProperty()
                    .bind(widthProperty());

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

        getSettingsContainer().getChildren()
                .stream()
                .filter(PropertyEditorControl.class::isInstance)
                .map(PropertyEditorControl.class::cast)
                .forEach(PropertyEditorControl::checkDependency);

        var okButton = getOkButton();
        if (okButton == null) {
            return;
        }

        var result = validate(vars);

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
        writeData(vars, resultFile);
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
        return Array.empty();
    }
}
