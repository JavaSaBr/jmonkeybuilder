package com.ss.builder.plugin.api.editor;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.component.editor.state.impl.EditorWithEditorToolEditorState;
import com.ss.builder.fx.component.split.pane.EditorToolSplitPane;
import com.ss.builder.fx.component.tab.EditorToolComponent;
import com.ss.builder.fx.component.tab.ScrollableEditorToolComponent;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.fx.util.FxUtils;
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
     * The pane of editor area.
     */
    @NotNull
    private final StackPane editorAreaPane;

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

    protected BaseFileEditorWithRightTool() {
        editorAreaPane = new StackPane();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull StackPane root) {
        createEditorAreaPane();

        mainSplitContainer = new EditorToolSplitPane(EditorUtils.getFxScene(), root);

        editorToolComponent = new ScrollableEditorToolComponent(mainSplitContainer, 1);
        editorToolComponent.prefHeightProperty().bind(root.heightProperty());

        createToolComponents(editorToolComponent, root);

        editorToolComponent.addChangeListener((observable, oldValue, newValue) -> processChangeTool(oldValue, newValue));
        editorToolComponent.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var editorState = getEditorState();
            if (editorState != null) {
                editorState.setOpenedTool(newValue.intValue());
            }
        });

        mainSplitContainer.initFor(editorToolComponent, editorAreaPane);

        FxUtils.addClass(mainSplitContainer, CssClasses.FILE_EDITOR_MAIN_SPLIT_PANE);
        FxUtils.addChild(root, mainSplitContainer);
    }

    /**
     * Create editor area pane.
     */
    @FxThread
    protected void createEditorAreaPane() {

        editorAreaPane.setOnDragOver(this::handleDragOverEvent);
        editorAreaPane.setOnDragDropped(this::handleDragDroppedEvent);

        FxUtils.addClass(editorAreaPane, CssClasses.FILE_EDITOR_EDITOR_AREA);
    }

    /**
     * Process change tool.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    @FxThread
    protected void processChangeTool(@Nullable Number oldValue, @NotNull Number newValue) {
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        var editorState = getEditorState();
        if (editorState == null) {
            return;
        }

        editorToolComponent.getSelectionModel().select(editorState.getOpenedTool());
        mainSplitContainer.updateFor(editorState);
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
