package com.ss.editor.plugin.api.file.creator;

import static com.ss.editor.plugin.api.property.control.PropertyEditorControlFactory.build;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.plugin.api.property.control.PropertyEditorControl;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.control.Button;
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
     * The result vars of the creator.
     */
    @Nullable
    private VarTable vars;

    public GenericFileCreator() {
    }

    @Override
    protected void createSettings(@NotNull final GridPane root) {
        super.createSettings(root);

        this.vars = VarTable.newInstance();

        int rowIndex = 1;

        final Array<PropertyDefinition> definitions = getPropertyDefinitions();
        for (final PropertyDefinition definition : definitions) {
            final PropertyEditorControl<?> control = build(vars, definition, this::validateFileName);
            control.prefWidthProperty().bind(widthProperty());
            root.add(control, 0, rowIndex++, 2, 1);
        }
    }

    @Override
    @FXThread
    public void show(final @NotNull Window owner) {
        super.show(owner);
        validateFileName();
    }

    /**
     * @return the result vars of the creator.
     */
    protected @NotNull VarTable getVars() {
        return notNull(vars);
    }

    @Override
    @FXThread
    protected void validateFileName() {
        super.validateFileName();

        final Button okButton = getOkButton();
        if (okButton == null) return;

        final boolean result = validate(getVars());

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
    @FXThread
    protected boolean validate(@NotNull final VarTable vars) {
        return true;
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull final Path resultFile) throws IOException {
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
    protected void writeData(@NotNull final VarTable vars, @NotNull final Path resultFile) throws IOException {
    }

    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        return EMPTY_ARRAY;
    }
}
