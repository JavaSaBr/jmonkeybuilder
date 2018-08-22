package com.ss.builder.plugin.api.property.control;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to edit resource values from asset.
 *
 * @author JavaSaBr
 */
public abstract class AssetResourcePropertyEditorControl<T> extends ResourcePropertyEditorControl<T> {

    protected AssetResourcePropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
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
        return type -> false;
    }

    /**
     * Get a list with available extensions.
     *
     * @return the list with available extensions.
     */
    @FromAnyThread
    protected @NotNull Array<String> getExtensions() {
        return Array.empty();
    }

    @Override
    @FxThread
    protected void chooseNewResource() {
        super.chooseNewResource();
        UiUtils.openFileAssetDialog(this::chooseNewResource, getExtensions(), getActionTester());
    }

    /**
     * Choose the new resource by the file.
     *
     * @param file the selected file.
     */
    @FxThread
    protected void chooseNewResource(@NotNull Path file) {
        changed();
        reload();
    }
}
