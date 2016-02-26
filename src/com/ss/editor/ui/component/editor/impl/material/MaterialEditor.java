package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.ss.editor.state.editor.impl.material.MaterialEditorState;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;

import javafx.scene.layout.StackPane;

/**
 * Реализация редактора для редактирования материалов.
 *
 * @author Ronn
 */
public class MaterialEditor extends AbstractFileEditor<StackPane> {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialEditor::new);
        DESCRIPTION.setEditorName("MaterialEditor");
        DESCRIPTION.addExtension("j3m");
    }

    /**
     * 3D часть редактора.
     */
    private final MaterialEditorState editorState;

    /**
     * Текущий редактируемый материал.
     */
    private Material currentMaterial;

    public MaterialEditor() {
        this.editorState = new MaterialEditorState();
        addEditorState(editorState);
    }

    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void createContent(final StackPane root) {

    }

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final Path assetFile = EditorUtil.getAssetFile(file);
        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material material = assetManager.loadMaterial(assetFile.toString());

        final MaterialEditorState editorState = getEditorState();
        editorState.changeMode(MaterialEditorState.ModelType.BOX);
        editorState.updateMaterial(material);
    }

    /**
     * @param currentMaterial текущий редактируемый материал.
     */
    private void setCurrentMaterial(Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    /**
     * @return текущий редактируемый материал.
     */
    private Material getCurrentMaterial() {
        return currentMaterial;
    }

    /**
     * @return 3D часть редактора.
     */
    private MaterialEditorState getEditorState() {
        return editorState;
    }
}
