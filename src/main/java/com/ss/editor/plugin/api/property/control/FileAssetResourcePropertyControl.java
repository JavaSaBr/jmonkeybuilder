package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to edit file values from asset folder.
 *
 * @author JavaSaBr
 */
public class FileAssetResourcePropertyControl extends AssetResourcePropertyEditorControl<Path> {

    private static final Predicate<Class<?>> ACTION_TESTER =
            type -> type == NewFileAction.class;

    /**
     * The list of target extensions.
     */
    @NotNull
    private final Array<String> extensions;

    public FileAssetResourcePropertyControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.extensions = ArrayFactory.asArray(notNull(definition.getExtension()));
    }

    @Override
    @FromAnyThread
    public @NotNull Predicate<Class<?>> getActionTester() {
        return ACTION_TESTER;
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<String> getExtensions() {
        return extensions;
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
                .orElse(NOT_SELECTED));

        super.reloadImpl();
    }
}
