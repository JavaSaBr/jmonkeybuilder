package com.ss.builder.ui.control.tree.action.impl;

import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.editor.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.*;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.builder.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.builder.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.builder.ui.control.property.operation.PropertyOperation;
import com.ss.builder.ui.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.util.MaterialSerializer;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.util.MaterialSerializer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The implementation of the {@link AbstractNodeAction} to save a material as file.
 *
 * @author JavaSaBr
 */
public class SaveAsMaterialAction extends AbstractNodeAction<ChangeConsumer> {

    @NotNull
    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    public SaveAsMaterialAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.SAVE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_SAVE_AS;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();
        UiUtils.openSaveAsDialog(this::processSave, FileExtensions.JME_MATERIAL, ACTION_TESTER);
    }

    /**
     * The process of saving the file.
     *
     * @param file the file to save
     */
    @FxThread
    private void processSave(@NotNull final Path file) {

        final TreeNode<?> node = getNode();
        final Material material = (Material) node.getElement();
        final String materialContent = MaterialSerializer.serializeToString(material);

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(file, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(materialContent);
        } catch (final IOException e) {
            EditorUtils.handleException(LOGGER, this, e);
            return;
        }

        final Path assetFile = notNull(EditorUtils.getAssetFile(file));
        final AssetManager assetManager = EditorUtils.getAssetManager();
        final Material savedMaterial = assetManager.loadMaterial(notNull(EditorUtils.toAssetPath(assetFile)));

        final PropertyOperation<ChangeConsumer, Material, AssetKey> operation =
                new PropertyOperation<>(material, "AssetKey", savedMaterial.getKey(), null);
        operation.setApplyHandler(Material::setKey);

        final ChangeConsumer changeConsumer = notNull(getNodeTree().getChangeConsumer());
        changeConsumer.execute(operation);
    }
}
