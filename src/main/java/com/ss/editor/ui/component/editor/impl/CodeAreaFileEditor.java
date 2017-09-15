package com.ss.editor.ui.component.editor.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.control.code.BaseCodeArea;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

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
    private BaseCodeArea codeArea;

    @Override
    @FXThread
    protected @NotNull VBox createRoot() {
        return new VBox();
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final VBox root) {

        codeArea = createCodeArea();
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> updateDirty(newValue));
        codeArea.prefHeightProperty().bind(root.heightProperty());
        codeArea.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(codeArea, root);
        FXUtils.addClassTo(codeArea, CSSClasses.TEXT_EDITOR_TEXT_AREA);
    }

    /**
     * Create the code area.
     *
     * @return the code area.
     */
    @FXThread
    protected @NotNull BaseCodeArea createCodeArea() {
        throw new RuntimeException();
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
     * @return the code area.
     */
    @FXThread
    private @NotNull BaseCodeArea getCodeArea() {
        return notNull(codeArea);
    }

    @Override
    @FXThread
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        setOriginalContent(FileUtils.read(file));

        final BaseCodeArea codeArea = getCodeArea();
        codeArea.loadContent(getOriginalContent());

        setOriginalContent(codeArea.getText());
        updateDirty(getOriginalContent());
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

        final CodeArea codeArea = getCodeArea();
        final String newContent = codeArea.getText();

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(toStore))) {
            out.print(newContent);
        }
    }

    @Override
    @FXThread
    protected void postSave() {
        super.postSave();

        final CodeArea codeArea = getCodeArea();
        final String newContent = codeArea.getText();

        setOriginalContent(newContent);
        updateDirty(newContent);
    }

    @Override
    @FXThread
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        final String newContent = FileUtils.read(getEditFile());

        final BaseCodeArea codeArea = getCodeArea();
        final String currentContent = codeArea.getText();
        codeArea.reloadContent(newContent);

        setOriginalContent(currentContent);
        updateDirty(newContent);
    }
}
