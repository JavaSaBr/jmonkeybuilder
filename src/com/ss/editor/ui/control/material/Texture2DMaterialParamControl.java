package com.ss.editor.ui.control.material;

import static com.ss.editor.FileExtensions.TEXTURE_EXTENSIONS;
import static com.ss.editor.Messages.*;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.ss.editor.Editor;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.control.material.operation.TextureMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The implementation of the {@link MaterialParamControl} for editing textures.
 *
 * @author JavaSaBr
 */
public class Texture2DMaterialParamControl extends MaterialParamControl {

    @NotNull
    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    @NotNull
    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();

    @NotNull
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * The channels preview of the texture.
     */
    @Nullable
    private ImageChannelPreview textureTooltip;

    /**
     * The preview of the texture.
     */
    @Nullable
    private ImageView texturePreview;

    /**
     * The checkbox for editing repeat property.
     */
    @Nullable
    private CheckBox repeatButton;

    /**
     * The checkbox for editing flip property.
     */
    @Nullable
    private CheckBox flipButton;

    /**
     * Instantiates a new Texture 2 d material param control.
     *
     * @param changeHandler the change handler
     * @param material      the material
     * @param parameterName the parameter name
     */
    public Texture2DMaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler,
                                         @NotNull final Material material, @NotNull final String parameterName) {
        super(changeHandler, material, parameterName);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        textureTooltip = new ImageChannelPreview();

        final VBox previewContainer = new VBox();

        texturePreview = new ImageView();
        texturePreview.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        texturePreview.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));
        texturePreview.setCache(true);
        texturePreview.setCacheHint(CacheHint.SPEED);

        Tooltip.install(texturePreview, textureTooltip);

        final Button addButton = new Button();
        addButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD));
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAdd());

        repeatButton = new CheckBox();
        repeatButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT));
        repeatButton.selectedProperty().addListener((observ, old, newValue) -> processChangeRepeat(newValue));

        flipButton = new CheckBox();
        flipButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP));
        flipButton.selectedProperty().addListener((observ, old, newValue) -> processChangeFlip(newValue));

        final Button removeButton = new Button();
        removeButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE));
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addToPane(texturePreview, previewContainer);
        FXUtils.addToPane(previewContainer, this);
        FXUtils.addToPane(addButton, this);
        FXUtils.addToPane(repeatButton, this);
        FXUtils.addToPane(flipButton, this);
        FXUtils.addToPane(removeButton, this);

        FXUtils.addClassesTo(addButton, removeButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_BUTTON);
        FXUtils.addClassTo(previewContainer, CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_TEXTURE_PREVIEW);

        DynamicIconSupport.addSupport(addButton, removeButton);

        removeButton.disableProperty().bind(texturePreview.imageProperty().isNull());
        repeatButton.disableProperty().bind(texturePreview.imageProperty().isNull());
        flipButton.disableProperty().bind(texturePreview.imageProperty().isNull());
    }

    /**
     * Handle dropped files to editor.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {
        UIUtils.handleDroppedFile(dragEvent, TEXTURE_EXTENSIONS, this, Texture2DMaterialParamControl::addTexture);
    }

    /**
     * Handle drag over.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {
        UIUtils.acceptIfHasFile(dragEvent, TEXTURE_EXTENSIONS);
    }

    @Override
    protected double getLabelPercentWidth() {
        return 0.9;
    }

    /**
     * @return the checkbox for editing repeat property.
     */
    @NotNull
    private ImageChannelPreview getTextureTooltip() {
        return notNull(textureTooltip);
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
        UIUtils.openAssetDialog(this::addTexture, TEXTURE_EXTENSIONS, ACTION_TESTER);
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
        final Path assetFile = notNull(getAssetFile(path), "Can't get an asset file for " + path);
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
    @NotNull
    private CheckBox getRepeatButton() {
        return notNull(repeatButton);
    }

    /**
     * @return the checkbox for editing flip property.
     */
    @NotNull
    private CheckBox getFlipButton() {
        return notNull(flipButton);
    }

    /**
     * @return the preview of the texture.
     */
    @NotNull
    private ImageView getTexturePreview() {
        return notNull(texturePreview);
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

        final CheckBox flipButton = getFlipButton();
        final CheckBox repeatButton = getRepeatButton();
        final Texture value = (Texture) param.getValue();
        final TextureKey textureKey = (TextureKey) value.getKey();

        if (value instanceof Texture2D) {
            final Texture2D texture2D = (Texture2D) value;
            flipButton.setSelected(textureKey.isFlipY());
            repeatButton.setSelected(texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat);
        } else if (value instanceof Texture3D) {
            final Texture3D texture2D = (Texture3D) value;
            flipButton.setSelected(textureKey.isFlipY());
            repeatButton.setSelected(texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat);
        }

        final Path realFile = EditorUtil.getRealFile(textureKey.getName());

        final ImageView preview = getTexturePreview();
        preview.setImage(IMAGE_MANAGER.getTexturePreview(realFile, 28, 28));

        textureTooltip.showImage(realFile);
    }
}
