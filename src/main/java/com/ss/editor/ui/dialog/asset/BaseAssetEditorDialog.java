package com.ss.editor.ui.dialog.asset;

import static com.ss.editor.Messages.ASSET_EDITOR_DIALOG_TITLE;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Editor;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.JMEFilePreviewManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.Utils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The base implementation of the {@link AbstractSimpleEditorDialog} to choose some asset resource.
 *
 * @author JavaSaBr
 */
public class BaseAssetEditorDialog<T, C> extends AbstractSimpleEditorDialog {

    /**
     * The dialog size.
     */
    @NotNull
    protected static final Point DIALOG_SIZE = new Point(-1, -1);

    /**
     * The image manager.
     */
    @NotNull
    protected static final JavaFXImageManager JAVA_FX_IMAGE_MANAGER = JavaFXImageManager.getInstance();

    /**
     * The editor.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The function to handle the choose.
     */
    @NotNull
    protected final Consumer<C> consumer;

    /**
     * The function to validate the choose.
     */
    @Nullable
    private final Function<@NotNull C, @Nullable String> validator;

    /**
     * The image preview.
     */
    @Nullable
    private ImageView imageView;

    /**
     * The preview of text files.
     */
    @Nullable
    private TextArea textView;

    /**
     * The label with any warning.
     */
    @Nullable
    private Label warningLabel;

    public BaseAssetEditorDialog(@NotNull final Consumer<C> consumer) {
        this(consumer, null);
    }

    public BaseAssetEditorDialog(@NotNull final Consumer<C> consumer,
                                 @Nullable final Function<@NotNull C, @Nullable String> validator) {
        this.consumer = consumer;
        this.validator = validator;
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final VBox root) {

        final HBox container = new HBox();

        final Region firstPart = buildFirstPart(container);
        firstPart.prefHeightProperty().bind(root.heightProperty());
        firstPart.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        final Region secondPart = buildSecondPart(container);
        secondPart.prefHeightProperty().bind(root.heightProperty());
        secondPart.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        FXUtils.addToPane(firstPart, container);
        FXUtils.addToPane(secondPart, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(root, CSSClasses.ASSET_EDITOR_DIALOG);
    }

    /**
     * Build the first part of this dialog.
     *
     * @param container the horizontal container.
     * @return the built component.
     */
    @FXThread
    protected @NotNull Region buildFirstPart(@NotNull final HBox container) {
        throw new RuntimeException("unsupported");
    }

    /**
     * Get the target object from the asset element.
     *
     * @param element the asset element.
     * @return the target object.
     */
    @FXThread
    protected @Nullable C getObject(@NotNull final T element) {
        throw new RuntimeException("unsupported");
    }

    /**
     * Gets consumer.
     *
     * @return the function for handling the choose.
     */
    @FromAnyThread
    protected @NotNull Consumer<C> getConsumer() {
        return consumer;
    }

    /**
     * Handle selected element in the tree.
     */
    @FXThread
    protected void processSelected(@Nullable final TreeItem<T> newValue) {

        final T element = newValue == null ? null : newValue.getValue();
        final String assetPath = element == null ? null : getAssetPath(element);
        final Path realFile = element == null ? null : getRealFile(element);

        validate(getWarningLabel(), element);
        try {

            if (assetPath != null) {
                updatePreview(assetPath);
            } else if (realFile != null) {
                updatePreview(realFile);
            } else {
                updatePreview((String) null);
            }

        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
        }
    }

    /**
     * Try to get an asset path from the element.
     *
     * @param element the element.
     * @return the asset path or null.
     */
    @FXThread
    protected @Nullable String getAssetPath(@NotNull final T element) {
        return null;
    }

    /**
     * Try to get a real file from the element.
     *
     * @param element the element.
     * @return the real file or null.
     */
    @FXThread
    protected @Nullable Path getRealFile(@NotNull final T element) {
        return null;
    }

    /**
     * Update the preview of the file.
     *
     * @param file the file.
     */
    @FXThread
    private void updatePreview(@NotNull final Path file) {

        final ImageView imageView = getImageView();
        imageView.setVisible(false);

        final TextArea textView = getTextView();
        textView.setVisible(false);

        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        if (JMEFilePreviewManager.isJmeFile(file)) {

            final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
            previewManager.show(file, width, height);

            final ImageView sourceView = previewManager.getImageView();
            final ObjectProperty<Image> imageProperty = imageView.imageProperty();
            imageProperty.bind(sourceView.imageProperty());

            imageView.setVisible(true);

        } else if (JavaFXImageManager.isImage(file)) {

            final Image preview = JAVA_FX_IMAGE_MANAGER.getImagePreview(file, width, height);
            imageView.setImage(preview);
            imageView.setVisible(true);

        } else if (!StringUtils.isEmpty(FileUtils.getExtension(file))) {

            imageView.imageProperty().unbind();
            imageView.setImage(null);

            textView.setText(FileUtils.read(file));
            textView.setVisible(true);

        } else {
            imageView.imageProperty().unbind();
            imageView.setImage(null);
        }
    }

    /**
     * Update the preview of the object by the asset path.
     *
     * @param assetPath the asset path of the object.
     */
    @FXThread
    private void updatePreview(@Nullable final String assetPath) {

        final ImageView imageView = getImageView();
        imageView.setVisible(false);

        final TextArea textView = getTextView();
        textView.setVisible(false);

        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        if (JMEFilePreviewManager.isJmeFile(assetPath)) {

            final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
            previewManager.show(assetPath, width, height);

            final ImageView sourceView = previewManager.getImageView();
            final ObjectProperty<Image> imageProperty = imageView.imageProperty();
            imageProperty.bind(sourceView.imageProperty());

            imageView.setVisible(true);

        } else if (JavaFXImageManager.isImage(assetPath)) {

            final Image preview = JAVA_FX_IMAGE_MANAGER.getImagePreview(assetPath, width, height);
            imageView.setImage(preview);
            imageView.setVisible(true);

        } else if (assetPath != null && !StringUtils.isEmpty(FileUtils.getExtension(assetPath))) {

            final ResourceManager resourceManager = ResourceManager.getInstance();
            final URL url = resourceManager.tryToFindResource(assetPath);

            String content;

            if (url != null) {
                content = Utils.get(url, first -> FileUtils.read(first.openStream()));
            } else {
                final Path realFile = EditorUtil.getRealFile(assetPath);
                content = realFile == null ? "" : FileUtils.read(realFile);
            }

            imageView.imageProperty().unbind();
            imageView.setImage(null);

            textView.setText(content);
            textView.setVisible(true);

        } else {
            imageView.imageProperty().unbind();
            imageView.setImage(null);
        }
    }

    /**
     * Validate the resource element.
     *
     * @param warningLabel the warning label
     * @param element      the element.
     */
    @FXThread
    protected void validate(@NotNull final Label warningLabel, @Nullable final T element) {

        final Function<@NotNull C, @Nullable String> validator = getValidator();
        if (validator == null) return;

        final C object = element == null ? null : getObject(element);
        final String message = object == null ? null : validator.apply(object);

        if (message == null) {
            warningLabel.setText(StringUtils.EMPTY);
            warningLabel.setVisible(false);
        } else {
            warningLabel.setText(message);
            warningLabel.setVisible(true);
        }
    }

    /**
     * Build second part parent.
     *
     * @param container the container
     * @return the parent
     */
    @FXThread
    protected @NotNull Region buildSecondPart(@NotNull final HBox container) {

        final StackPane previewContainer = new StackPane();

        imageView = new ImageView();
        imageView.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        imageView.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));

        textView = new TextArea();
        textView.setEditable(false);
        textView.prefWidthProperty().bind(previewContainer.widthProperty().subtract(2));
        textView.prefHeightProperty().bind(previewContainer.heightProperty().subtract(2));

        FXUtils.addToPane(imageView, previewContainer);
        FXUtils.addToPane(textView, previewContainer);

        FXUtils.addClassTo(previewContainer, CSSClasses.ASSET_EDITOR_DIALOG_PREVIEW_CONTAINER);
        FXUtils.addClassTo(textView, CSSClasses.TRANSPARENT_TEXT_AREA);

        return previewContainer;
    }

    /**
     * @return the image preview.
     */
    @FXThread
    protected @NotNull ImageView getImageView() {
        return notNull(imageView);
    }

    /**
     * @return the text preview.
     */
    @FXThread
    protected @NotNull TextArea getTextView() {
        return notNull(textView);
    }

    /**
     * Gets validator.
     *
     * @return the function for validating the choose.
     */
    @FXThread
    protected @Nullable Function<@NotNull C, @Nullable String> getValidator() {
        return validator;
    }

    /**
     * @return the label with any warning.
     */
    @FXThread
    protected @NotNull Label getWarningLabel() {
        return notNull(warningLabel);
    }

    @Override
    @FXThread
    public void hide() {

        final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
        previewManager.clear();

        super.hide();
    }

    @Override
    @FXThread
    protected void createBeforeActions(@NotNull final HBox container) {
        super.createBeforeActions(container);

        warningLabel = new Label();
        warningLabel.setGraphic(new ImageView(Icons.WARNING_24));
        warningLabel.setVisible(false);

        FXUtils.addClassTo(warningLabel, CSSClasses.DIALOG_LABEL_WARNING);
        FXUtils.addToPane(warningLabel, container);
    }

    @Override
    @FXThread
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final Button okButton = notNull(getOkButton());
        okButton.disableProperty().bind(buildDisableCondition());
    }

    /**
     * Build disable condition.
     *
     * @return the disable condition.
     */
    @FXThread
    protected @NotNull BooleanBinding buildDisableCondition() {
        final Label warningLabel = getWarningLabel();
        return warningLabel.visibleProperty().or(buildAdditionalDisableCondition());
    }

    /**
     * Build additional disable condition.
     *
     * @return the additional condition.
     */
    @FXThread
    protected @NotNull ObservableBooleanValue buildAdditionalDisableCondition() {
        throw new RuntimeException("unsupported");
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return ASSET_EDITOR_DIALOG_TITLE;
    }
}
