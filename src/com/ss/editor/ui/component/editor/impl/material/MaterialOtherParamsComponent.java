package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.BooleanMaterialParamControl;
import com.ss.editor.ui.control.material.FloatMaterialParamControl;
import com.ss.editor.ui.control.material.IntegerMaterialParamControl;
import com.ss.editor.ui.control.material.MaterialParamControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

/**
 * Реализация компонента конфигурирования других параметров материала.
 *
 * @author Ronn
 */
public class MaterialOtherParamsComponent extends TitledPane {

    public static final Insets CONTROL_OFFSET = new Insets(3, 0, 0, 4);

    /**
     * Обрбаотчик внесения изменений.
     */
    private final Consumer<EditorOperation> changeHandler;

    /**
     * Контейнер контролов различных параметров.
     */
    private final VBox container;

    public MaterialOtherParamsComponent(final Consumer<EditorOperation> changeHandler) {
        this.changeHandler = changeHandler;
        this.container = new VBox();
        setText(Messages.MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE);
        setContent(new ScrollPane(container));
        setAnimated(false);

        FXUtils.bindFixedWidth(container, widthProperty().subtract(10));
    }

    /**
     * @return контейнер контролов различных параметров.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * @return обрбаотчик внесения изменений.
     */
    private Consumer<EditorOperation> getChangeHandler() {
        return changeHandler;
    }

    /**
     * Построение настроек для материала.
     */
    public void buildFor(final Material material) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        final MaterialDef materialDef = material.getMaterialDef();

        final List<MatParam> materialParams = new ArrayList<>(materialDef.getMaterialParams());

        Collections.sort(materialParams, (first, second) -> StringUtils.compareIgnoreCase(first.getName(), second.getName()));

        materialParams.forEach(matParam -> buildFor(matParam, material));
    }

    /**
     * Построение контрола для параметра.
     */
    private void buildFor(final MatParam matParam, final Material material) {

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        final VarType varType = matParam.getVarType();

        MaterialParamControl control = null;

        if (varType == VarType.Boolean) {
            control = new BooleanMaterialParamControl(changeHandler, material, matParam.getName());
        } else if (varType == VarType.Int) {
            control = new IntegerMaterialParamControl(changeHandler, material, matParam.getName());
        } else if (varType == VarType.Float) {
            control = new FloatMaterialParamControl(changeHandler, material, matParam.getName());
        }

        if (control == null) {
            return;
        }

        FXUtils.addToPane(control, getContainer());

        VBox.setMargin(control, CONTROL_OFFSET);
    }

    /**
     * @param paramName название обновленного параметра.
     */
    public void updateParam(final String paramName) {

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> {

            if (!(node instanceof MaterialParamControl)) {
                return;
            }

            final MaterialParamControl control = (MaterialParamControl) node;

            if (!StringUtils.equals(control.getParameterName(), paramName)) {
                return;
            }

            control.setIgnoreListeners(true);
            try {
                control.reload();
            } finally {
                control.setIgnoreListeners(false);
            }
        });
    }
}
