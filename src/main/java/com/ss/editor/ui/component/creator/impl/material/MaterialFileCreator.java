package com.ss.editor.ui.component.creator.impl.material;

import static com.ss.editor.FileExtensions.JME_MATERIAL;
import static java.nio.file.StandardOpenOption.*;
import com.jme3.material.Material;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.creator.FileCreatorDescriptor;
import com.ss.editor.util.EditorUtils;
import com.ss.editor.util.MaterialSerializer;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create a new material.
 *
 * @author JavaSaBr
 */
public class MaterialFileCreator extends GenericFileCreator {

    public static final FileCreatorDescriptor DESCRIPTOR = new FileCreatorDescriptor(
            Messages.MATERIAL_FILE_CREATOR_FILE_DESCRIPTION,
            MaterialFileCreator::new
    );

    private static final String PBR_MAT_DEF = "Common/MatDefs/Light/PBRLighting.j3md";
    private static final String LIGHTING_MAT_DEF = "Common/MatDefs/Light/Lighting.j3md";
    private static final String PROP_MAT_DEF = "matDef";

    /**
     * The list of available definitions.
     */
    @NotNull
    private final Array<String> definitions;

    private MaterialFileCreator() {
        this.definitions = ResourceManager.getInstance()
                .getAvailableResources(FileExtensions.JME_MATERIAL_DEFINITION);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.MATERIAL_FILE_CREATOR_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return JME_MATERIAL;
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {

        String def;

        if (definitions.contains(PBR_MAT_DEF)) {
            def = PBR_MAT_DEF;
        } else if (definitions.contains(LIGHTING_MAT_DEF)) {
            def = LIGHTING_MAT_DEF;
        } else {
            def = definitions.first();
        }

        var type = new PropertyDefinition(EditablePropertyType.STRING_FROM_LIST,
                Messages.MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL, PROP_MAT_DEF, def, definitions);

        return Array.of(type);
    }

    /**
     * Get the list of available definitions.
     *
     * @return the list of available definitions.
     */
    @FromAnyThread
    private @NotNull Array<String> getDefinitions() {
        return definitions;
    }

    @Override
    @FxThread
    protected boolean validate(@NotNull VarTable vars) {

        var matDef = vars.get(PROP_MAT_DEF, String.class, StringUtils.EMPTY);

        if (matDef.isEmpty() || !getDefinitions().contains(matDef)) {
            return false;
        }

        return super.validate(vars);
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull VarTable vars, @NotNull Path resultFile) throws IOException {
        super.writeData(vars, resultFile);

        var assetManager = EditorUtils.getAssetManager();
        var matDef = vars.get(PROP_MAT_DEF, String.class);

        var material = new Material(assetManager, matDef);
        material.getAdditionalRenderState();

        var materialContent = MaterialSerializer.serializeToString(material);

        try (var out = new PrintWriter(Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(materialContent);
        }
    }
}
