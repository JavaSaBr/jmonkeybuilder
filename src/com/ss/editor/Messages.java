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
    public static final String EDITOR_BAR_ASSET_REOPEN_ASSET_FOLDER;
    public static final String EDITOR_BAR_ASSET_CLOSE_EDITOR;
    public static final String EDITOR_BAR_SETTINGS;
    public static final String EDITOR_BAR_SETTINGS_GRAPHICS;

    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CONVERT_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_EXTERNAL_EDITOR;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_RENAME_FILE;

    public static final String FILE_EDITOR_ACTION_SAVE;

    public static final String POST_FILTER_EDITOR_MATERIAL_LABEL;

    public static final String ASSET_EDITOR_DIALOG_TITLE;
    public static final String ASSET_EDITOR_DIALOG_BUTTON_OK;
    public static final String ASSET_EDITOR_DIALOG_BUTTON_CANCEL;

    public static final String MATERIAL_EDITOR_MATERIAL_TYPE_LABEL;
    public static final String MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL;

    public static final String MATERIAL_FILE_EDITOR_TEXTURES_COMPONENT_TITLE;
    public static final String MATERIAL_FILE_EDITOR_COLORS_COMPONENT_TITLE;
    public static final String MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE;
    public static final String MATERIAL_FILE_EDITOR_RENDER_PARAMS_COMPONENT_TITLE;

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

    public static final String TEXT_FILE_EDITOR_NAME;
    public static final String POST_FILTER_EDITOR_NAME;
    public static final String MATERIAL_EDITOR_NAME;

    public static final String FILE_CREATOR_BUTTON_OK;
    public static final String FILE_CREATOR_BUTTON_CANCEL;
    public static final String FILE_CREATOR_FILE_NAME_LABEL;

    public static final String MATERIAL_FILE_CREATOR_TITLE;
    public static final String MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL;
    public static final String MATERIAL_FILE_CREATOR_FILE_DESCRIPTION;

    public static final String POST_FILTER_VIEW_FILE_CREATOR_TITLE;
    public static final String POST_FILTER_VIEW_FILE_CREATOR_FILE_DESCRIPTION;

    public static final String GRAPHICS_DIALOG_TITLE;
    public static final String GRAPHICS_DIALOG_FXAA;
    public static final String GRAPHICS_DIALOG_FULLSCREEN;
    public static final String GRAPHICS_DIALOG_SCREEN_SIZE;
    public static final String GRAPHICS_DIALOG_ANISOTROPY;
    public static final String GRAPHICS_DIALOG_BUTTON_OK;
    public static final String GRAPHICS_DIALOG_BUTTON_CANCEL;
    public static final String GRAPHICS_DIALOG_MESSAGE;

    public static final String BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION;

    public static final String MODEL_FILE_EDITOR_NAME;
    public static final String MODEL_FILE_EDITOR_NO_SKY;
    public static final String MODEL_FILE_EDITOR_FAST_SKY;
    public static final String MODEL_FILE_EDITOR_NODE_TREE;
    public static final String MODEL_FILE_EDITOR_PROPERTIES;
    public static final String MODEL_FILE_EDITOR_NODE_MESH;

    public static final String MODEL_NODE_TREE_ACTION_MIKKTSPACE_TANGENT_GENERATOR;
    public static final String MODEL_NODE_TREE_ACTION_REMOVE;
    public static final String MODEL_NODE_TREE_ACTION_RENAME;
    public static final String MODEL_NODE_TREE_ACTION_OPTIMIZE_GEOMETRY;
    public static final String MODEL_NODE_TREE_ACTION_TOOLS;
    public static final String MODEL_NODE_TREE_ACTION_CREATE;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_NODE;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_SKY;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_BOX;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_SPHERE;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_QUAD;
    public static final String MODEL_NODE_TREE_ACTION_LOAD_MODEL;
    public static final String MODEL_NODE_TREE_ACTION_TANGENT_GENERATOR;

    public static final String MODEL_PROPERTY_CULL_HINT;
    public static final String MODEL_PROPERTY_SHADOW_MODE;
    public static final String MODEL_PROPERTY_QUEUE_BUCKET;
    public static final String MODEL_PROPERTY_LOCATION;
    public static final String MODEL_PROPERTY_SCALE;
    public static final String MODEL_PROPERTY_ROTATION;
    public static final String MODEL_PROPERTY_MATERIAL;

    public static final String MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;

    public static final String RENAME_DIALOG_TITLE;
    public static final String RENAME_DIALOG_NEW_NAME_LABEL;
    public static final String RENAME_DIALOG_BUTTON_OK;
    public static final String RENAME_DIALOG_BUTTON_CANCEL;

    public static final String FOLDER_CREATOR_DESCRIPTION;
    public static final String FOLDER_CREATOR_TITLE;
    public static final String FOLDER_CREATOR_FILE_NAME_LABEL;

    public static final String EMPTY_FILE_CREATOR_DESCRIPTION;
    public static final String EMPTY_FILE_CREATOR_TITLE;

    public static final String IMAGE_VIEWER_EDITOR_NAME;

    public static final String CREATE_SKY_DIALOG_TITLE;
    public static final String CREATE_SKY_DIALOG_SKY_TYPE_SINGLE;
    public static final String CREATE_SKY_DIALOG_SKY_TYPE_MULTIPLE;
    public static final String CREATE_SKY_DIALOG_SKY_TYPE_LABEL;
    public static final String CREATE_SKY_DIALOG_NORMAL_SCALE_LABEL;
    public static final String CREATE_SKY_DIALOG_TEXTURE_LABEL;
    public static final String CREATE_SKY_DIALOG_TEXTURE_TYPE_LABEL;
    public static final String CREATE_SKY_DIALOG_FLIP_Y_LABEL;
    public static final String CREATE_SKY_DIALOG_NORTH_LABEL;
    public static final String CREATE_SKY_DIALOG_SOUTH_LABEL;
    public static final String CREATE_SKY_DIALOG_EAST_LABEL;
    public static final String CREATE_SKY_DIALOG_WEST_LABEL;
    public static final String CREATE_SKY_DIALOG_TOP_LABEL;
    public static final String CREATE_SKY_DIALOG_BOTTOM_LABEL;

    public static final String NODE_DIALOG_BUTTON_OK;
    public static final String NODE_DIALOG_BUTTON_CANCEL;

    public static final String EMPTY_MODEL_CREATOR_DESCRIPTION;
    public static final String EMPTY_MODEL_CREATOR_TITLE;

    public static final String GLSL_FILE_EDITOR_NAME;
;

    static {

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, ResourceControl.getInstance());

        EDITOR_BAR_ASSET = bundle.getString("EditorBarComponent.asset");
        EDITOR_BAR_ASSET_OPEN_ASSET = bundle.getString("EditorBarComponent.asset.openAsset");
        EDITOR_BAR_ASSET_OPEN_ASSET_DIRECTORY_CHOOSER = bundle.getString("EditorBarComponent.asset.openAsset.DirectoryChooser");
        EDITOR_BAR_ASSET_REOPEN_ASSET_FOLDER = bundle.getString("EditorBarComponent.asset.reopen");
        EDITOR_BAR_ASSET_CLOSE_EDITOR = bundle.getString("EditorBarComponent.asset.close");

        EDITOR_BAR_SETTINGS = bundle.getString("EditorBarComponent.settings");
        EDITOR_BAR_SETTINGS_GRAPHICS = bundle.getString("EditorBarComponent.settings.graphics");

        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE = bundle.getString("AssetComponentResourceTreeContextMenuNewFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE = bundle.getString("AssetComponentResourceTreeContextMenuOpenFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE = bundle.getString("AssetComponentResourceTreeContextMenuOpenWithFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE = bundle.getString("AssetComponentResourceTreeContextMenuCopyFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE = bundle.getString("AssetComponentResourceTreeContextMenuCutFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE = bundle.getString("AssetComponentResourceTreeContextMenuPasteFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE = bundle.getString("AssetComponentResourceTreeContextMenuDeleteFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CONVERT_FILE = bundle.getString("AssetComponentResourceTreeContextMenuConvertFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_EXTERNAL_EDITOR = bundle.getString("AssetComponentResourceTreeContextMenuOpenFileByExternalEditor");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_RENAME_FILE = bundle.getString("AssetComponentResourceTreeContextMenuRenameFile");

        FILE_EDITOR_ACTION_SAVE = bundle.getString("FileEditorActionSave");

        POST_FILTER_EDITOR_MATERIAL_LABEL = bundle.getString("PostFilterEditorMaterialListLabel");
        ASSET_EDITOR_DIALOG_TITLE = bundle.getString("AssetEditorDialogTitle");
        ASSET_EDITOR_DIALOG_BUTTON_OK = bundle.getString("AssetEditorDialogButtonOk");
        ASSET_EDITOR_DIALOG_BUTTON_CANCEL = bundle.getString("AssetEditorDialogButtonCancel");

        MATERIAL_FILE_EDITOR_TEXTURES_COMPONENT_TITLE = bundle.getString("MaterialFileEditorTexturesComponentTitle");
        MATERIAL_FILE_EDITOR_COLORS_COMPONENT_TITLE = bundle.getString("MaterialFileEditorColorsComponentTitle");
        MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE = bundle.getString("MaterialFileEditorOtherComponentTitle");
        MATERIAL_FILE_EDITOR_RENDER_PARAMS_COMPONENT_TITLE = bundle.getString("MaterialFileEditorRenderParamsComponentTitle");

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

        MATERIAL_EDITOR_MATERIAL_TYPE_LABEL = bundle.getString("MaterialFileEditorMaterialTypeLabel");
        MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL = bundle.getString("MaterialFileEditorBucketTypeLabel");

        TEXT_FILE_EDITOR_NAME = bundle.getString("TextFileEditorName");
        POST_FILTER_EDITOR_NAME = bundle.getString("PostFilterEditorName");
        MATERIAL_EDITOR_NAME = bundle.getString("MaterialFileEditorName");

        FILE_CREATOR_BUTTON_OK = bundle.getString("FileCreatorButtonOk");
        FILE_CREATOR_BUTTON_CANCEL = bundle.getString("FileCreatorButtonCancel");
        FILE_CREATOR_FILE_NAME_LABEL = bundle.getString("FileCreatorFileNameLabel");

        MATERIAL_FILE_CREATOR_TITLE = bundle.getString("MaterialFileCreatorTitle");
        MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL = bundle.getString("MaterialFileCreatorMaterialTypeLabel");
        MATERIAL_FILE_CREATOR_FILE_DESCRIPTION = bundle.getString("MaterialFileCreatorFileDescription");

        POST_FILTER_VIEW_FILE_CREATOR_TITLE = bundle.getString("PostFilterViewFileCreatorTitle");
        POST_FILTER_VIEW_FILE_CREATOR_FILE_DESCRIPTION = bundle.getString("PostFilterViewFileCreatorFileDescription");

        GRAPHICS_DIALOG_TITLE = bundle.getString("GraphicsDialogTitle");
        GRAPHICS_DIALOG_FXAA = bundle.getString("GraphicsDialogFXAA");
        GRAPHICS_DIALOG_FULLSCREEN = bundle.getString("GraphicsDialogFullscreen");
        GRAPHICS_DIALOG_SCREEN_SIZE = bundle.getString("GraphicsDialogScreenSize");
        GRAPHICS_DIALOG_ANISOTROPY = bundle.getString("GraphicsDialogAnisotropy");
        GRAPHICS_DIALOG_BUTTON_OK = bundle.getString("GraphicsDialogButtonOk");
        GRAPHICS_DIALOG_BUTTON_CANCEL = bundle.getString("GraphicsDialogButtonCancel");
        GRAPHICS_DIALOG_MESSAGE = bundle.getString("GraphicsDialogMessage");

        BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("BlendToJ3oFileConverterDescription");

        MODEL_FILE_EDITOR_NAME = bundle.getString("ModelFileEditorName");
        MODEL_FILE_EDITOR_NO_SKY = bundle.getString("ModelFileEditorNoSky");
        MODEL_FILE_EDITOR_FAST_SKY = bundle.getString("ModelFileEditorFastSky");
        MODEL_FILE_EDITOR_NODE_TREE = bundle.getString("ModelFileEditorNodeTree");
        MODEL_FILE_EDITOR_PROPERTIES = bundle.getString("ModelFileEditorProperties");
        MODEL_FILE_EDITOR_NODE_MESH = bundle.getString("ModelFileEditorNodeMesh");

        MODEL_NODE_TREE_ACTION_MIKKTSPACE_TANGENT_GENERATOR = bundle.getString("ModelNodeTreeActionMikktspaceTangentGenerator");
        MODEL_NODE_TREE_ACTION_REMOVE = bundle.getString("ModelNodeTreeActionRemove");
        MODEL_NODE_TREE_ACTION_RENAME = bundle.getString("ModelNodeTreeActionRename");
        MODEL_NODE_TREE_ACTION_OPTIMIZE_GEOMETRY = bundle.getString("ModelNodeTreeActionOptimizeGeometry");
        MODEL_NODE_TREE_ACTION_TOOLS = bundle.getString("ModelNodeTreeActionTools");
        MODEL_NODE_TREE_ACTION_CREATE = bundle.getString("ModelNodeTreeActionCreate");
        MODEL_NODE_TREE_ACTION_CREATE_NODE = bundle.getString("ModelNodeTreeActionCreateNode");
        MODEL_NODE_TREE_ACTION_CREATE_SKY = bundle.getString("ModelNodeTreeActionCreateSky");
        MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE = bundle.getString("ModelNodeTreeActionCreatePrimitive");
        MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_BOX = bundle.getString("ModelNodeTreeActionCreatePrimitiveBox");
        MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_SPHERE = bundle.getString("ModelNodeTreeActionCreatePrimitiveSphere");
        MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_QUAD = bundle.getString("ModelNodeTreeActionCreatePrimitiveQuad");
        MODEL_NODE_TREE_ACTION_LOAD_MODEL = bundle.getString("ModelNodeTreeActionLoadModel");
        MODEL_NODE_TREE_ACTION_TANGENT_GENERATOR = bundle.getString("ModelNodeTreeActionTangentGenerator");

        MODEL_PROPERTY_CULL_HINT = bundle.getString("ModelPropertyCullHint");
        MODEL_PROPERTY_SHADOW_MODE = bundle.getString("ModelPropertyShadowMode");
        MODEL_PROPERTY_QUEUE_BUCKET = bundle.getString("ModelPropertyQueueBucket");
        MODEL_PROPERTY_LOCATION = bundle.getString("ModelPropertyLocation");
        MODEL_PROPERTY_SCALE = bundle.getString("ModelPropertyScale");
        MODEL_PROPERTY_ROTATION = bundle.getString("ModelPropertyRotation");
        MODEL_PROPERTY_MATERIAL = bundle.getString("ModelPropertyMaterial");

        MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL = bundle.getString("MaterialModelPropertyControlNoMaterial");

        RENAME_DIALOG_TITLE = bundle.getString("RenameDialogTitle");
        RENAME_DIALOG_NEW_NAME_LABEL = bundle.getString("RenameDialogNewNameLabel");
        RENAME_DIALOG_BUTTON_OK = bundle.getString("RenameDialogButtonOk");
        RENAME_DIALOG_BUTTON_CANCEL = bundle.getString("RenameDialogButtonCancel");

        FOLDER_CREATOR_DESCRIPTION = bundle.getString("FolderCreatorDescription");
        FOLDER_CREATOR_TITLE = bundle.getString("FolderCreatorTitle");
        FOLDER_CREATOR_FILE_NAME_LABEL = bundle.getString("FolderCreatorFileNameLabel");

        EMPTY_FILE_CREATOR_DESCRIPTION = bundle.getString("EmptyFileCreatorDescription");
        EMPTY_FILE_CREATOR_TITLE = bundle.getString("EmptyFileCreatorTitle");

        IMAGE_VIEWER_EDITOR_NAME = bundle.getString("ImageViewerEditorName");

        CREATE_SKY_DIALOG_TITLE = bundle.getString("CreateSkyDialogTitle");
        CREATE_SKY_DIALOG_SKY_TYPE_SINGLE = bundle.getString("CreateSkyDialogSkyTypeSingle");
        CREATE_SKY_DIALOG_SKY_TYPE_MULTIPLE = bundle.getString("CreateSkyDialogSkyTypeMultiple");
        CREATE_SKY_DIALOG_SKY_TYPE_LABEL = bundle.getString("CreateSkyDialogSkyTypeLabel");
        CREATE_SKY_DIALOG_NORMAL_SCALE_LABEL = bundle.getString("CreateSkyDialogNormalScaleLabel");
        CREATE_SKY_DIALOG_TEXTURE_LABEL = bundle.getString("CreateSkyDialogTextureLabel");
        CREATE_SKY_DIALOG_TEXTURE_TYPE_LABEL = bundle.getString("CreateSkyDialogTextureTypeLabel");
        CREATE_SKY_DIALOG_FLIP_Y_LABEL = bundle.getString("CreateSkyDialogFlipYLabel");
        CREATE_SKY_DIALOG_NORTH_LABEL = bundle.getString("CreateSkyDialogNorthLabel");
        CREATE_SKY_DIALOG_SOUTH_LABEL = bundle.getString("CreateSkyDialogSouthLabel");
        CREATE_SKY_DIALOG_EAST_LABEL = bundle.getString("CreateSkyDialogEastLabel");
        CREATE_SKY_DIALOG_WEST_LABEL = bundle.getString("CreateSkyDialogWestLabel");
        CREATE_SKY_DIALOG_TOP_LABEL = bundle.getString("CreateSkyDialogTopLabel");
        CREATE_SKY_DIALOG_BOTTOM_LABEL = bundle.getString("CreateSkyDialogBottomLabel");

        NODE_DIALOG_BUTTON_OK = bundle.getString("NodeDialogButtonOk");
        NODE_DIALOG_BUTTON_CANCEL = bundle.getString("NodeDialogButtonCancel");

        EMPTY_MODEL_CREATOR_DESCRIPTION = bundle.getString("EmptyModelCreatorDescription");
        EMPTY_MODEL_CREATOR_TITLE = bundle.getString("EmptyModelCreatorTitle");

        GLSL_FILE_EDITOR_NAME = bundle.getString("GLSLFileEditorName");
    }
}