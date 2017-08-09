package com.ss.editor.plugin.api.file.creator;

import static com.ss.editor.plugin.api.property.control.PropertyEditorControlFactory.build;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.plugin.api.property.control.PropertyEditorControl;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    private final VarTable vars;

    private GenericFileCreator() {
        this.vars = VarTable.newInstance();
    }

    @Override
    protected void createSettings(@NotNull final GridPane root) {
        super.createSettings(root);

        int rowIndex = 1;

        final Array<PropertyDefinition> definitions = getPropertyDefinitions();
        for (final PropertyDefinition definition : definitions) {
            final PropertyEditorControl<?> control = build(vars, definition, this::validate);
            control.prefWidthProperty().bind(widthProperty());
            root.add(control, 0, rowIndex, 2, 1);
        }
    }

    /**
     * @return the result vars of the creator.
     */
    @NotNull
    protected VarTable getVars() {
        return vars;
    }

    /**
     * Validate this creator.
     */
    private void validate() {

        validateFileName();

        final Button okButton = getOkButton();
        if (okButton == null || okButton.isDisabled()) {
            return;
        }

        validate(getVars());
    }

    /**
     * Validate variables.
     *
     * @param vars the variables.
     */
    protected void validate(@NotNull final VarTable vars) {
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull final Path resultFile) {
        writeData(getVars(), resultFile);
    }

    /**
     * Write created data to the created file.
     *
     * @param vars       the available variables.
     * @param resultFile the result file.
     */
    @BackgroundThread
    protected void writeData(@NotNull final VarTable vars, @NotNull final Path resultFile) {
    }

    @NotNull
    protected Array<PropertyDefinition> getPropertyDefinitions() {
        return EMPTY_ARRAY;
    }
}
