package com.ss.editor.ui.component.editor.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The implementation of editor to edit text files.
 *
 * @author JavaSaBr
 */
public class TextFileEditor extends AbstractFileEditor<VBox> {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(TextFileEditor::new);
        DESCRIPTION.setEditorName(Messages.TEXT_FILE_EDITOR_NAME);
        DESCRIPTION.setEditorId(TextFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(EditorRegistry.ALL_FORMATS);
    }

    /**
     * The original content of the opened file.
     */
    @Nullable
    private String originalContent;

    /**
     * The text area.
     */
    @Nullable
    private TextArea textArea;

    @Override
    @FXThread
    protected @NotNull VBox createRoot() {
        return new VBox();
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final VBox root) {

        textArea = new TextArea();
        textArea.textProperty().addListener((observable, oldValue, newValue) -> updateDirty(newValue));
        textArea.prefHeightProperty().bind(root.heightProperty());
        textArea.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(textArea, root);
        FXUtils.addClassesTo(textArea, CSSClasses.TRANSPARENT_TEXT_AREA);
    }

    /**
     * Update dirty state.
     */
    @FXThread
    private void updateDirty(final String newContent) {
        setDirty(!getOriginalContent().equals(newContent));
    }

    @Override
    @FXThread
    protected boolean needToolbar() {
        return true;
    }

    @Override
    @FXThread
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);
        FXUtils.addToPane(createSaveAction(), container);
    }

    /**
     * @return the text area.
     */
    @FXThread
    private @NotNull TextArea getTextArea() {
        return notNull(textArea);
    }

    @Override
    @FXThread
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        setOriginalContent(FileUtils.read(file));

        /* TODO added to handle some exceptions
        try {

        } catch (final MalformedInputException e) {
            throw new RuntimeException("This file isn't a text file.", e);
        } */

        final TextArea textArea = getTextArea();
        textArea.setText(getOriginalContent());
    }

    /**
     * @return the original content of the opened file.
     */
    @FXThread
    private @NotNull String getOriginalContent() {
        return notNull(originalContent);
    }

    /**
     * @param originalContent the original content of the opened file.
     */
    @FXThread
    private void setOriginalContent(@NotNull final String originalContent) {
        this.originalContent = originalContent;
    }

    @Override
    @BackgroundThread
    public void doSave(@NotNull final Path toStore) throws IOException {
        super.doSave(toStore);

        final TextArea textArea = getTextArea();
        final String newContent = textArea.getText();

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(toStore))) {
            out.print(newContent);
        }
    }

    @Override
    @FXThread
    protected void postSave() {
        super.postSave();

        final TextArea textArea = getTextArea();
        final String newContent = textArea.getText();

        setOriginalContent(newContent);
        updateDirty(newContent);
    }

    @Override
    @FXThread
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        final String newContent = FileUtils.read(getEditFile());

        final TextArea textArea = getTextArea();
        final String currentContent = textArea.getText();
        textArea.setText(newContent);

        setOriginalContent(currentContent);
        updateDirty(newContent);
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
