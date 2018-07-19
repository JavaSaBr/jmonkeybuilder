package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtil;
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
        setPropertyValue(EditorUtil.requireAssetFile(file));
        super.chooseNewResource(file);
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        resourceLabel.setText(getPropertyValueOpt()
                .map(EditorUtil::toAssetPath)
                .filter(StringUtils::isEmpty)
                .orElse("/"));

        super.reloadImpl();
    }
}
