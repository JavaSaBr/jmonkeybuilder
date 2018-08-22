package com.ss.builder.fx.preview.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ResourceManager;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ResourceManager;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.Utils;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Path;

/**
 * The implementation of {@link com.ss.editor.ui.preview.FilePreview} to show text files.
 *
 * @author JavaSaBr
 */
public class TextFilePreview extends AbstractFilePreview<TextArea> {

    @Override
    @FxThread
    protected @NotNull TextArea createGraphicsNode() {
        final TextArea textArea = new TextArea();
        textArea.setEditable(false);
        return textArea;
    }

    @Override
    @FxThread
    protected void initialize(@NotNull final TextArea node, @NotNull final StackPane pane) {
        super.initialize(node, pane);

        node.prefWidthProperty().bind(pane.widthProperty().subtract(2));
        node.prefHeightProperty().bind(pane.heightProperty().subtract(2));

        FXUtils.addClassTo(node, CssClasses.TRANSPARENT_TEXT_AREA);
    }

    @Override
    @FxThread
    public void show(@NotNull final Path file) {
        super.show(file);

        final TextArea textArea = getGraphicsNode();
        textArea.setText(FileUtils.read(file));
    }

    @Override
    @FxThread
    public void show(@NotNull final String resource) {
        super.show(resource);

        final ResourceManager resourceManager = ResourceManager.getInstance();
        final URL url = resourceManager.tryToFindResource(resource);

        String content;

        if (url != null) {
            content = Utils.get(url, toRead -> FileUtils.read(toRead.openStream()));
        } else {
            final Path realFile = EditorUtils.getRealFile(resource);
            content = realFile == null ? "" : FileUtils.read(realFile);
        }

        final TextArea textArea = getGraphicsNode();
        textArea.setText(content);
    }
}
