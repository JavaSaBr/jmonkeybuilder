package com.ss.editor.ui.control.material;

import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;
import static rlib.util.ClassUtils.unsafeCast;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.material.operation.TextureMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link MaterialParamControl} for editing textures.
 *
 * @author JavaSaBr
 */
public class Texture2DMaterialParamControl extends MaterialParamControl {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    private static final Array<String> TEXTURE_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_PNG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_JPG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_JPEG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_TGA);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_DDS);
    }

    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * The channels preview of the texture.
     */
    private ImageChannelPreview textureTooltip;

    /**
     * The preview of the texture.
     */
    private ImageView texturePreview;

    /**
     * The checkbox for editing repeat property.
     */
    private CheckBox repeatButton;

    /**
     * The checkbox for editing flip property.
     */
    private CheckBox flipButton;

    public Texture2DMaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler,
                                         @NotNull final Material material, @NotNull final String parameterName) {
        super(changeHandler, material, parameterName);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        setOnDragExited(this::dragExited);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        textureTooltip = new ImageChannelPreview();

        final VBox previewContainer = new VBox();
        previewContainer.setId(CSSIds.TEXTURE_2D_MATERIAL_PARAM_CONTROL_PREVIEW);

        texturePreview = new ImageView();
        texturePreview.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        texturePreview.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));
        texturePreview.setCache(true);
        texturePreview.setCacheHint(CacheHint.SPEED);

        Tooltip.install(texturePreview, textureTooltip);

        final Button addButton = new Button();
        addButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_BUTTON);
        addButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD));
        addButton.setGraphic(new ImageView(Icons.ADD_18));
        addButton.setOnAction(event -> processAdd());

        repeatButton = new CheckBox();
        repeatButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        repeatButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT));
        repeatButton.selectedProperty().addListener((observ, old, newValue) -> processChangeRepeat(newValue));

        flipButton = new CheckBox();
        flipButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        flipButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP));
        flipButton.selectedProperty().addListener((observ, old, newValue) -> processChangeFlip(newValue));

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_BUTTON);
        removeButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE));
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addToPane(texturePreview, previewContainer);
        FXUtils.addToPane(previewContainer, this);
        FXUtils.addToPane(addButton, this);
        FXUtils.addToPane(repeatButton, this);
        FXUtils.addToPane(flipButton, this);
        FXUtils.addToPane(removeButton, this);

        FXUtils.addClassTo(addButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);

        removeButton.disableProperty().bind(texturePreview.imageProperty().isNull());
        repeatButton.disableProperty().bind(texturePreview.imageProperty().isNull());
        flipButton.disableProperty().bind(texturePreview.imageProperty().isNull());
    }

    /**
     * Handle grad exiting.
     */
    private void dragExited(@NotNull final DragEvent dragEvent) {

    }

    /**
     * Handle dropped files to editor.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName());

        if (!TEXTURE_EXTENSIONS.contains(extension)) {
            return;
        }

        addTexture(file.toPath());
    }

    /**
     * Handle drag over.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName());

        if (!TEXTURE_EXTENSIONS.contains(extension)) {
            return;
        }

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    @Override
    protected double getLabelPercentWidth() {
        return 0.9;
    }

    /**
     * @return the checkbox for editing repeat property.
     */
    @NotNull
    public ImageChannelPreview getTextureTooltip() {
        return textureTooltip;
    }

    /**
     * Handle changing the repeat property.
     */
    private void processChangeRepeat(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D texture2D = (Texture2D) textureParam.getValue();
        final TextureKey key = (TextureKey) texture2D.getKey();

        WrapMode oldMode;
        WrapMode newMode;

        if (newValue) {
            oldMode = WrapMode.EdgeClamp;
            newMode = WrapMode.Repeat;
            texture2D.setWrap(WrapMode.Repeat);
        } else {
            oldMode = WrapMode.Repeat;
            newMode = WrapMode.EdgeClamp;
            texture2D.setWrap(WrapMode.EdgeClamp);
        }

        execute(new TextureMaterialParamOperation(parameterName, key, newMode, key, oldMode));
    }

    /**
     * Handle changing the flip property.
     */
    private void processChangeFlip(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final String parameterName = getParameterName();

        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D texture2D = (Texture2D) textureParam.getValue();
        final TextureKey oldKey = (TextureKey) texture2D.getKey();
        final TextureKey newKey = (TextureKey) oldKey.clone();
        newKey.setFlipY(newValue);

        WrapMode mode = WrapMode.EdgeClamp;

        if (texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat) {
            mode = WrapMode.Repeat;
        }

        execute(new TextureMaterialParamOperation(parameterName, newKey, mode, oldKey, mode));
    }

    /**
     * The process of adding a new texture.
     */
    private void processAdd() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final AssetEditorDialog dialog = new FileAssetEditorDialog(this::addTexture);
        dialog.setExtensionFilter(TEXTURE_EXTENSIONS);
        dialog.setActionTester(ACTION_TESTER);
        dialog.show(scene.getWindow());
    }

    /**
     * Add texture by the path.
     *
     * @param path the path to texture.
     */
    private void addTexture(@NotNull final Path path) {

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D oldTexture = textureParam == null ? null : (Texture2D) textureParam.getValue();
        final TextureKey oldKey = oldTexture == null ? null : (TextureKey) oldTexture.getKey();

        WrapMode oldMode = WrapMode.EdgeClamp;

        if (oldTexture != null && oldTexture.getWrap(WrapAxis.S) == WrapMode.Repeat) {
            oldMode = WrapMode.Repeat;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Path assetFile = requireNonNull(getAssetFile(path), "Can't get an asset file for " + path);
        final String assetPath = toAssetPath(assetFile);

        final CheckBox flipButton = getFlipButton();
        final CheckBox repeatButton = getRepeatButton();

        final TextureKey newKey = new TextureKey(assetPath);
        newKey.setFlipY(oldKey == null ? EDITOR_CONFIG.isDefaultUseFlippedTexture() : flipButton.isSelected());

        try {
            assetManager.loadTexture(newKey);
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        final WrapMode newMode = repeatButton.isSelected() ? WrapMode.Repeat : WrapMode.EdgeClamp;
        execute(new TextureMaterialParamOperation(parameterName, newKey, newMode, oldKey, oldMode));
    }

    /**
     * Remove the current texture.
     */
    private void processRemove() {

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D texture2D = (Texture2D) textureParam.getValue();
        final TextureKey oldKey = (TextureKey) texture2D.getKey();

        WrapMode mode = WrapMode.EdgeClamp;

        if (texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat) {
            mode = WrapMode.Repeat;
        }

        execute(new TextureMaterialParamOperation(parameterName, null, null, oldKey, mode));
    }

    /**
     * @return the checkbox for editing repeat property.
     */
    private CheckBox getRepeatButton() {
        return repeatButton;
    }

    /**
     * @return the checkbox for editing flip property.
     */
    private CheckBox getFlipButton() {
        return flipButton;
    }

    /**
     * @return the preview of the texture.
     */
    public ImageView getTexturePreview() {
        return texturePreview;
    }

    @Override
    public void reload() {
        super.reload();

        final ImageChannelPreview textureTooltip = getTextureTooltip();
        final Material material = getMaterial();
        final MatParamTexture param = (MatParamTexture) material.getParam(getParameterName());

        if (param == null) {

            final ImageView preview = getTexturePreview();
            preview.setImage(null);

            final CheckBox flipButton = getFlipButton();
            flipButton.setSelected(false);

            final CheckBox repeatButton = getRepeatButton();
            repeatButton.setSelected(false);

            textureTooltip.showImage(null);
            return;
        }

        final Texture2D texture2D = (Texture2D) param.getValue();
        final TextureKey textureKey = (TextureKey) texture2D.getKey();

        final CheckBox flipButton = getFlipButton();
        flipButton.setSelected(textureKey.isFlipY());

        final CheckBox repeatButton = getRepeatButton();
        repeatButton.setSelected(texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat);

        final Path realFile = EditorUtil.getRealFile(textureKey.getName());

        final ImageView preview = getTexturePreview();
        preview.setImage(IMAGE_MANAGER.getTexturePreview(realFile, 28, 28));

        textureTooltip.showImage(realFile);
    }
}
