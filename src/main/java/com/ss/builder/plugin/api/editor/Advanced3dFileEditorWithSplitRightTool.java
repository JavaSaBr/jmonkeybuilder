package com.ss.builder.plugin.api.editor;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.editor.part3d.Advanced3dFileEditor3dEditorPart;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.editor.part3d.Advanced3dFileEditor3dEditorPart;
import com.ss.builder.fx.component.editor.state.impl.Editor3dWithEditorToolEditorState;
import com.ss.builder.fx.css.CssClasses;
import com.ss.rlib.fx.util.FxUtils;
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
public abstract class Advanced3dFileEditorWithSplitRightTool<T extends Advanced3dFileEditor3dEditorPart, S extends Editor3dWithEditorToolEditorState> extends
        Advanced3dFileEditorWithRightTool<T, S> {

    /**
     * Build split component.
     *
     * @param first the first component.
     * @param second the second component.
     * @param root the root.
     * @return the result component.
     */
    @FxThread
    protected Region buildSplitComponent(@NotNull Node first, @NotNull Node second, @NotNull StackPane root) {

        var splitPane = new SplitPane(first, second);
        splitPane.prefHeightProperty()
                .bind(root.heightProperty());
        splitPane.prefWidthProperty()
                .bind(root.widthProperty());

        root.heightProperty()
                .addListener((observableValue, oldValue, newValue) -> calcVSplitSize(splitPane));

        FxUtils.addClass(splitPane, CssClasses.FILE_EDITOR_TOOL_SPLIT_PANE);

        return splitPane;
    }

    /**
     * Calc height of vertical split pane.
     *
     * @param splitPane the split pane
     */
    @FxThread
    protected void calcVSplitSize(@NotNull SplitPane splitPane) {
        splitPane.setDividerPosition(0, 0.3);
    }
}
