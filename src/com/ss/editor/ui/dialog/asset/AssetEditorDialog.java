package com.ss.editor.ui.dialog.asset;

import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.EditorDialog;

import java.awt.*;
import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

import static com.ss.editor.Messages.ASSET_EDITOR_DIALOG_TITLE;
import static com.ss.editor.ui.css.CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CANCEL;
import static com.ss.editor.ui.css.CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER;
import static com.ss.editor.ui.css.CSSIds.ASSET_EDITOR_DIALOG_BUTTON_OK;

/**
 * Реализация диалога для выбора объекта из Asset.
 *
 * @author Ronn
 */
public class AssetEditorDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);

    /**
     * Функция для использования выбранного ресурса.
     */
    private final Consumer<Path> consumer;

    /**
     * Дерево ресурсов.
     */
    private ResourceTree resourceTree;

    public AssetEditorDialog(final Consumer<Path> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void createContent(final VBox root) {

        final EditorConfig editorConfig = EditorConfig.getInstance();

        resourceTree = new ResourceTree();
        resourceTree.fill(editorConfig.getCurrentAsset());
        resourceTree.prefHeightProperty().bind(root.heightProperty());

        FXUtils.addToPane(resourceTree, root);
    }

    @Override
    protected void createActions(final VBox root) {

        final HBox container = new HBox();
        container.setId(ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        final Button okButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_OK);
        okButton.setId(ASSET_EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processSelect());

        final Button cancelButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(ASSET_EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * @return функция для использования выбранного ресурса.
     */
    private Consumer<Path> getConsumer() {
        return consumer;
    }

    /**
     * @return дерево ресурсов.
     */
    private ResourceTree getResourceTree() {
        return resourceTree;
    }

    /**
     * Процесс выбора элемента.
     */
    private void processSelect() {

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            hide();
            return;
        }

        final ResourceElement element = selectedItem.getValue();
        final Path file = element.getFile();

        hide();

        final Consumer<Path> consumer = getConsumer();
        consumer.accept(file);
    }

    @Override
    protected String getTitleText() {
        return ASSET_EDITOR_DIALOG_TITLE;
    }

    @Override
    protected Point getSize() {
        return new Point(900, 600);
    }
}
