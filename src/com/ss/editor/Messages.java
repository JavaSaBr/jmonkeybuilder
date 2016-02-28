package com.ss.editor;

import java.util.ResourceBundle;

/**
 * Набор констант с локализованными сообщениями.
 *
 * @author Ronn
 */
public class Messages {

    public static final String BUNDLE_NAME = "messages/messages";

    public static final String EDITOR_BAR_ASSET;
    public static final String EDITOR_BAR_ASSET_OPEN_ASSET;
    public static final String EDITOR_BAR_ASSET_OPEN_ASSET_DIRECTORY_CHOOSER;

    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;

    public static final String FILE_EDITOR_ACTION_SAVE;
    public static final String FILE_EDITOR_TEXT_EDITOR;

    public static final String POST_FILTER_EDITOR_MATERIAL_LABEL;

    public static final String ASSET_EDITOR_DIALOG_TITLE;
    public static final String ASSET_EDITOR_DIALOG_BUTTON_OK;
    public static final String ASSET_EDITOR_DIALOG_BUTTON_CANCEL;

    public static final String MATERIAL_EDITOR_MATERIAL_TYPE_LABEL;

    public static final String MATERIAL_TEXTURES_COMPONENT_TITLE;
    public static final String MATERIAL_COLORS_COMPONENT_TITLE;
    public static final String MATERIAL_OTHER_COMPONENT_TITLE;
    public static final String MATERIAL_RENDER_PARAMS_COMPONENT_TITLE;

    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT;
    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP;
    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD;
    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE;

    public static final String COLOR_MATERIAL_PARAM_CONTROL_REMOVE;

    public static final String MATERIAL_RENDER_STATE_FACE_CULL_MODE;
    public static final String MATERIAL_RENDER_STATE_BLEND_MODE;
    public static final String MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR;
    public static final String MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS;
    public static final String MATERIAL_RENDER_STATE_POINT_SPRITE;
    public static final String MATERIAL_RENDER_STATE_DEPTH_WRITE;
    public static final String MATERIAL_RENDER_STATE_COLOR_WRITE;
    public static final String MATERIAL_RENDER_STATE_DEPTH_TEST;
    public static final String MATERIAL_RENDER_STATE_WIREFRAME;

    static {

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, ResourceControl.getInstance());

        EDITOR_BAR_ASSET = bundle.getString("EditorBarComponent.asset");
        EDITOR_BAR_ASSET_OPEN_ASSET = bundle.getString("EditorBarComponent.asset.openAsset");
        EDITOR_BAR_ASSET_OPEN_ASSET_DIRECTORY_CHOOSER = bundle.getString("EditorBarComponent.asset.openAsset.DirectoryChooser");

        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE = bundle.getString("AssetComponentResourceTreeContextMenuOpenFile");

        FILE_EDITOR_ACTION_SAVE = bundle.getString("FileEditorActionSave");
        FILE_EDITOR_TEXT_EDITOR = bundle.getString("FileEditorTextEditor");

        POST_FILTER_EDITOR_MATERIAL_LABEL = bundle.getString("PostFilterEditorMaterialListLabel");

        ASSET_EDITOR_DIALOG_TITLE = bundle.getString("AssetEditorDialogTitle");
        ASSET_EDITOR_DIALOG_BUTTON_OK = bundle.getString("AssetEditorDialogButtonOk");
        ASSET_EDITOR_DIALOG_BUTTON_CANCEL = bundle.getString("AssetEditorDialogButtonCancel");

        MATERIAL_TEXTURES_COMPONENT_TITLE = bundle.getString("MaterialTexturesComponentTitle");
        MATERIAL_COLORS_COMPONENT_TITLE = bundle.getString("MaterialColorsComponentTitle");
        MATERIAL_OTHER_COMPONENT_TITLE = bundle.getString("MaterialOtherComponentTitle");
        MATERIAL_RENDER_PARAMS_COMPONENT_TITLE = bundle.getString("MaterialRenderParamsComponentTitle");

        TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT = bundle.getString("Texture2DMaterialParamControlRepeat");
        TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP = bundle.getString("Texture2DMaterialParamControlFlip");
        TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD = bundle.getString("Texture2DMaterialParamControlAdd");
        TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE = bundle.getString("Texture2DMaterialParamControlRemove");

        COLOR_MATERIAL_PARAM_CONTROL_REMOVE = bundle.getString("ColorMaterialParamControlRemove");

        MATERIAL_RENDER_STATE_FACE_CULL_MODE = bundle.getString("MaterialRenderStateFaceCullMode");
        MATERIAL_RENDER_STATE_BLEND_MODE = bundle.getString("MaterialRenderStateBlendMode");
        MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR = bundle.getString("MaterialRenderStatePolyOffsetFactor");
        MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS = bundle.getString("MaterialRenderStatePolyOffsetUnits");
        MATERIAL_RENDER_STATE_POINT_SPRITE = bundle.getString("MaterialRenderStatePointSprite");
        MATERIAL_RENDER_STATE_DEPTH_WRITE = bundle.getString("MaterialRenderStateDepthWrite");
        MATERIAL_RENDER_STATE_COLOR_WRITE = bundle.getString("MaterialRenderStateColorWrite");
        MATERIAL_RENDER_STATE_DEPTH_TEST = bundle.getString("MaterialRenderStateDepthTest");
        MATERIAL_RENDER_STATE_WIREFRAME = bundle.getString("MaterialRenderStateWireframe");

        MATERIAL_EDITOR_MATERIAL_TYPE_LABEL = bundle.getString("MaterialEditorMaterialTypeLabel");
    }
}