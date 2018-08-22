package com.ss.builder.editor.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.control.code.BaseCodeArea;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * The code area.
     */
    @NotNull
    private final BaseCodeArea codeArea;

    /**
     * The original content of the opened file.
     */
    @Nullable
    private volatile String originalContent;

    protected CodeAreaFileEditor() {
        this.codeArea = createCodeArea();
    }

    @Override
    @FxThread
    protected @NotNull VBox createRoot() {
        return new VBox();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {

        codeArea.prefHeightProperty()
                .bind(root.heightProperty());
        codeArea.prefWidthProperty()
                .bind(root.widthProperty());
        codeArea.textProperty()
                .addListener((observable, oldValue, newValue) -> updateDirty(newValue));

        FxUtils.addClass(codeArea, CssClasses.TEXT_EDITOR_TEXT_AREA);
        FxUtils.addChild(root, codeArea);
    }

    /**
     * Create the code area.
     *
     * @return the code area.
     */
    @FxThread
    protected @NotNull BaseCodeArea createCodeArea() {
        throw new RuntimeException();
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
    @BackgroundThread
    public void openFile(@NotNull Path file) {
        super.openFile(file);

        setOriginalContent(FileUtils.read(file));

        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {
            codeArea.loadContent(getOriginalContent());
            setOriginalContent(codeArea.getText());
            updateDirty(getOriginalContent());
        });
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

        var newContent = codeArea.getText();

        try (var out = new PrintWriter(Files.newOutputStream(toStore))) {
            out.print(newContent);
        }
    }

    @Override
    @FxThread
    protected void postSave() {
        super.postSave();

        var newContent = codeArea.getText();

        setOriginalContent(newContent);
        updateDirty(newContent);
    }

    @Override
    @FxThread
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        var newContent = FileUtils.read(getFile());
        var currentContent = codeArea.getText();

        codeArea.reloadContent(newContent);

        setOriginalContent(currentContent);
        updateDirty(newContent);
    }
}
