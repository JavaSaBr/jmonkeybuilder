package com.ss.editor.ui.dialog.factory.control;

import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to edit resource values from asset.
 *
 * @author JavaSaBr
 */
public abstract class AssetResourcePropertyEditorControl<T> extends ResourcePropertyEditorControl<T> {

    @NotNull
    private static final Predicate<Class<?>> DEFAULT_ACTION_TESTER = type -> false;

    @NotNull
    private static final Array<String> DEFAULT_EXTENSIONS = ArrayFactory.newArray(String.class);

    protected AssetResourcePropertyEditorControl(@NotNull final VarTable vars,
                                                 @NotNull final PropertyDefinition definition,
                                                 @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    /**
     * Gets an action tester for asset dialog.
     *
     * @return the action tester.
     */
    @NotNull
    protected Predicate<Class<?>> getActionTester() {
        return DEFAULT_ACTION_TESTER;
    }

    /**
     * Gets a list with available extensions.
     *
     * @return the list with available extensions.
     */
    @NotNull
    protected Array<String> getExtensions() {
        return DEFAULT_EXTENSIONS;
    }

    @Override
    protected void processSelect() {
        super.processSelect();
        UIUtils.openAssetDialog(this, this::processSelect, getExtensions(), getActionTester());
    }

    /**
     * Handles the selected file.
     *
     * @param file the selected file.
     */
    protected void processSelect(@NotNull final Path file) {
        change();
        reload();
    }
}
