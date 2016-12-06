package com.ss.editor.ui.control.model.tree.dialog.sky;

import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The control for choosing textures.
 *
 * @author JavaSaBr.
 */
public class ChooseTextureControl extends HBox {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    private static final Array<String> TEXTURE_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_PNG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_JPG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_JPEG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_TGA);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_DDS);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_HDR);
    }

    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The image channels preview.
     */
    private ImageChannelPreview textureTooltip;

    /**
     * The image preview.
     */
    private ImageView texturePreview;

    /**
     * The label for the path of texture.
     */
    private Label textureLabel;

    /**
     * The selected file.
     */
    private Path textureFile;

    /**
     * The handler.
     */
    private Runnable changeHandler;

    public ChooseTextureControl() {
        setAlignment(Pos.CENTER_LEFT);
        createComponents();
    }

    /**
     * @param changeHandler the handler.
     */
    public void setChangeHandler(final Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    /**
     * tThe handler.
     */
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    protected void createComponents() {

        textureLabel = new Label();
        textureLabel.setId(CSSIds.CHOOSE_TEXTURE_CONTROL_TEXTURE_LABEL);

        textureTooltip = new ImageChannelPreview();

        final VBox previewContainer = new VBox();
        previewContainer.setId(CSSIds.CHOOSE_TEXTURE_CONTROL_PREVIEW);

        texturePreview = new ImageView();
        texturePreview.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        texturePreview.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));

        Tooltip.install(texturePreview, textureTooltip);

        final Button addButton = new Button();
        addButton.setId(CSSIds.CREATE_SKY_DIALOG_BUTTON);
        addButton.setGraphic(new ImageView(Icons.ADD_18));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.CREATE_SKY_DIALOG_BUTTON);
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addToPane(textureLabel, this);
        FXUtils.addToPane(texturePreview, previewContainer);
        FXUtils.addToPane(previewContainer, this);
        FXUtils.addToPane(addButton, this);
        FXUtils.addToPane(removeButton, this);

        FXUtils.addClassTo(textureLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(addButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);

        HBox.setMargin(previewContainer, ELEMENT_OFFSET);
        HBox.setMargin(addButton, ELEMENT_OFFSET);
        HBox.setMargin(removeButton, ELEMENT_OFFSET);

        removeButton.disableProperty().bind(texturePreview.imageProperty().isNull());
    }

    /**
     * @return the label for the path of texture.
     */
    private Label getTextureLabel() {
        return textureLabel;
    }

    /**
     * @return The image channels preview.
     */
    public ImageChannelPreview getTextureTooltip() {
        return textureTooltip;
    }

    /**
     * Add new texture.
     */
    private void processAdd() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final AssetEditorDialog dialog = new FileAssetEditorDialog(this::setTextureFile);
        dialog.setExtensionFilter(TEXTURE_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    /**
     * @return the selected file.
     */
    public Path getTextureFile() {
        return textureFile;
    }

    /**
     * @param textureFile the selected file.
     */
    private void setTextureFile(final Path textureFile) {
        this.textureFile = textureFile;

        reload();

        final Runnable changeHandler = getChangeHandler();
        if (changeHandler != null) changeHandler.run();
    }

    /**
     * Remove the texture.
     */
    private void processRemove() {
        setTextureFile(null);
    }

    /**
     * @return the image preview.
     */
    public ImageView getTexturePreview() {
        return texturePreview;
    }

    protected void reload() {

        final ImageChannelPreview textureTooltip = getTextureTooltip();
        final Label textureLabel = getTextureLabel();
        final ImageView preview = getTexturePreview();

        final Path textureFile = getTextureFile();

        if (textureFile == null) {
            //FIXME добавить локализацию
            textureLabel.setText("No texture");
            preview.setImage(null);
            textureTooltip.showImage(null);
            return;
        }

        final Path assetFile = EditorUtil.getAssetFile(textureFile);

        Objects.requireNonNull(assetFile, "can't find the asset file for the " + textureFile);

        textureLabel.setText(assetFile.toString());
        preview.setImage(IMAGE_MANAGER.getTexturePreview(textureFile, 28, 28));
        textureTooltip.showImage(textureFile);
    }
}
