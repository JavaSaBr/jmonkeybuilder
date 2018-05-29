package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to edit resource values from asset.
 *
 * @author JavaSaBr
 */
public abstract class AssetResourcePropertyEditorControl<T> extends ResourcePropertyEditorControl<T> {

    private static final Predicate<Class<?>> DEFAULT_ACTION_TESTER = type -> false;

    private static final Array<String> DEFAULT_EXTENSIONS = ArrayFactory.newArray(String.class);

    protected AssetResourcePropertyEditorControl(
            @NotNull final VarTable vars,
            @NotNull final PropertyDefinition definition,
            @NotNull final Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    /**
     * Get the action tester for asset dialog.
     *
     * @return the action tester.
     */
    @FromAnyThread
    protected @NotNull Predicate<Class<?>> getActionTester() {
        return DEFAULT_ACTION_TESTER;
    }

    /**
     * Gets a list with available extensions.
     *
     * @return the list with available extensions.
     */
    @FromAnyThread
    protected @NotNull Array<String> getExtensions() {
        return DEFAULT_EXTENSIONS;
    }

    @Override
    @FxThread
    protected void chooseNew() {
        super.chooseNew();
        UiUtils.openFileAssetDialog(this::chooseNew, getExtensions(), getActionTester());
    }

    /**
     * Choose the new resource by the file.
     *
     * @param file the selected file.
     */
    @FxThread
    protected void chooseNew(@NotNull Path file) {
        change();
        reload();
    }
}
