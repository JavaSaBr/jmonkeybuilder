package com.ss.editor.plugin.api.property.control;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to edit folder values from asset folder.
 *
 * @author JavaSaBr
 */
public class FolderAssetResourcePropertyControl extends AssetResourcePropertyEditorControl<Path> {

    @NotNull
    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class;

    public FolderAssetResourcePropertyControl(@NotNull final VarTable vars,
                                              @NotNull final PropertyDefinition definition,
                                              @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FromAnyThread
    public @NotNull Predicate<Class<?>> getActionTester() {
        return ACTION_TESTER;
    }

    @Override
    @FxThread
    protected void processSelect() {
        UiUtils.openFolderAssetDialog(this::processSelect, getActionTester());
    }

    @Override
    @FxThread
    protected void processSelect(@NotNull final Path file) {
        setPropertyValue(notNull(getAssetFile(file)));
        super.processSelect(file);
    }

    @Override
    @FxThread
    public void reload() {

        final Path file = getPropertyValue();
        final String assetPath = file == null ? NOT_SELECTED : toAssetPath(file);

        final Label resourceLabel = getResourceLabel();
        resourceLabel.setText(StringUtils.isEmpty(assetPath) ? "/" : assetPath);

        super.reload();
    }
}
