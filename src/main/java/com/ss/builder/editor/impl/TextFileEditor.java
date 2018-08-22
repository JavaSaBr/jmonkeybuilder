package com.ss.builder.editor.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.Messages;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.editor.EditorDescriptor;
import com.ss.builder.editor.EditorRegistry;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The implementation of editor to edit text files.
 *
 * @author JavaSaBr
 */
public class TextFileEditor extends AbstractFileEditor<VBox> {

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            TextFileEditor::new,
            Messages.TEXT_FILE_EDITOR_NAME,
            TextFileEditor.class.getSimpleName(),
            EditorRegistry.ALL_FORMATS
    );

    /**
     * The text area.
     */
    @NotNull
    private final TextArea textArea;

    /**
     * The original content of the opened file.
     */
    @Nullable
    private String originalContent;

    private TextFileEditor() {
        this.textArea = new TextArea();
    }

    @Override
    @FxThread
    protected @NotNull VBox createRoot() {
        return new VBox();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {

        textArea.prefHeightProperty()
                .bind(root.heightProperty());
        textArea.prefWidthProperty()
                .bind(root.widthProperty());

        FxControlUtils.onTextChange(textArea, this::updateDirty);

        FxUtils.addClass(textArea, CssClasses.TRANSPARENT_TEXT_AREA);
        FxUtils.addChild(root, textArea);
    }

    /**
     * Update dirty state.
     */
    @FxThread
    private void updateDirty(@NotNull String newContent) {
        setDirty(!getOriginalContent().equals(newContent));
    }

    @Override
    @FxThread
    protected boolean needToolbar() {
        return true;
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull HBox container) {
        super.createToolbar(container);
        FxUtils.addChild(container, createSaveAction());
    }

    @Override
    @FxThread
    public void openFile(@NotNull Path file) {
        super.openFile(file);

        setOriginalContent(FileUtils.read(file));

        /* TODO added to handle some exceptions
        try {

        } catch (final MalformedInputException e) {
            throw new RuntimeException("This file isn't a text file.", e);
        } */

        textArea.setText(getOriginalContent());
    }

    /**
     * Get the original content of the opened file.
     *
     * @return the original content of the opened file.
     */
    @FxThread
    private @NotNull String getOriginalContent() {
        return notNull(originalContent);
    }

    /**
     * Set the original content of the opened file.
     *
     * @param originalContent the original content of the opened file.
     */
    @FxThread
    private void setOriginalContent(@NotNull String originalContent) {
        this.originalContent = originalContent;
    }

    @Override
    @BackgroundThread
    public void doSave(@NotNull Path toStore) throws Throwable {
        super.doSave(toStore);

        var newContent = textArea.getText();

        try (var out = new PrintWriter(Files.newOutputStream(toStore))) {
            out.print(newContent);
        }
    }

    @Override
    @FxThread
    protected void postSave() {
        super.postSave();

        var newContent = textArea.getText();

        setOriginalContent(newContent);
        updateDirty(newContent);
    }

    @Override
    @FxThread
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        var newContent = FileUtils.read(getFile());
        var currentContent = textArea.getText();

        textArea.setText(newContent);
        setOriginalContent(currentContent);
        updateDirty(newContent);
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }
}
