package com.ss.editor.plugin.api.editor;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.editor.part3d.Advanced3DEditorPart;
import com.ss.editor.ui.component.editor.state.impl.Editor3DWithEditorToolEditorState;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

/**
 * The advanced implementation of 3D editor with a split right tool.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3DFileEditorWithSplitRightTool<T extends Advanced3DEditorPart, S extends Editor3DWithEditorToolEditorState> extends
        Advanced3DFileEditorWithRightTool<T, S> {

    /**
     * Build split component.
     *
     * @param first the first component.
     * @param second the second component.
     * @param root the root.
     * @return the result component.
     */
    @FxThread
    protected Region buildSplitComponent(@NotNull final Node first, @NotNull final Node second,
                                         @NotNull final StackPane root) {

        final SplitPane splitPane = new SplitPane(first, second);
        splitPane.prefHeightProperty().bind(root.heightProperty());
        splitPane.prefWidthProperty().bind(root.widthProperty());

        root.heightProperty().addListener((observableValue, oldValue, newValue) -> calcVSplitSize(splitPane));

        FXUtils.addClassTo(splitPane, CssClasses.FILE_EDITOR_TOOL_SPLIT_PANE);

        return splitPane;
    }

    /**
     * Calc height of vertical split pane.
     *
     * @param splitPane the split pane
     */
    @FxThread
    protected void calcVSplitSize(@NotNull final SplitPane splitPane) {
        splitPane.setDividerPosition(0, 0.3);
    }
}
