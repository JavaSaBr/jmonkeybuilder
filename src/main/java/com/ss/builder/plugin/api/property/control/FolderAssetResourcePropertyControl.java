package com.ss.builder.plugin.api.property.control;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to edit folder values from asset folder.
 *
 * @author JavaSaBr
 */
public class FolderAssetResourcePropertyControl extends AssetResourcePropertyEditorControl<Path> {

    private static final Predicate<Class<?>> ACTION_TESTER =
            type -> type == NewFileAction.class;

    public FolderAssetResourcePropertyControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FromAnyThread
    public @NotNull Predicate<Class<?>> getActionTester() {
        return ACTION_TESTER;
    }

    @Override
    @FxThread
    protected void chooseNewResource() {
        UiUtils.openFolderAssetDialog(this::chooseNewResource, getActionTester());
    }

    @Override
    @FxThread
    protected void chooseNewResource(@NotNull Path file) {
        setPropertyValue(EditorUtils.requireAssetFile(file));
        super.chooseNewResource(file);
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        resourceLabel.setText(getPropertyValueOpt()
                .map(EditorUtils::toAssetPath)
                .filter(StringUtils::isEmpty)
                .orElse("/"));

        super.reloadImpl();
    }
}
