package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.Util;

/**
 * The implementation of editor for editing text files.
 *
 * @author JavaSaBr
 */
public class TextFileEditor extends AbstractFileEditor<VBox> {

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
    private String originalContent;

    /**
     * The text area.
     */
    private TextArea textArea;

    @NotNull
    @Override
    protected VBox createRoot() {
        return new VBox();
    }

    @Override
    protected void createContent(@NotNull final VBox root) {

        textArea = new TextArea();
        textArea.setId(CSSIds.TEXT_EDITOR_TEXT_AREA);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> updateDirty(newValue));
        textArea.prefHeightProperty().bind(root.heightProperty());
        textArea.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(textArea, root);
        FXUtils.addClassTo(textArea, CSSClasses.MAIN_FONT_13);
    }

    /**
     * Update dirty state.
     */
    private void updateDirty(final String newContent) {
        setDirty(!getOriginalContent().equals(newContent));
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);
        FXUtils.addToPane(createSaveAction(), container);
    }

    /**
     * @return the text area.
     */
    private TextArea getTextArea() {
        return textArea;
    }

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final byte[] content = Util.safeGet(file, Files::readAllBytes);

        setOriginalContent(new String(content));

        final TextArea textArea = getTextArea();
        textArea.setText(getOriginalContent());
    }

    /**
     * @return the original content of the opened file.
     */
    public String getOriginalContent() {
        return originalContent;
    }

    /**
     * @param originalContent the original content of the opened file.
     */
    public void setOriginalContent(final String originalContent) {
        this.originalContent = originalContent;
    }

    @Override
    public void doSave() {
        super.doSave();

        final TextArea textArea = getTextArea();
        final String newContent = textArea.getText();

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(getEditFile()))) {
            out.print(newContent);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setOriginalContent(newContent);
        updateDirty(newContent);
        notifyFileChanged();
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
