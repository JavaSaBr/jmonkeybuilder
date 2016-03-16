package com.ss.editor.ui.dialog.asset;

import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

import static com.ss.editor.Messages.ASSET_EDITOR_DIALOG_TITLE;

/**
 * Реализация диалога для выбора объекта из Asset.
 *
 * @author Ronn
 */
public class AssetEditorDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);
    private static final Insets PREVIEW_OFFSET = new Insets(0, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);

    private static final Point DIALOG_SIZE = new Point(1200, 700);

    private static final JavaFXImageManager JAVA_FX_IMAGE_MANAGER = JavaFXImageManager.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Функция для использования выбранного ресурса.
     */
    private final Consumer<Path> consumer;

    /**
     * Дерево ресурсов.
     */
    private ResourceTree resourceTree;

    /**
     * Превью картинок.
     */
    private ImageView imageView;

    public AssetEditorDialog(final Consumer<Path> consumer) {
        this.consumer = consumer;
    }

    /**
     * @param extensionFilter список фильтруемых расширений.
     */
    public void setExtensionFilter(final Array<String> extensionFilter) {
        resourceTree.setExtensionFilter(extensionFilter);
    }

    @Override
    protected void createContent(final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Consumer<ResourceElement> openFunction = element -> {

            hide();

            final Consumer<Path> consumer = getConsumer();
            consumer.accept(element.getFile());
        };

        resourceTree = new ResourceTree(openFunction, true);
        resourceTree.prefHeightProperty().bind(root.heightProperty());
        resourceTree.prefWidthProperty().bind(root.widthProperty());
        resourceTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processSelected(newValue));

        final VBox previewContainer = new VBox();
        previewContainer.setId(CSSIds.ASSET_EDITOR_DIALOG_PREVIEW_CONTAINER);

        imageView = new ImageView();
        imageView.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        imageView.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));

        FXUtils.addToPane(resourceTree, container);
        FXUtils.addToPane(imageView, previewContainer);
        FXUtils.addToPane(previewContainer, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(previewContainer, PREVIEW_OFFSET);

        root.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                processSelect();
            }
        });
    }

    @Override
    public void show(final Window owner) {
        super.show(owner);

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(editorConfig.getCurrentAsset());

        EXECUTOR_MANAGER.addFXTask(resourceTree::requestFocus);
    }

    /**
     * @return превью картинок.
     */
    private ImageView getImageView() {
        return imageView;
    }

    /**
     * Обработка выбора в дереве элемента.
     */
    private void processSelected(final TreeItem<ResourceElement> newValue) {

        final ImageView imageView = getImageView();

        if (newValue == null) {
            imageView.setImage(null);
            return;
        }

        final ResourceElement element = newValue.getValue();
        final Path file = element.getFile();

        if (Files.isDirectory(file) || !JavaFXImageManager.isImage(file)) {
            imageView.setImage(null);
            return;
        }

        final Image preview = JAVA_FX_IMAGE_MANAGER.getTexturePreview(file, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());

        imageView.setImage(preview);
    }

    @Override
    protected void createActions(final VBox root) {

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        final Button okButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processSelect());

        final Button cancelButton = new Button(Messages.ASSET_EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
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
        return DIALOG_SIZE;
    }
}
