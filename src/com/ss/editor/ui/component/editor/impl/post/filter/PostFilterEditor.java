package com.ss.editor.ui.component.editor.impl.post.filter;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.Material;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.state.editor.impl.post.filter.PostFilterEditorState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.FileUtils;

import static com.ss.editor.FileExtensions.JME_MATERIAL;
import static com.ss.editor.FileExtensions.POST_FILTER_VIEW;
import static com.ss.editor.Messages.POST_FILTER_EDITOR_MATERIAL_LABEL;
import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;
import static com.ss.editor.ui.css.CSSClasses.TOOLBAR_BUTTON;
import static com.ss.editor.ui.css.CSSClasses.TRANSPARENT_LIST_VIEW;
import static com.ss.editor.ui.css.CSSIds.POST_FILTER_EDITOR_MATERIAL_FILTER_CONTAINER;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.geometry.Pos.TOP_RIGHT;

/**
 * Реализация редактора пост филтров.
 *
 * @author Ronn
 */
public class PostFilterEditor extends AbstractFileEditor<StackPane> {

    public static final EditorDescription DESCRIPTION;

    static {
        DESCRIPTION = new EditorDescription();
        DESCRIPTION.setConstructor(PostFilterEditor::new);
        DESCRIPTION.addExtension(POST_FILTER_VIEW);
    }

    /**
     * 3D часть этого редактора.
     */
    private final PostFilterEditorState editorState;

    /**
     * Список используемых матералов.
     */
    private ListView<Material> materialsView;

    /**
     * Кнопка добавления нового материала.
     */
    private Button addMaterial;

    public PostFilterEditor() {
        this.editorState = new PostFilterEditorState();
        addEditorState(editorState);
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(final HBox container) {
        FXUtils.addToPane(createSaveAction(), container);
    }

    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void createContent(final StackPane root) {
        root.setAlignment(TOP_RIGHT);

        final VBox materialListContainer = new VBox();
        materialListContainer.setId(POST_FILTER_EDITOR_MATERIAL_FILTER_CONTAINER);

        final Label titleLabel = new Label();
        titleLabel.setText(POST_FILTER_EDITOR_MATERIAL_LABEL);
        titleLabel.setAlignment(CENTER_LEFT);

        materialsView = new ListView<>();
        materialsView.setCellFactory(param -> new MaterialListCell(this));
        materialsView.setOnDragOver(this::dragOver);
        materialsView.setOnDragDropped(this::dragDropped);
        materialsView.setMinHeight(24);

        addMaterial = new Button();
        addMaterial.setGraphic(new ImageView(Icons.ADD_24));

        final HBox titleContainer = new HBox(addMaterial, titleLabel);

        FXUtils.addClassTo(addMaterial, TOOLBAR_BUTTON);
        FXUtils.addClassTo(materialsView, TRANSPARENT_LIST_VIEW);
        FXUtils.addClassTo(titleLabel, MAIN_FONT_13);
        FXUtils.bindFixedHeight(materialsView, materialListContainer.heightProperty().subtract(addMaterial.heightProperty()).subtract(26));
        FXUtils.bindFixedHeight(titleLabel, addMaterial.heightProperty());

        FXUtils.addToPane(titleContainer, materialListContainer);
        FXUtils.addToPane(materialsView, materialListContainer);
        FXUtils.addToPane(materialListContainer, root);

        VBox.setMargin(addMaterial, new Insets(3, 0, 0, 5));
        VBox.setMargin(titleContainer, new Insets(0, 0, 0, 4));
        HBox.setMargin(titleLabel, new Insets(0, 0, 0, 3));
    }

    /**
     * Обработка принятия файла.
     */
    private void dragDropped(final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = (List<File>) dragboard.getContent(DataFormat.FILES);

        if (files == null || files.size() != 1) {
            return;
        }

        final Path file = files.get(0).toPath();
        final String extension = FileUtils.getExtension(file.getFileName().toString());

        if (!JME_MATERIAL.equals(extension)) {
            return;
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path relativize = currentAsset.relativize(file);

        dragEvent.consume();

        EXECUTOR_MANAGER.addEditorThreadTask(() -> addMaterial(relativize));
    }

    /**
     * Процесс добавления материала.
     */
    private void addMaterial(Path relativize) {

        final MaterialKey materialKey = new MaterialKey(relativize.toString());

        if (editorState.hasFilter(materialKey)) {
            return;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material material = assetManager.loadAsset(materialKey);

        editorState.addFilter(material);

        EXECUTOR_MANAGER.addFXTask(() -> {
            final ListView<Material> materialsView = getMaterialsView();
            final ObservableList<Material> items = materialsView.getItems();
            items.add(material);
        });
    }

    /**
     * Удаление указанного материала.
     */
    public void remove(final Material material) {

        final MaterialKey materialKey = (MaterialKey) material.getKey();

        if (!editorState.hasFilter(materialKey)) {
            return;
        }

        editorState.removeFilter(material);

        EXECUTOR_MANAGER.addFXTask(() -> {
            final ListView<Material> materialsView = getMaterialsView();
            final ObservableList<Material> items = materialsView.getItems();
            items.remove(material);
        });
    }

    /**
     * @return список используемых матералов.
     */
    private ListView<Material> getMaterialsView() {
        return materialsView;
    }

    /**
     * Обработка вхождения в зону.
     */
    private void dragOver(final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = (List<File>) dragboard.getContent(DataFormat.FILES);

        if (files == null || files.size() != 1) {
            return;
        }

        final Path file = files.get(0).toPath();
        final String extension = FileUtils.getExtension(file.getFileName().toString());

        if (!JME_MATERIAL.equals(extension)) {
            return;
        }

        dragEvent.acceptTransferModes(TransferMode.COPY);
        dragEvent.consume();
    }
}
