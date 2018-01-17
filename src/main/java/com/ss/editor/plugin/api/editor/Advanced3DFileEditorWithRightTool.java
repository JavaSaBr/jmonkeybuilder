package com.ss.editor.plugin.api.editor;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.editor.part3d.Advanced3DEditorPart;
import com.ss.editor.ui.component.editor.state.impl.Editor3DWithEditorToolEditorState;
import com.ss.editor.ui.component.split.pane.EditorToolSplitPane;
import com.ss.editor.ui.component.tab.EditorToolComponent;
import com.ss.editor.ui.component.tab.ScrollableEditorToolComponent;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.event.Event;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The advanced implementation of 3D editor with a right tool.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3DFileEditorWithRightTool<T extends Advanced3DEditorPart, S extends Editor3DWithEditorToolEditorState>
        extends Advanced3DFileEditor<T, S> {

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

    /**
     * The pane of 3D editor area.
     */
    @Nullable
    private BorderPane editor3DArea;

    @Override
    @FxThread
    protected void createContent(@NotNull final StackPane root) {
        createEditorAreaPane();

        mainSplitContainer = new EditorToolSplitPane(JFX_APPLICATION.getScene(), root);

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

        editor3DArea = new BorderPane();
        editor3DArea.setOnMousePressed(event -> editor3DArea.requestFocus());
        editor3DArea.setOnKeyReleased(Event::consume);
        editor3DArea.setOnKeyPressed(Event::consume);

        FXUtils.addToPane(editor3DArea, editorAreaPane);
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

    @Override
    @FxThread
    public @Nullable BorderPane get3DArea() {
        return editor3DArea;
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
