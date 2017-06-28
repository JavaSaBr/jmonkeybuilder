package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.MaterialKey;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

/**
 * The implementation of the {@link ModelPropertyControl} to edit the {@link MaterialKey}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class MaterialKeyModelPropertyControl<T extends Spatial> extends MaterialModelPropertyControl<T, MaterialKey> {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class || type == RenameFileAction.class;

    /**
     * Instantiates a new Material key model property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public MaterialKeyModelPropertyControl(@Nullable final MaterialKey element, @NotNull final String paramName,
                                           @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void processChange() {
        UIUtils.openAssetDialog(this::addMaterial, MATERIAL_EXTENSIONS, ACTION_TESTER);
    }

    @Override
    protected void addMaterial(@NotNull final Path file) {

        final Path assetFile = notNull(getAssetFile(file));
        final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));

        changed(materialKey, getPropertyValue());
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    @Override
    protected void processEdit() {

        final MaterialKey element = getPropertyValue();
        if (element == null) return;

        final String assetPath = element.getName();
        if (StringUtils.isEmpty(assetPath)) return;

        final Path assetFile = Paths.get(assetPath);
        final Path realFile = notNull(getRealFile(assetFile));
        if (!Files.exists(realFile)) return;

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(realFile);

        FX_EVENT_MANAGER.notify(event);
    }

    @Override
    protected void reload() {
        final MaterialKey element = getPropertyValue();
        final Label materialLabel = getMaterialLabel();
        materialLabel.setText(element == null || StringUtils.isEmpty(element.getName()) ? NO_MATERIAL : element.getName());
    }
}
