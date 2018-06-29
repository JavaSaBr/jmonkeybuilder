package com.ss.editor.ui.dialog.asset;

import static com.ss.editor.Messages.ASSET_EDITOR_DIALOG_TITLE;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.preview.FilePreview;
import com.ss.editor.ui.preview.FilePreviewFactoryRegistry;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
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

/**
 * The base implementation of the {@link AbstractSimpleEditorDialog} to choose some asset resource.
 *
 * @author JavaSaBr
 */
public class BaseAssetEditorDialog<T, C> extends AbstractSimpleEditorDialog {

    @FunctionalInterface
    public interface Validator<C> {

        /**
         * Return a string with error message if the object isn't valid or null if the object is ok.
         *
         * @param object the object to validate.
         * @return the message or null.
         */
        @FxThread
        @Nullable String validate(@NotNull C object);
    }

    /**
     * The dialog.
     */
    protected static final Point DIALOG_SIZE = new Point(-1, -1);

    /**
     * The list of available previews.
     */
    @NotNull
    protected final Array<FilePreview> previews;

    /**
     * The function to handle the choose.
     */
    @NotNull
    protected final Consumer<C> consumer;

    /**
     * The function to validate the choose.
     */
    @Nullable
    private final Validator<C> validator;

    /**
     * The label with any warning.
     */
    @NotNull
    private final Label warningLabel;

    public BaseAssetEditorDialog(@NotNull Consumer<C> consumer) {
        this(consumer, null);
    }

    public BaseAssetEditorDialog(@NotNull Consumer<C> consumer, @Nullable Validator<C> validator) {
        this.consumer = consumer;
        this.validator = validator;
        this.warningLabel = new Label();
        this.previews = FilePreviewFactoryRegistry.getInstance()
                .createAvailablePreviews();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {

        var container = new HBox();

        var firstPart = buildFirstPart(container);
        firstPart.prefHeightProperty()
                .bind(root.heightProperty());
        firstPart.prefWidthProperty()
                .bind(root.widthProperty().multiply(0.5));

        var secondPart = buildSecondPart(container);
        secondPart.prefHeightProperty()
                .bind(root.heightProperty());
        secondPart.prefWidthProperty()
                .bind(root.widthProperty().multiply(0.5));

        FxUtils.addClass(container, CssClasses.DEF_HBOX)
                .addClass(root, CssClasses.ASSET_EDITOR_DIALOG);

        FxUtils.addChild(container, firstPart, secondPart)
                .addChild(root, container);
    }

    /**
     * Build the first part of this dialog.
     *
     * @param container the horizontal container.
     * @return the built component.
     */
    @FxThread
    protected @NotNull Region buildFirstPart(@NotNull HBox container) {
        throw new RuntimeException("unsupported");
    }

    /**
     * Get the target object from the asset element.
     *
     * @param element the asset element.
     * @return the target object.
     */
    @FxThread
    protected @Nullable C getObject(@NotNull T element) {
        throw new RuntimeException("unsupported");
    }

    /**
     * Get the function to handle a choose.
     *
     * @return the function to handle a choose.
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
    protected void processSelected(@Nullable TreeItem<T> newValue) {

        var element = newValue == null ? null : newValue.getValue();
        var assetPath = element == null ? null : getAssetPath(element);
        var realFile = element == null ? null : getRealFile(element);

        validate(getWarningLabel(), element);
        try {

            if (assetPath != null) {
                updatePreview(assetPath);
            } else if (realFile != null) {
                updatePreview(realFile);
            } else {
                updatePreview((String) null);
            }

        } catch (Exception e) {
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
    protected @Nullable String getAssetPath(@NotNull T element) {
        return null;
    }

    /**
     * Try to get a real file from the element.
     *
     * @param element the element.
     * @return the real file or null.
     */
    @FxThread
    protected @Nullable Path getRealFile(@NotNull T element) {
        return null;
    }

    /**
     * Update the preview of the file.
     *
     * @param file the file.
     */
    @FxThread
    private void updatePreview(@NotNull Path file) {

        var previews = getPreviews();

        var preview = previews.findAny(file, FilePreview::isSupport);
        previews.forEach(preview,
                (filePreview, toCheck) -> filePreview != toCheck,
                (filePreview, toCheck) -> filePreview.hide());

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
    private void updatePreview(@Nullable String assetPath) {

        var previews = getPreviews();

        if (assetPath == null) {
            previews.forEach(FilePreview::hide);
            return;
        }

        var preview = previews.findAny(assetPath, FilePreview::isSupport);
        previews.forEach(preview,
                (filePreview, toCheck) -> filePreview != toCheck,
                (filePreview, toCheck) -> filePreview.hide());

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
    protected void validate(@NotNull Label warningLabel, @Nullable T element) {

        var validator = getValidator();

        if (validator == null) {
            return;
        }

        var object = element == null ? null : getObject(element);
        var message = object == null ? null : validator.validate(object);

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
    protected @NotNull Region buildSecondPart(@NotNull HBox container) {

        var previewContainer = new StackPane();

        previews.forEach(previewContainer, FilePreview::initialize);

        FxUtils.addClass(previewContainer,
                CssClasses.ASSET_EDITOR_DIALOG_PREVIEW_CONTAINER);

        return previewContainer;
    }

    /**
     * Get the function to validate a choose.
     *
     * @return the function to validate a choose.
     */
    @FxThread
    protected @Nullable Validator<C> getValidator() {
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
    protected void createBeforeActions(@NotNull HBox container) {
        super.createBeforeActions(container);

        warningLabel.setGraphic(new ImageView(Icons.WARNING_24));
        warningLabel.setVisible(false);

        FxUtils.addClass(warningLabel, CssClasses.DIALOG_LABEL_WARNING);
        FxUtils.addChild(container, warningLabel);
    }

    @Override
    @FxThread
    protected void createActions(@NotNull VBox root) {
        super.createActions(root);

        var okButton = notNull(getOkButton());
        okButton.disableProperty().bind(buildDisableCondition());
    }

    /**
     * Build disable condition.
     *
     * @return the disable condition.
     */
    @FxThread
    protected @NotNull BooleanBinding buildDisableCondition() {
        return getWarningLabel().visibleProperty()
                .or(buildAdditionalDisableCondition());
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
