package com.ss.editor.ui.dialog.imports.model;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.manager.JMEFilePreviewManager;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of a dialog to import external models.
 *
 * @author JavaSaBr
 */
public class ModelImportDialog extends GenericFileCreator {

    private static final String PROP_FILE = "file";

    /**
     * The image view to show preview of model.
     */
    @Nullable
    private ImageView imageView;

    @Override
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {

        final Array<PropertyDefinition> result = ArrayFactory.newArray(PropertyDefinition.class);
        result.add(new PropertyDefinition(EditablePropertyType.EXTERNAL_FILE, "External file", PROP_FILE, null));

        return result;
    }

    @Override
    @FXThread
    protected void createPreview(@NotNull final BorderPane container) {
        super.createPreview(container);
        imageView = new ImageView();
        imageView.fitHeightProperty().bind(container.heightProperty());
        imageView.fitWidthProperty().bind(container.widthProperty());
        container.setCenter(imageView);
    }

    @Override
    @FromAnyThread
    protected boolean needPreview() {
        return true;
    }

    /**
     * Get the image view.
     *
     * @return the image view.
     */
    @FXThread
    private @NotNull ImageView getImageView() {
        return notNull(imageView);
    }

    @Override
    @FXThread
    protected boolean validate(@NotNull final VarTable vars) {

        final ImageView imageView = getImageView();

        if (!vars.has(PROP_FILE)) {
            imageView.setImage(null);
            return false;
        }

        final Path file = vars.get(PROP_FILE);

        if (!JMEFilePreviewManager.isModelFile(file)) {
            imageView.setImage(null);
            return false;
        }

        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
        previewManager.showExternal(file, width, height);

        final ImageView sourceView = previewManager.getImageView();
        final ObjectProperty<Image> imageProperty = imageView.imageProperty();
        imageProperty.bind(sourceView.imageProperty());

        return super.validate(vars);
    }

    @Override
    @FXThread
    public void hide() {

        final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
        previewManager.clear();

        super.hide();
    }
}
