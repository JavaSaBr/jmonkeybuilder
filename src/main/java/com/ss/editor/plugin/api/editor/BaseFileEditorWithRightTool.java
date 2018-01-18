package com.ss.editor.plugin.api.editor;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.impl.EditorWithEditorToolEditorState;
import com.ss.editor.ui.component.split.pane.EditorToolSplitPane;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.component.tab.ScrollableEditorToolComponent;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of a file editor without 3D part and with right tool panel.
 *
 * @author JavaSaBr
 */
public abstract class BaseFileEditorWithRightTool<S extends EditorWithEditorToolEditorState> extends BaseFileEditor<S> {

    /**
     * The main split container.
     */
    @Nullable
    private EditorToolSplitPane mainSplitContainer;

    /**
     * Editor tool component.
     */
    @Nullable
    private ScrollableEditorToolComponent editorToolComponent;

    /**
     * The pane of editor area.
     */
    @Nullable
    private StackPane editorAreaPane;

    @Override
    @FxThread
    protected void createContent(@NotNull final StackPane root) {
        createEditorAreaPane();

        mainSplitContainer = new EditorToolSplitPane(EditorUtil.getFxScene(), root);

        editorToolComponent = new ScrollableEditorToolComponent(mainSplitContainer, 1);
        editorToolComponent.prefHeightProperty().bind(root.heightProperty());

        createToolComponents(editorToolComponent, root);

        editorToolComponent.addChangeListener((observable, oldValue, newValue) -> processChangeTool(oldValue, newValue));
        editorToolComponent.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final S editorState = getEditorState();
            if (editorState != null) editorState.setOpenedTool(newValue.intValue());
        });

        mainSplitContainer.initFor(editorToolComponent, getEditorAreaPane());

        FXUtils.addToPane(mainSplitContainer, root);
        FXUtils.addClassTo(mainSplitContainer, CSSClasses.FILE_EDITOR_MAIN_SPLIT_PANE);
    }

    /**
     * Create editor area pane.
     */
    @FxThread
    protected void createEditorAreaPane() {

        editorAreaPane = new StackPane();
        editorAreaPane.setOnDragOver(this::handleDragOverEvent);
        editorAreaPane.setOnDragDropped(this::handleDragDroppedEvent);

        FXUtils.addClassTo(editorAreaPane, CSSClasses.FILE_EDITOR_EDITOR_AREA);
    }

    /**
     * Process change tool.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    @FxThread
    protected void processChangeTool(@Nullable final Number oldValue, @NotNull final Number newValue) {
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        final S editorState = getEditorState();
        if (editorState == null) {
            return;
        }

        editorToolComponent.getSelectionModel().select(editorState.getOpenedTool());
        mainSplitContainer.updateFor(editorState);
    }

    /**
     * @return the pane of editor area.
     */
    @FxThread
    protected @NotNull StackPane getEditorAreaPane() {
        return notNull(editorAreaPane);
    }

    /**
     * Create and add tool components to the container.
     *
     * @param container the tool container.
     * @param root the root.
     */
    @FxThread
    protected void createToolComponents(@NotNull final EditorToolComponent container, @NotNull final StackPane root) {
    }

    /**
     * Handle drag over events.
     *
     * @param dragEvent the drag event.
     */
    @FxThread
    protected void handleDragOverEvent(@NotNull final DragEvent dragEvent) {
    }

    /**
     * Handle dropped events.
     *
     * @param dragEvent the drop event.
     */
    @FxThread
    protected void handleDragDroppedEvent(@NotNull final DragEvent dragEvent) {
    }
}
