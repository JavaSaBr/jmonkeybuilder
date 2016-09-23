package com.ss.editor;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import rlib.util.ReflectionUtils;

/**
 * The class with all messages of this application.
 *
 * @author JavaSaBr
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
    public static final String EDITOR_BAR_SETTINGS_OTHER;

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

    public static final String PARTICLE_ASSET_EDITOR_DIALOG_TEXTURE_PARAM_LABEL;
    public static final String PARTICLE_ASSET_EDITOR_DIALOG_LIGHTING_TRANSFORM_LABEL;

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
    public static final String GRAPHICS_DIALOG_GAMMA_CORRECTION;
    public static final String GRAPHICS_DIALOG_TONEMAP_FILTER;
    public static final String GRAPHICS_DIALOG_TONEMAP_FILTER_WHITE_POINT;
    public static final String GRAPHICS_DIALOG_SCREEN_SIZE;
    public static final String GRAPHICS_DIALOG_ANISOTROPY;
    public static final String GRAPHICS_DIALOG_BUTTON_OK;
    public static final String GRAPHICS_DIALOG_BUTTON_CANCEL;
    public static final String GRAPHICS_DIALOG_MESSAGE;

    public static final String OTHER_SETTINGS_DIALOG_TITLE;
    public static final String OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL;
    public static final String OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE;
    public static final String OTHER_SETTINGS_DIALOG_BUTTON_OK;
    public static final String OTHER_SETTINGS_DIALOG_BUTTON_CANCEL;

    public static final String BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION;

    public static final String MODEL_FILE_EDITOR_NAME;
    public static final String MODEL_FILE_EDITOR_NO_SKY;
    public static final String MODEL_FILE_EDITOR_FAST_SKY;
    public static final String MODEL_FILE_EDITOR_NODE_TREE;
    public static final String MODEL_FILE_EDITOR_PROPERTIES;
    public static final String MODEL_FILE_EDITOR_NODE_MESH;
    public static final String MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_POINT_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_SPOT_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_LIGHT_PROBE;
    public static final String MODEL_FILE_EDITOR_NODE_ANIM_CONTROL;

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
    public static final String MODEL_NODE_TREE_ACTION_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_POINT_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_SPOT_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_PLAY;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_TEMITTER;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_TRIANGLE_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PRIMITIVE_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_BOX_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_CYLINDER_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_DOME_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_QUAD_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SPHERE_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_TORUS_SHAPE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_MODEL_SHAPE;

    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_QUAD;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_POINT;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_IMPOSTOR;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_MODEL;

    public static final String MODEL_PROPERTY_CULL_HINT;
    public static final String MODEL_PROPERTY_SHADOW_MODE;
    public static final String MODEL_PROPERTY_QUEUE_BUCKET;
    public static final String MODEL_PROPERTY_LOCATION;
    public static final String MODEL_PROPERTY_SCALE;
    public static final String MODEL_PROPERTY_ROTATION;
    public static final String MODEL_PROPERTY_MATERIAL;
    public static final String MODEL_PROPERTY_DIRECTION;
    public static final String MODEL_PROPERTY_RADIUS;
    public static final String MODEL_PROPERTY_COLOR;
    public static final String MODEL_PROPERTY_INNER_ANGLE;
    public static final String MODEL_PROPERTY_OUTER_ANGLE;
    public static final String MODEL_PROPERTY_MIN;
    public static final String MODEL_PROPERTY_MAX;

    public static final String PARTICLE_EMITTER_TEST_MODE;
    public static final String PARTICLE_EMITTER_ENABLED;
    public static final String PARTICLE_EMITTER_RANDOM_POINT;
    public static final String PARTICLE_EMITTER_SEQUENTIAL_FACE;
    public static final String PARTICLE_EMITTER_SKIP_PATTERN;
    public static final String PARTICLE_EMITTER_DIRECTION_TYPE;
    public static final String PARTICLE_EMITTER_EMISSION_POINT;
    public static final String PARTICLE_EMITTER_MAX_PARTICLES;
    public static final String PARTICLE_EMITTER_EMISSION_PER_SECOND;
    public static final String PARTICLE_EMITTER_PARTICLES_PER_SECOND;
    public static final String PARTICLE_EMITTER_TEST_PARTICLES;
    public static final String PARTICLE_EMITTER_FOLLOW_EMITTER;
    public static final String PARTICLE_EMITTER_STRETCHING;
    public static final String PARTICLE_EMITTER_MAGNITUDE;
    public static final String PARTICLE_EMITTER_BILLBOARD;
    public static final String PARTICLE_EMITTER_INITIAL_FORCE;
    public static final String PARTICLE_EMITTER_LIFE;
    public static final String PARTICLE_EMITTER_COLUMNS;
    public static final String PARTICLE_EMITTER_ROWS;
    public static final String PARTICLE_EMITTER_SPRITE_COUNT;

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

    public static final String GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED;
    public static final String GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL;
    public static final String GENERATE_TANGENTS_DIALOG_TITLE;
    public static final String GENERATE_TANGENTS_DIALOG_BUTTON_OK;
    public static final String GENERATE_TANGENTS_DIALOG_BUTTON_CANCEL;

    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME;
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE;
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS;
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX;

    static {

        final Locale locale = Locale.getDefault();
        final ClassLoader classLoader = com.sun.javafx.scene.control.skin.resources.ControlResources.class.getClassLoader();

        final ResourceBundle controlBundle = ResourceBundle.getBundle("com/sun/javafx/scene/control/skin/resources/controls", locale, classLoader, ResourceControl.getInstance());
        final ResourceBundle overrideBundle = ResourceBundle.getBundle("com/sun/javafx/scene/control/skin/resources/controls", ResourceControl.getInstance());

        final Map override = ReflectionUtils.getFieldValue(overrideBundle, "lookup");
        final Map original = ReflectionUtils.getFieldValue(controlBundle, "lookup");
        original.putAll(override);

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, ResourceControl.getInstance());

        EDITOR_BAR_ASSET = bundle.getString("EditorBarComponent.asset");
        EDITOR_BAR_ASSET_OPEN_ASSET = bundle.getString("EditorBarComponent.asset.openAsset");
        EDITOR_BAR_ASSET_OPEN_ASSET_DIRECTORY_CHOOSER = bundle.getString("EditorBarComponent.asset.openAsset.DirectoryChooser");
        EDITOR_BAR_ASSET_REOPEN_ASSET_FOLDER = bundle.getString("EditorBarComponent.asset.reopen");
        EDITOR_BAR_ASSET_CLOSE_EDITOR = bundle.getString("EditorBarComponent.asset.close");

        EDITOR_BAR_SETTINGS = bundle.getString("EditorBarComponent.settings");
        EDITOR_BAR_SETTINGS_GRAPHICS = bundle.getString("EditorBarComponent.settings.graphics");
        EDITOR_BAR_SETTINGS_OTHER = bundle.getString("EditorBarComponent.settings.other");

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

        PARTICLE_ASSET_EDITOR_DIALOG_TEXTURE_PARAM_LABEL = bundle.getString("ParticlesAssetEditorDialogTextureParamLabel");
        PARTICLE_ASSET_EDITOR_DIALOG_LIGHTING_TRANSFORM_LABEL = bundle.getString("ParticlesAssetEditorDialogTextureLightingTransformLabel");

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
        GRAPHICS_DIALOG_GAMMA_CORRECTION = bundle.getString("GraphicsDialogGammaCorrection");
        GRAPHICS_DIALOG_TONEMAP_FILTER = bundle.getString("GraphicsDialogToneMapFilter");
        GRAPHICS_DIALOG_TONEMAP_FILTER_WHITE_POINT = bundle.getString("GraphicsDialogToneMapFilterWhitePoint");
        GRAPHICS_DIALOG_SCREEN_SIZE = bundle.getString("GraphicsDialogScreenSize");
        GRAPHICS_DIALOG_ANISOTROPY = bundle.getString("GraphicsDialogAnisotropy");
        GRAPHICS_DIALOG_BUTTON_OK = bundle.getString("GraphicsDialogButtonOk");
        GRAPHICS_DIALOG_BUTTON_CANCEL = bundle.getString("GraphicsDialogButtonCancel");
        GRAPHICS_DIALOG_MESSAGE = bundle.getString("GraphicsDialogMessage");

        OTHER_SETTINGS_DIALOG_TITLE = bundle.getString("OtherSettingsDialogTitle");
        OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL = bundle.getString("OtherSettingsDialogClasspathFolderLabel");
        OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE = bundle.getString("OtherSettingsDialogClasspathFolderChooserTitle");
        OTHER_SETTINGS_DIALOG_BUTTON_OK = bundle.getString("OtherSettingsDialogButtonOk");
        OTHER_SETTINGS_DIALOG_BUTTON_CANCEL = bundle.getString("OtherSettingsDialogButtonCancel");

        BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("BlendToJ3oFileConverterDescription");

        MODEL_FILE_EDITOR_NAME = bundle.getString("ModelFileEditorName");
        MODEL_FILE_EDITOR_NO_SKY = bundle.getString("ModelFileEditorNoSky");
        MODEL_FILE_EDITOR_FAST_SKY = bundle.getString("ModelFileEditorFastSky");
        MODEL_FILE_EDITOR_NODE_TREE = bundle.getString("ModelFileEditorNodeTree");
        MODEL_FILE_EDITOR_PROPERTIES = bundle.getString("ModelFileEditorProperties");
        MODEL_FILE_EDITOR_NODE_MESH = bundle.getString("ModelFileEditorNodeMesh");
        MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT = bundle.getString("ModelFileEditorNodeAmbientLight");
        MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT = bundle.getString("ModelFileEditorNodeDirectionLight");
        MODEL_FILE_EDITOR_NODE_POINT_LIGHT = bundle.getString("ModelFileEditorNodePointLight");
        MODEL_FILE_EDITOR_NODE_SPOT_LIGHT = bundle.getString("ModelFileEditorNodeSpotLight");
        MODEL_FILE_EDITOR_NODE_LIGHT_PROBE = bundle.getString("ModelFileEditorNodeLightProbe");
        MODEL_FILE_EDITOR_NODE_ANIM_CONTROL = bundle.getString("ModelFileEditorNodeAnimControl");

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
        MODEL_NODE_TREE_ACTION_LIGHT = bundle.getString("ModelNodeTreeActionLight");
        MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT = bundle.getString("ModelNodeTreeActionAmbientLight");
        MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT = bundle.getString("ModelNodeTreeActionDirectionLight");
        MODEL_NODE_TREE_ACTION_POINT_LIGHT = bundle.getString("ModelNodeTreeActionPointLight");
        MODEL_NODE_TREE_ACTION_SPOT_LIGHT = bundle.getString("ModelNodeTreeActionSpotLight");
        MODEL_NODE_TREE_ACTION_ANIMATION_PLAY = bundle.getString("ModelNodeTreeActionAnimationPlay");
        MODEL_NODE_TREE_ACTION_CREATE_TEMITTER = bundle.getString("ModelNodeTreeActionCreateTEmitter");

        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_TRIANGLE_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeTriangleShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PRIMITIVE_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangePrimitiveShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_BOX_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeBoxShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_CYLINDER_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeCylinderShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_DOME_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeDomeShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_QUAD_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeQuadShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SPHERE_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeSphereShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_TORUS_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeTorusShape");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_MODEL_SHAPE = bundle.getString("ModelNodeTreeActionEmitterChangeModelShape");

        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH = bundle.getString("ModelNodeTreeActionEmitterChangeParticlesMesh");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_QUAD = bundle.getString("ModelNodeTreeActionEmitterChangeParticlesMeshQuad");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_POINT = bundle.getString("ModelNodeTreeActionEmitterChangeParticlesMeshPoint");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_IMPOSTOR = bundle.getString("ModelNodeTreeActionEmitterChangeParticlesMeshImpostor");
        MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_MODEL = bundle.getString("ModelNodeTreeActionEmitterChangeParticlesMeshModel");

        MODEL_PROPERTY_CULL_HINT = bundle.getString("ModelPropertyCullHint");
        MODEL_PROPERTY_SHADOW_MODE = bundle.getString("ModelPropertyShadowMode");
        MODEL_PROPERTY_QUEUE_BUCKET = bundle.getString("ModelPropertyQueueBucket");
        MODEL_PROPERTY_LOCATION = bundle.getString("ModelPropertyLocation");
        MODEL_PROPERTY_SCALE = bundle.getString("ModelPropertyScale");
        MODEL_PROPERTY_ROTATION = bundle.getString("ModelPropertyRotation");
        MODEL_PROPERTY_MATERIAL = bundle.getString("ModelPropertyMaterial");
        MODEL_PROPERTY_DIRECTION = bundle.getString("ModelPropertyDirection");
        MODEL_PROPERTY_RADIUS = bundle.getString("ModelPropertyRadius");
        MODEL_PROPERTY_COLOR = bundle.getString("ModelPropertyColor");
        MODEL_PROPERTY_INNER_ANGLE = bundle.getString("ModelPropertyInnerAngle");
        MODEL_PROPERTY_OUTER_ANGLE = bundle.getString("ModelPropertyOuterAngle");
        MODEL_PROPERTY_MIN = bundle.getString("ModelPropertyMin");
        MODEL_PROPERTY_MAX = bundle.getString("ModelPropertyMax");

        PARTICLE_EMITTER_TEST_MODE = bundle.getString("ParticleEmitterTestMode");
        PARTICLE_EMITTER_ENABLED = bundle.getString("ParticleEmitterEnabled");
        PARTICLE_EMITTER_RANDOM_POINT = bundle.getString("ParticleEmitterRandomPoint");
        PARTICLE_EMITTER_SEQUENTIAL_FACE = bundle.getString("ParticleEmitterSequentialFace");
        PARTICLE_EMITTER_SKIP_PATTERN = bundle.getString("ParticleEmitterSkipPattern");
        PARTICLE_EMITTER_DIRECTION_TYPE = bundle.getString("ParticleEmitterDirectionType");
        PARTICLE_EMITTER_EMISSION_POINT = bundle.getString("ParticleEmitterEmissionPoint");
        PARTICLE_EMITTER_MAX_PARTICLES = bundle.getString("ParticleEmitterMaxParticles");
        PARTICLE_EMITTER_EMISSION_PER_SECOND = bundle.getString("ParticleEmitterEmissionPerSecond");
        PARTICLE_EMITTER_PARTICLES_PER_SECOND = bundle.getString("ParticleEmitterParticlesPerSecond");
        PARTICLE_EMITTER_TEST_PARTICLES = bundle.getString("ParticleEmitterTestParticles");
        PARTICLE_EMITTER_FOLLOW_EMITTER = bundle.getString("ParticleEmitterFollowEmitter");
        PARTICLE_EMITTER_STRETCHING = bundle.getString("ParticleEmitterStretching");
        PARTICLE_EMITTER_MAGNITUDE = bundle.getString("ParticleEmitterMagnitude");
        PARTICLE_EMITTER_BILLBOARD = bundle.getString("ParticleEmitterBillboard");
        PARTICLE_EMITTER_INITIAL_FORCE = bundle.getString("ParticleEmitterInitialForce");
        PARTICLE_EMITTER_LIFE = bundle.getString("ParticleEmitterLife");
        PARTICLE_EMITTER_COLUMNS = bundle.getString("ParticleEmitterColumns");
        PARTICLE_EMITTER_ROWS = bundle.getString("ParticleEmitterRows");
        PARTICLE_EMITTER_SPRITE_COUNT = bundle.getString("ParticleEmitterSpriteCount");

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

        GENERATE_TANGENTS_DIALOG_TITLE = bundle.getString("GenerateTangentsDialogTitle");
        GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL = bundle.getString("GenerateTangentsDialogAlgorithmLabel");
        GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED = bundle.getString("GenerateTangentsDialogSplitMirrored");
        GENERATE_TANGENTS_DIALOG_BUTTON_OK = bundle.getString("GenerateTangentsDialogButtonOk");
        GENERATE_TANGENTS_DIALOG_BUTTON_CANCEL = bundle.getString("GenerateTangentsDialogButtonCancel");

        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME = bundle.getString("BoundingVolumeModelPropertyControlName");
        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE = bundle.getString("BoundingVolumeModelPropertyControlSphere");
        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS = bundle.getString("BoundingVolumeModelPropertyControlSphereRadius");
        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX = bundle.getString("BoundingVolumeModelPropertyControlBox");
    }
}