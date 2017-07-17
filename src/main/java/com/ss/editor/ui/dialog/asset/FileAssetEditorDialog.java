package com.ss.editor.ui.dialog.asset;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.control.Label;

/**
 * The implementation of the {@link AssetEditorDialog} for choosing the {@link Path} from asset.
 *
 * @author JavaSaBr
 */
public class FileAssetEditorDialog extends AssetEditorDialog<Path> {

    /**
     * Instantiates a new File asset editor dialog.
     *
     * @param consumer the consumer
     */
    public FileAssetEditorDialog(@NotNull final Consumer<Path> consumer) {
        super(consumer);
    }

    /**
     * Instantiates a new File asset editor dialog.
     *
     * @param consumer  the consumer
     * @param validator the validator
     */
    public FileAssetEditorDialog(@NotNull final Consumer<Path> consumer, @Nullable final Function<Path, String> validator) {
        super(consumer, validator);
    }

    @Override
    protected void processOpen(@NotNull final ResourceElement element) {
        super.processOpen(element);
        final Consumer<Path> consumer = getConsumer();
        consumer.accept(element.getFile());
    }

    @Override
    protected void validate(@NotNull final Label warningLabel, @Nullable final ResourceElement element) {
        super.validate(warningLabel, element);

        if (element == null) {
            LOGGER.warning(this, "The element is null.");
            return;
        }

        final Function<Path, String> validator = getValidator();
        String message = validator == null ? null : validator.apply(element.getFile());

        if (message != null) {
            warningLabel.setText(message);
        }

        warningLabel.setVisible(message != null);
    }
}
