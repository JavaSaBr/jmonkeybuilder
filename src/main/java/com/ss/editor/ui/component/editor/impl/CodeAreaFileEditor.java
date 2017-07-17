package com.ss.editor.ui.component.editor.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.undo.UndoManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/**
 * The implementation of editor to edit files with code.
 *
 * @author JavaSaBr
 */
public abstract class CodeAreaFileEditor extends AbstractFileEditor<VBox> {

    /**
     * The original content of the opened file.
     */
    @Nullable
    private String originalContent;

    /**
     * The code area.
     */
    @Nullable
    private CodeArea codeArea;

    @NotNull
    @Override
    protected VBox createRoot() {
        return new VBox();
    }

    @Override
    protected void createContent(@NotNull final VBox root) {

        codeArea = new CodeArea();
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, getStyleSpans(codeArea.getText())));
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> updateDirty(newValue));
        codeArea.prefHeightProperty().bind(root.heightProperty());
        codeArea.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(codeArea, root);
        FXUtils.addClassTo(codeArea, CSSClasses.TEXT_EDITOR_TEXT_AREA);
    }

    /**
     * Gets style spans.
     *
     * @param text the text
     * @return the style spans
     */
    @NotNull
    protected StyleSpans<? extends Collection<String>> getStyleSpans(@NotNull final String text) {
        throw new RuntimeException("unsupported");
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
     * @return the code area.
     */
    @NotNull
    private CodeArea getCodeArea() {
        return notNull(codeArea);
    }

    @FXThread
    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        setOriginalContent(FileUtils.read(file));

        final CodeArea codeArea = getCodeArea();
        codeArea.appendText(getOriginalContent());

        final UndoManager undoManager = codeArea.getUndoManager();
        undoManager.forgetHistory();

        setOriginalContent(codeArea.getText());
        updateDirty(getOriginalContent());
    }

    /**
     * @return the original content of the opened file.
     */
    @NotNull
    private String getOriginalContent() {
        return notNull(originalContent);
    }

    /**
     * @param originalContent the original content of the opened file.
     */
    private void setOriginalContent(@NotNull final String originalContent) {
        this.originalContent = originalContent;
    }

    @Override
    public void doSave() {
        super.doSave();

        final CodeArea codeArea = getCodeArea();
        final String newContent = codeArea.getText();

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(getEditFile()))) {
            out.print(newContent);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setOriginalContent(newContent);
        updateDirty(newContent);
    }

    @Override
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        final String newContent = FileUtils.read(getEditFile());

        final CodeArea codeArea = getCodeArea();
        final String currentContent = codeArea.getText();
        codeArea.replaceText(0, currentContent.length(), newContent);

        setOriginalContent(currentContent);
        updateDirty(newContent);
    }
}
