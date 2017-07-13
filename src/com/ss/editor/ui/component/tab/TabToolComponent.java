package com.ss.editor.ui.component.tab;

import com.ss.editor.ui.component.ScreenComponent;
import com.ss.rlib.ui.util.FXUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The component to contain tool components.
 *
 * @author JavaSaBr
 */
public class TabToolComponent extends TabPane implements ScreenComponent {

    /**
     * The split pane.
     */
    @NotNull
    protected final SplitPane pane;

    /**
     * The collapsed property.
     */
    @NotNull
    protected final BooleanProperty collapsed;

    /**
     * The last expand position.
     */
    private double expandPosition;

    /**
     * The flag of changing the tab.
     */
    private boolean changingTab;

    /**
     * Instantiates a new Tab tool component.
     *
     * @param pane the pane
     */
    protected TabToolComponent(@NotNull final SplitPane pane) {
        this.collapsed = new SimpleBooleanProperty(this, "collapsed", false);
        this.pane = pane;
        bindCollapsedProperty();
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::processMouseClick);
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changedTab(oldValue));
    }

    /**
     * Bind collapsed property.
     */
    protected void bindCollapsedProperty() {
        this.collapsed.bind(widthProperty().lessThanOrEqualTo(minWidthProperty()));
    }

    /**
     * Add a change tab index listener.
     *
     * @param listener the listener.
     */
    public void addChangeListener(@NotNull final ChangeListener<? super Number> listener) {
        getSelectionModel().selectedIndexProperty().addListener(listener);
    }

    /**
     * Sets expand position.
     *
     * @param expandPosition the last expand position.
     */
    public void setExpandPosition(final double expandPosition) {
        this.expandPosition = expandPosition;
    }

    /**
     * @return the last expand position.
     */
    private double getExpandPosition() {
        return expandPosition;
    }

    /**
     * @param changingTab true if now is changing tab.
     */
    private void setChangingTab(final boolean changingTab) {
        this.changingTab = changingTab;
    }

    /**
     * @return true if now is changing tab.
     */
    private boolean isChangingTab() {
        return changingTab;
    }

    /**
     * Handle a changed tab.
     */
    private void changedTab(@Nullable final Tab oldValue) {
        if (isCollapsed()) expand();
        setChangingTab(oldValue != null);
    }

    /**
     * Add a new component to this tool container.
     *
     * @param component the component
     * @param name      the name
     */
    public void addComponent(@NotNull final Region component, @NotNull final String name) {

        final Tab tab = new Tab(name);
        tab.setContent(component);
        tab.setClosable(false);

        getTabs().add(tab);

        FXUtils.bindFixedHeight(component, heightProperty());
    }

    /**
     * Handle a click to a tab.
     */
    private void processMouseClick(@NotNull final MouseEvent event) {
        final EventTarget target = event.getTarget();
        if (!(target instanceof Node)) return;
        final Node node = (Node) target;
        if (!(node instanceof Text || node.getStyleClass().contains("tab-container"))) return;

        if (isChangingTab()) {
            setChangingTab(false);
            return;
        }

        processExpandOrCollapse();
    }

    /**
     * Process to calculate what need to expand or collapse,
     */
    protected void processExpandOrCollapse() {

        final double minWidth = getMinWidth();
        final double width = getWidth();

        if (width <= minWidth) {
            expand();
        } else {
            collapse();
        }
    }

    /**
     * Is collapsed boolean.
     *
     * @return true if this is collapsed.
     */
    public boolean isCollapsed() {
        return collapsed.get();
    }

    /**
     * Collapse selected tab.
     */
    public void collapse() {
        setCollapseSize();
        pane.setDividerPosition(getDividerIndex(), getCollapsePosition());
    }

    /**
     * Set collapse size.
     */
    protected void setCollapseSize() {
        maxWidthProperty().bind(minWidthProperty());
    }

    /**
     * Gets collapse position.
     *
     * @return the collapse position.
     */
    protected int getCollapsePosition() {
        return 0;
    }

    /**
     * Expand selected tab.
     */
    protected void expand() {

        final int dividerIndex = getDividerIndex();
        final double position = getExpandPosition();

        expand(dividerIndex, position);
    }

    /**
     * @return the divider index.
     */
    private int getDividerIndex() {
        return 0;
    }

    /**
     * Expand selected tab.
     */
    private void expand(final int dividerIndex, final double position) {
        setExpandSize();
        pane.setDividerPosition(dividerIndex, position);
    }

    /**
     * Set collapse size.
     */
    protected void setExpandSize() {
        maxWidthProperty().unbind();
        setMaxWidth(USE_COMPUTED_SIZE);
    }
}
