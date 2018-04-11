package com.ss.editor.ui.component.creator.impl.material;

import static com.ss.editor.FileExtensions.JME_MATERIAL;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.*;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.MaterialSerializer;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    @NotNull
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    @NotNull
    private static final String PBR_MAT_DEF = "Common/MatDefs/Light/PBRLighting.j3md";

    @NotNull
    private static final String LIGHTING_MAT_DEF = "Common/MatDefs/Light/Lighting.j3md";

    @NotNull
    private static final String PROP_MAT_DEF = "matDef";

    static {
        DESCRIPTION.setFileDescription(Messages.MATERIAL_FILE_CREATOR_FILE_DESCRIPTION);
        DESCRIPTION.setConstructor(MaterialFileCreator::new);
    }

    /**
     * The list of available definitions.
     */
    @Nullable
    private Array<String> definitions;

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

        definitions = RESOURCE_MANAGER.getAvailableResources(FileExtensions.JME_MATERIAL_DEFINITION);

        final String def;

        if (definitions.contains(PBR_MAT_DEF)) {
            def = PBR_MAT_DEF;
        } else if (definitions.contains(LIGHTING_MAT_DEF)) {
            def = LIGHTING_MAT_DEF;
        } else {
            def = definitions.first();
        }

        final Array<PropertyDefinition> result = ArrayFactory.newArray(PropertyDefinition.class);
        result.add(new PropertyDefinition(EditablePropertyType.STRING_FROM_LIST,
                Messages.MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL, PROP_MAT_DEF, def, definitions));

        return result;
    }

    /**
     * @return the list of available definitions.
     */
    @FromAnyThread
    private @NotNull Array<String> getDefinitions() {
        return notNull(definitions);
    }

    @Override
    @FxThread
    protected boolean validate(@NotNull final VarTable vars) {

        final String matDef = vars.get(PROP_MAT_DEF, String.class, StringUtils.EMPTY);

        if (matDef.isEmpty() || !getDefinitions().contains(matDef)) {
            return false;
        }

        return super.validate(vars);
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull final VarTable vars, @NotNull final Path resultFile) throws IOException {
        super.writeData(vars, resultFile);

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final String matDef = vars.get(PROP_MAT_DEF);

        final Material material = new Material(assetManager, matDef);
        material.getAdditionalRenderState();

        final String materialContent = MaterialSerializer.serializeToString(material);

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(materialContent);
        }
    }
}
