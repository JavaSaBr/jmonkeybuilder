package com.ss.editor.ui.component.editor.impl.post.filter;

import static com.ss.editor.FileExtensions.JME_MATERIAL;
import static com.ss.editor.FileExtensions.POST_FILTER_VIEW;
import static com.ss.editor.Messages.POST_FILTER_EDITOR_NAME;
import static rlib.util.ClassUtils.unsafeCast;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.Material;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.PostFilterViewFile;
import com.ss.editor.serializer.PostFilterViewSerializer;
import com.ss.editor.state.editor.impl.post.filter.PostFilterEditorState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import rlib.util.StringUtils;
import rlib.util.Util;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link FileEditor} for viewing post filters.
 *
 * @author JavaSaBr.
 */
public class PostFilterEditor extends AbstractFileEditor<StackPane> {

    public static final Insets ADD_MATERIAL_OFFSET = new Insets(3, 0, 0, 0);
    public static final Insets TITLE_CONTAINER_OFFSET = new Insets(0, 0, 0, 5);
    public static final Insets TITLE_LABEL_OFFSET = new Insets(0, 0, 0, 3);

    private static final Array<String> MATERIAL_EXTENSION = ArrayFactory.newArray(String.class);

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {

        DESCRIPTION.setEditorName(POST_FILTER_EDITOR_NAME);
        DESCRIPTION.setConstructor(PostFilterEditor::new);
        DESCRIPTION.setEditorId(PostFilterEditor.class.getName());
        DESCRIPTION.addExtension(POST_FILTER_VIEW);

        MATERIAL_EXTENSION.add(FileExtensions.JME_MATERIAL);
    }

    /**
     * The listener changed files events.
     */
    private final EventHandler<Event> fileChangedHandler;

    /**
     * The 3D part of this editor.
     */
    private final PostFilterEditorState editorState;

    /**
     * The list used materials.
     */
    private ListView<Material> materialsView;

    /**
     * The button for adding a new material.
     */
    private Button addMaterial;

    /**
     * The current opened file.
     */
    private PostFilterViewFile currentFile;

    /**
     * The original content of the opened fie.
     */
    private String originalContent;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListeners;

    public PostFilterEditor() {
        this.editorState = new PostFilterEditorState(this);
        this.fileChangedHandler = event -> processChangedFile((FileChangedEvent) event);
        addEditorState(editorState);
    }

    /**
     * @param ignoreListeners the flag for ignoring listeners.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return the flag for ignoring listeners.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param originalContent the original content of the opened fie.
     */
    private void setOriginalContent(final String originalContent) {
        this.originalContent = originalContent;
    }

    /**
     * @return the original content of the opened fie.
     */
    private String getOriginalContent() {
        return originalContent;
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {
        FXUtils.addToPane(createSaveAction(), container);
    }

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final PostFilterViewFile currentFile = PostFilterViewSerializer.deserialize(file);
        final byte[] content = Util.safeGet(file, Files::readAllBytes);

        setOriginalContent(new String(content));
        setCurrentFile(currentFile);
        setIgnoreListeners(true);
        try {
            final List<String> materials = currentFile.getMaterials();
            materials.forEach(assetName -> addRelativeMaterial(Paths.get(assetName)));
        } finally {
            setIgnoreListeners(false);
        }

        FX_EVENT_MANAGER.addEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());
    }

    @Override
    public void notifyClosed() {
        FX_EVENT_MANAGER.removeEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());
    }

    /**
     * @return the listener changed files events.
     */
    private EventHandler<Event> getFileChangedHandler() {
        return fileChangedHandler;
    }

    /**
     * Handle the event of changing a file.
     */
    private void processChangedFile(final FileChangedEvent event) {

        final Path file = event.getFile();
        final String extension = FileUtils.getExtension(file);
        if (!extension.endsWith(FileExtensions.JME_MATERIAL)) return;

        final Path assetFile = EditorUtil.getAssetFile(file);
        if (assetFile == null) return;

        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final PostFilterViewFile currentFile = getCurrentFile();
        final List<String> materials = currentFile.getMaterials();
        if (!materials.contains(assetPath)) return;

        final MaterialKey materialKey = new MaterialKey(assetPath);
        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Material material = assetManager.loadAsset(materialKey);

        editorState.removeFilter(material);
        editorState.addFilter(material);
    }

    /**
     * Handle a change.
     */
    private void handleChange() {

        final PostFilterViewFile currentFile = getCurrentFile();
        final String newContent = PostFilterViewSerializer.serializeToString(currentFile);
        final String originalContent = getOriginalContent();

        setDirty(!StringUtils.equals(originalContent, newContent));
    }

    @Override
    public void doSave() {

        final PostFilterViewFile currentFile = getCurrentFile();
        final String newContent = PostFilterViewSerializer.serializeToString(currentFile);

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(getEditFile()))) {
            out.print(newContent);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setOriginalContent(newContent);
        setDirty(false);
        notifyFileChanged();
    }

    /**
     * @param currentFile the current opened file.
     */
    private void setCurrentFile(final PostFilterViewFile currentFile) {
        this.currentFile = currentFile;
    }

    /**
     * @return the current opened file.
     */
    private PostFilterViewFile getCurrentFile() {
        return currentFile;
    }

    @NotNull
    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void createContent(@NotNull final StackPane root) {
        root.setAlignment(Pos.TOP_RIGHT);

        final VBox materialListContainer = new VBox();
        materialListContainer.setId(CSSIds.POST_FILTER_EDITOR_MATERIAL_FILTER_CONTAINER);

        final Label titleLabel = new Label();
        titleLabel.setText(Messages.POST_FILTER_EDITOR_MATERIAL_LABEL + ":");
        titleLabel.setAlignment(Pos.CENTER_LEFT);

        materialsView = new ListView<>();
        materialsView.setCellFactory(param -> new MaterialListCell(this));
        materialsView.setOnDragOver(this::dragOver);
        materialsView.setOnDragDropped(this::dragDropped);
        materialsView.setMinHeight(24);

        addMaterial = new Button();
        addMaterial.setId(CSSIds.POST_FILTER_EDITOR_ADD_MATERIAL_BUTTON);
        addMaterial.setGraphic(new ImageView(Icons.ADD_24));
        addMaterial.setOnAction(event -> processAdd());

        final HBox titleContainer = new HBox(addMaterial, titleLabel);

        FXUtils.addClassTo(addMaterial, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(materialsView, CSSClasses.TRANSPARENT_LIST_VIEW);
        FXUtils.addClassTo(titleLabel, CSSClasses.MAIN_FONT_13);

        FXUtils.bindFixedHeight(materialsView, materialListContainer.heightProperty().subtract(addMaterial.heightProperty()).subtract(26));
        FXUtils.bindFixedHeight(titleLabel, addMaterial.heightProperty());

        FXUtils.addToPane(titleContainer, materialListContainer);
        FXUtils.addToPane(materialsView, materialListContainer);
        FXUtils.addToPane(materialListContainer, root);

        VBox.setMargin(addMaterial, ADD_MATERIAL_OFFSET);
        VBox.setMargin(titleContainer, TITLE_CONTAINER_OFFSET);
        HBox.setMargin(titleLabel, TITLE_LABEL_OFFSET);
    }

    /**
     * Handle adding a new material.
     */
    private void processAdd() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final AssetEditorDialog dialog = new FileAssetEditorDialog(this::addMaterial);
        dialog.setExtensionFilter(MATERIAL_EXTENSION);
        dialog.show(scene.getWindow());
    }

    /**
     * Handle dropping a new file.
     */
    private void dragDropped(final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));
        if (files == null || files.size() != 1) return;

        final Path file = files.get(0).toPath();
        final String extension = FileUtils.getExtension(file);
        if (!JME_MATERIAL.equals(extension)) return;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path relativize = currentAsset.relativize(file);

        dragEvent.consume();

        addRelativeMaterial(relativize);
    }

    /**
     * The process of adding a new material.
     */
    private void addRelativeMaterial(final Path relativize) {

        final String assetPath = EditorUtil.toAssetPath(relativize);
        final MaterialKey materialKey = new MaterialKey(assetPath);
        if (editorState.hasFilter(materialKey)) return;

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Material material = assetManager.loadAsset(materialKey);

        editorState.addFilter(material);

        final ListView<Material> materialsView = getMaterialsView();
        final ObservableList<Material> items = materialsView.getItems();
        items.add(material);

        if (!isIgnoreListeners()) {

            final PostFilterViewFile currentFile = getCurrentFile();
            currentFile.addMaterial(materialKey.getName());

            handleChange();
        }
    }

    /**
     * The process of adding a new material.
     */
    private void addMaterial(final Path file) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        addRelativeMaterial(currentAsset.relativize(file));
    }

    /**
     * Remove the material.
     */
    public void remove(final Material material) {

        final MaterialKey materialKey = (MaterialKey) material.getKey();
        if (!editorState.hasFilter(materialKey)) return;

        editorState.removeFilter(material);

        final ListView<Material> materialsView = getMaterialsView();
        final ObservableList<Material> items = materialsView.getItems();
        items.remove(material);

        if (!isIgnoreListeners()) {

            final PostFilterViewFile currentFile = getCurrentFile();
            currentFile.removeMaterial(materialKey.getName());

            handleChange();
        }
    }

    /**
     * @return the list used materials.
     */
    private ListView<Material> getMaterialsView() {
        return materialsView;
    }

    /**
     * Handle the drag entering.
     */
    private void dragOver(final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));
        if (files == null || files.size() != 1) return;

        final Path file = files.get(0).toPath();
        final String extension = FileUtils.getExtension(file);
        if (!JME_MATERIAL.equals(extension)) return;

        dragEvent.acceptTransferModes(TransferMode.COPY);
        dragEvent.consume();
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
