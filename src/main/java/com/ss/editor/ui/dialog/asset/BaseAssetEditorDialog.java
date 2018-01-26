package com.ss.editor.ui.dialog.asset;

import static com.ss.editor.Messages.ASSET_EDITOR_DIALOG_TITLE;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.preview.FilePreview;
import com.ss.editor.ui.preview.FilePreviewFactoryRegistry;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
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
     * The registry of available file previews.
     */
    @NotNull
    protected static final FilePreviewFactoryRegistry FILE_PREVIEW_FACTORY_REGISTRY = FilePreviewFactoryRegistry.getInstance();

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
     * The list of available previews.
     */
    @Nullable
    protected Array<FilePreview> previews;

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
    @FxThread
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

        FXUtils.addClassTo(container, CssClasses.DEF_HBOX);
        FXUtils.addClassTo(root, CssClasses.ASSET_EDITOR_DIALOG);
    }

    /**
     * Build the first part of this dialog.
     *
     * @param container the horizontal container.
     * @return the built component.
     */
    @FxThread
    protected @NotNull Region buildFirstPart(@NotNull final HBox container) {
        throw new RuntimeException("unsupported");
    }

    /**
     * Get the target object from the asset element.
     *
     * @param element the asset element.
     * @return the target object.
     */
    @FxThread
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
     *
     * @param newValue the new selected item.
     */
    @FxThread
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
    @FxThread
    protected @Nullable String getAssetPath(@NotNull final T element) {
        return null;
    }

    /**
     * Try to get a real file from the element.
     *
     * @param element the element.
     * @return the real file or null.
     */
    @FxThread
    protected @Nullable Path getRealFile(@NotNull final T element) {
        return null;
    }

    /**
     * Update the preview of the file.
     *
     * @param file the file.
     */
    @FxThread
    private void updatePreview(@NotNull final Path file) {

        final Array<FilePreview> previews = getPreviews();
        final FilePreview preview = previews.search(file, FilePreview::isSupport);
        previews.forEach(preview, (filePreview, toCheck) -> filePreview != toCheck,
                (filePreview, tooCheck) -> filePreview.hide());

        if (preview == null) {
            return;
        }

        preview.show(file);
    }

    /**
     * Update the preview of the object by the asset path.
     *
     * @param assetPath the asset path of the object.
     */
    @FxThread
    private void updatePreview(@Nullable final String assetPath) {

        final Array<FilePreview> previews = getPreviews();

        if (assetPath == null) {
            previews.forEach(FilePreview::hide);
            return;
        }

        final FilePreview preview = previews.search(assetPath, FilePreview::isSupport);
        previews.forEach(preview, (filePreview, toCheck) -> filePreview != toCheck,
                (filePreview, tooCheck) -> filePreview.hide());

        if (preview == null) {
            return;
        }

        preview.show(assetPath);
    }

    /**
     * Validate the resource element.
     *
     * @param warningLabel the warning label
     * @param element      the element.
     */
    @FxThread
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
    @FxThread
    protected @NotNull Region buildSecondPart(@NotNull final HBox container) {

        final StackPane previewContainer = new StackPane();

        final Array<FilePreview> availablePreviews = FILE_PREVIEW_FACTORY_REGISTRY.createAvailablePreviews();
        availablePreviews.forEach(previewContainer, FilePreview::initialize);

        FXUtils.addClassTo(previewContainer, CssClasses.ASSET_EDITOR_DIALOG_PREVIEW_CONTAINER);

        this.previews = availablePreviews;

        return previewContainer;
    }

    /**
     * Gets validator.
     *
     * @return the function for validating the choose.
     */
    @FxThread
    protected @Nullable Function<@NotNull C, @Nullable String> getValidator() {
        return validator;
    }

    /**
     * @return the label with any warning.
     */
    @FxThread
    protected @NotNull Label getWarningLabel() {
        return notNull(warningLabel);
    }

    /**
     * Get the list of available file previews.
     *
     * @return the list of available file previews.
     */
    @FxThread
    protected @NotNull Array<FilePreview> getPreviews() {
        return notNull(previews);
    }

    @Override
    @FxThread
    public void hide() {
        getPreviews().forEach(FilePreview::release);
        super.hide();
    }

    @Override
    @FxThread
    protected void createBeforeActions(@NotNull final HBox container) {
        super.createBeforeActions(container);

        warningLabel = new Label();
        warningLabel.setGraphic(new ImageView(Icons.WARNING_24));
        warningLabel.setVisible(false);

        FXUtils.addClassTo(warningLabel, CssClasses.DIALOG_LABEL_WARNING);
        FXUtils.addToPane(warningLabel, container);
    }

    @Override
    @FxThread
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
    @FxThread
    protected @NotNull BooleanBinding buildDisableCondition() {
        final Label warningLabel = getWarningLabel();
        return warningLabel.visibleProperty().or(buildAdditionalDisableCondition());
    }

    /**
     * Build additional disable condition.
     *
     * @return the additional condition.
     */
    @FxThread
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
