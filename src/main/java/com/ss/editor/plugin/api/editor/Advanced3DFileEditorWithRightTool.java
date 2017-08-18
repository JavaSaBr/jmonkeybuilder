package com.ss.editor.plugin.api.editor;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.plugin.api.editor.part3d.Advanced3DEditorState;
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
 * The advanced implementation of 3D editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3DFileEditorWithRightTool<T extends Advanced3DEditorState, S extends Editor3DWithEditorToolEditorState>
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
    private BorderPane editorAreaPane;

    @Override
    @FXThread
    protected void createContent(@NotNull final StackPane root) {
        editorAreaPane = new BorderPane();
        editorAreaPane.setOnMousePressed(event -> editorAreaPane.requestFocus());
        editorAreaPane.setOnDragOver(this::dragOver);
        editorAreaPane.setOnDragDropped(this::dragDropped);
        editorAreaPane.setOnKeyReleased(Event::consume);
        editorAreaPane.setOnKeyPressed(Event::consume);

        mainSplitContainer = new EditorToolSplitPane(JFX_APPLICATION.getScene(), root);

        editorToolComponent = new ScrollableEditorToolComponent(mainSplitContainer, 1);
        editorToolComponent.prefHeightProperty().bind(root.heightProperty());
        editorToolComponent.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final S editorState = getEditorState();
            if (editorState != null) editorState.setOpenedTool(newValue.intValue());
        });

        createToolComponents(editorToolComponent);

        mainSplitContainer.initFor(editorToolComponent, editorAreaPane);

        FXUtils.addToPane(mainSplitContainer, root);
        FXUtils.addClassTo(mainSplitContainer, CSSClasses.FILE_EDITOR_MAIN_SPLIT_PANE);
    }

    @Override
    @FXThread
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
    @FXThread
    public @Nullable BorderPane get3DArea() {
        return editorAreaPane;
    }

    /**
     * Create and add tool components to the container.
     *
     * @param container the tool container.
     */
    protected void createToolComponents(@NotNull final EditorToolComponent container) {
    }

    /**
     * Handle drag objects.
     */
    protected void dragOver(@NotNull final DragEvent dragEvent) {
    }

    /**
     * Handle dropped texture.
     */
    protected void dragDropped(@NotNull final DragEvent dragEvent) {
    }
}
