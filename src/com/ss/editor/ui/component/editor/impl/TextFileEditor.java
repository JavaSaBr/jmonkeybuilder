package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.FileUtils;
import rlib.util.StringUtils;

import static com.ss.editor.ui.css.CSSIds.TEXT_EDITOR_TEXT_AREA;

/**
 * Реализация редактора текстовых файлов.
 *
 * @author Ronn
 */
public class TextFileEditor extends AbstractFileEditor<VBox> {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(TextFileEditor::new);
        DESCRIPTION.setEditorName(Messages.TEXT_FILE_EDITOR_NAME);
        DESCRIPTION.addExtension(EditorRegistry.ALL_FORMATS);
    }

    /**
     * Контент на момент открытия документа.
     */
    private String initContent;

    /**
     * Область для редактирования текста.
     */
    private TextArea textArea;

    @Override
    protected VBox createRoot() {
        return new VBox();
    }

    @Override
    protected void createContent(final VBox root) {

        textArea = new TextArea();
        textArea.setId(TEXT_EDITOR_TEXT_AREA);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> updateDirty(newValue));

        FXUtils.addToPane(textArea, root);
        FXUtils.bindFixedSize(textArea, root.widthProperty(), root.heightProperty());
    }

    private void updateDirty(final String newValue) {
        setDirty(!getInitContent().equals(newValue));
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(final HBox container) {
        super.createToolbar(container);

        final Button saveAction = createSaveAction();

        FXUtils.addToPane(saveAction, container);
    }

    /**
     * @return область для редактирования текста.
     */
    private TextArea getTextArea() {
        return textArea;
    }

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final byte[] content = FileUtils.getContent(file);

        if (content == null) {
            setInitContent(StringUtils.EMPTY);
        } else {
            setInitContent(new String(content));
        }

        final TextArea textArea = getTextArea();
        textArea.setText(getInitContent());
    }

    /**
     * @return контент на момент открытия документа.
     */
    public String getInitContent() {
        return initContent;
    }

    /**
     * @param initContent контент на момент открытия документа.
     */
    public void setInitContent(String initContent) {
        this.initContent = initContent;
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

        setInitContent(newContent);
        updateDirty(newContent);
    }
}
