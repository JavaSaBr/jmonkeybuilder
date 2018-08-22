package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import org.jetbrains.annotations.NotNull;

/**
 * The control to edit resource values from classpath.
 *
 * @author JavaSaBr
 */
public class ClasspathResourcePropertyControl extends ResourcePropertyEditorControl<String> {

    /**
     * The target extension.
     */
    @NotNull
    private final String extension;

    public ClasspathResourcePropertyControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.extension = notNull(definition.getExtension());
    }

    @Override
    @FxThread
    protected void chooseNewResource() {
        super.chooseNewResource();

        var resources = ResourceManager.getInstance()
                .getAvailableResources(extension);

        UiUtils.openResourceAssetDialog(this::chooseNewResource, this::validate, resources);
    }

    /**
     * Choose the new resource by the path
     *
     * @param resource the selected resource.
     */
    @FxThread
    private void chooseNewResource(@NotNull String resource) {
        setPropertyValue(resource);
        changed();
        reload();
    }

    /**
     * Validate the selected resource.
     *
     * @param resource the selected resource.
     * @return the message of problems or null if all are ok.
     */
    @FxThread
    private String validate(@NotNull String resource) {

        var extension = FileUtils.getExtension(resource);

        if (StringUtils.isEmpty(extension)) {
            return Messages.ASSET_EDITOR_DIALOG_WARNING_SELECT_FILE;
        }

        return null;
    }

    @Override
    @FxThread
    protected void reloadImpl() {
        resourceLabel.setText(StringUtils.ifEmpty(getPropertyValue(), NOT_SELECTED));
        super.reloadImpl();
    }
}
