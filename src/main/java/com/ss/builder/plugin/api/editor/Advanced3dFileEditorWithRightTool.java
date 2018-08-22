package com.ss.builder.plugin.api.editor;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.editor.part3d.Advanced3dFileEditor3dEditorPart;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.editor.state.impl.Editor3dWithEditorToolEditorState;
import com.ss.builder.fx.component.split.pane.EditorToolSplitPane;
import com.ss.builder.fx.component.tab.EditorToolComponent;
import com.ss.builder.fx.component.tab.ScrollableEditorToolComponent;
import com.ss.rlib.fx.util.FxUtils;
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
public abstract class Advanced3dFileEditorWithRightTool<T extends Advanced3dFileEditor3dEditorPart, S extends Editor3dWithEditorToolEditorState>
        extends Advanced3dFileEditor<T, S> {

    /**
     * The pane of editor area.
     */
    @NotNull
    protected final StackPane editorAreaPane;

    /**
     * The pane of 3D editor area.
     */
    @NotNull
    private final BorderPane editor3dArea;

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

    protected Advanced3dFileEditorWithRightTool() {
        this.editorAreaPane = new StackPane();
        this.editor3dArea = new BorderPane();
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

        editor3dArea.setOnMousePressed(event -> editor3dArea.requestFocus());
        editor3dArea.setOnKeyReleased(Event::consume);
        editor3dArea.setOnKeyPressed(Event::consume);

        FxUtils.addClass(editorAreaPane, CssClasses.FILE_EDITOR_EDITOR_AREA);
        FxUtils.addChild(editorAreaPane, editor3dArea);
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

        getEditorToolComponent().getSelectionModel()
                .select(editorState.getOpenedTool());

        getMainSplitContainer().updateFor(editorState);
    }

    @Override
    @FxThread
    public @NotNull BorderPane get3dArea() {
        return editor3dArea;
    }

    /**
     * Get the editor tool component.
     *
     * @return the editor tool component.
     */
    @FxThread
    protected @NotNull ScrollableEditorToolComponent getEditorToolComponent() {
           return notNull(editorToolComponent);
    }

    /**
     * Get the main split container.
     *
     * @return the main split container.
     */
    @FxThread
    protected @NotNull EditorToolSplitPane getMainSplitContainer() {
        return notNull(mainSplitContainer);
    }

    /**
     * Create and add tool components to the container.
     *
     * @param container the tool container.
     * @param root the root.
     */
    @FxThread
    protected void createToolComponents(@NotNull EditorToolComponent container, @NotNull StackPane root) {
    }

    /**
     * Handle drag over events.
     *
     * @param dragEvent the drag event.
     */
    @FxThread
    protected void handleDragOverEvent(@NotNull DragEvent dragEvent) {
    }

    /**
     * Handle dropped events.
     *
     * @param dragEvent the drop event.
     */
    @FxThread
    protected void handleDragDroppedEvent(@NotNull DragEvent dragEvent) {
    }
}
