package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import javafx.scene.control.Label;
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

    public ClasspathResourcePropertyControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                            @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
        this.extension = notNull(definition.getExtension());
    }

    @Override
    @FXThread
    protected void processSelect() {
        super.processSelect();

        final ResourceManager resourceManager = ResourceManager.getInstance();
        final Array<String> resources = resourceManager.getAvailableResources(extension);

        UIUtils.openResourceAssetDialog(this::processSelect, this::validate, resources);
    }

    /**
     * Handle selected resource.
     *
     * @param resource the selected resource.
     */
    @FXThread
    private void processSelect(@NotNull final String resource) {
        setPropertyValue(resource);
        change();
        reload();
    }

    /**
     * Validate the selected resource.
     *
     * @param resource the selected resource.
     * @return the message of problems or null if all are ok.
     */
    @FXThread
    private String validate(@NotNull final String resource) {

        final String extension = FileUtils.getExtension(resource);
        if (StringUtils.isEmpty(extension)) {
            return Messages.ASSET_EDITOR_DIALOG_WARNING_SELECT_FILE;
        }

        return null;
    }

    @Override
    @FXThread
    protected void reload() {

        final String resource = getPropertyValue();
        final Label resourceLabel = getResourceLabel();
        resourceLabel.setText(resource == null ? NOT_SELECTED : resource);

        super.reload();
    }
}
