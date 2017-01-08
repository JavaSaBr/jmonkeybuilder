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
    public static final String EDITOR_TOOL_ASSET;

    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE;
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION;
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

    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE;
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH;
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT;
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR;
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION;

    public static final String POST_FILTER_VIEW_FILE_CREATOR_TITLE;
    public static final String POST_FILTER_VIEW_FILE_CREATOR_FILE_DESCRIPTION;

    public static final String SETTINGS_DIALOG_TITLE;
    public static final String SETTINGS_DIALOG_FXAA;
    public static final String SETTINGS_DIALOG_DECORATED;
    public static final String SETTINGS_DIALOG_FRAME_RATE;
    public static final String SETTINGS_DIALOG_GAMMA_CORRECTION;
    public static final String SETTINGS_DIALOG_TONEMAP_FILTER;
    public static final String SETTINGS_DIALOG_TONEMAP_FILTER_WHITE_POINT;
    public static final String SETTINGS_DIALOG_ANISOTROPY;
    public static final String SETTINGS_DIALOG_BUTTON_OK;
    public static final String SETTINGS_DIALOG_BUTTON_CANCEL;
    public static final String SETTINGS_DIALOG_MESSAGE;
    public static final String SETTINGS_DIALOG_GOOGLE_ANALYTICS;

    public static final String OTHER_SETTINGS_DIALOG_TITLE;
    public static final String OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL;
    public static final String OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE;
    public static final String OTHER_SETTINGS_DIALOG_BUTTON_OK;
    public static final String OTHER_SETTINGS_DIALOG_BUTTON_CANCEL;

    public static final String BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    public static final String FBX_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    public static final String OBJ_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    public static final String XBUF_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    public static final String SCENE_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    public static final String MESH_XML_TO_J3O_FILE_CONVERTER_DESCRIPTION;

    public static final String MODEL_FILE_EDITOR_NAME;
    public static final String MODEL_FILE_EDITOR_TOOL_OBJECTS;
    public static final String MODEL_FILE_EDITOR_NO_SKY;
    public static final String MODEL_FILE_EDITOR_FAST_SKY;
    public static final String MODEL_FILE_EDITOR_NODE_MESH;
    public static final String MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_POINT_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_SPOT_LIGHT;
    public static final String MODEL_FILE_EDITOR_NODE_LIGHT_PROBE;
    public static final String MODEL_FILE_EDITOR_NODE_ANIM_CONTROL;
    public static final String MODEL_FILE_EDITOR_NODE_EMITTER_INFLUENCERS;

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
    public static final String MODEL_NODE_TREE_ACTION_LOD_GENERATOR;
    public static final String MODEL_NODE_TREE_ACTION_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_POINT_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_SPOT_LIGHT;
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_PLAY;
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_PLAY_SETTINGS;
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_STOP;
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_MANUAL_EXTRAXT_SUB_ANIMATION;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_AUDIO_NODE;
    public static final String MODEL_NODE_TREE_ACTION_AUDIO_PLAY;
    public static final String MODEL_NODE_TREE_ACTION_AUDIO_STOP;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_TEMITTER;
    public static final String MODEL_NODE_TREE_ACTION_CREATE_SOFT_TEMITTER;
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
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_ALPHA;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_COLOR;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_DESTINATION;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_GRAVITY;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_IMPULSE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_PHYSICS;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_RADIAL_VELOCITY;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_ROTATION;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_SIZE;
    public static final String MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_SPRITE;

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
    public static final String MODEL_PROPERTY_LOOPING;
    public static final String MODEL_PROPERTY_REVERB;
    public static final String MODEL_PROPERTY_DIRECTIONAL;
    public static final String MODEL_PROPERTY_POSITIONAL;
    public static final String MODEL_PROPERTY_AUDIO_PITCH;
    public static final String MODEL_PROPERTY_AUDIO_VOLUME;
    public static final String MODEL_PROPERTY_TIME_OFFSET;
    public static final String MODEL_PROPERTY_MAX_DISTANCE;
    public static final String MODEL_PROPERTY_REF_DISTANCE;
    public static final String MODEL_PROPERTY_AUDIO_DATA;
    public static final String MODEL_PROPERTY_VELOCITY;
    public static final String MODEL_PROPERTY_LOD;
    public static final String MODEL_PROPERTY_TRIANGLE_COUNT;
    public static final String MODEL_PROPERTY_LEVEL;

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
    public static final String PARTICLE_EMITTER_EMITTER_LIFE;
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
    public static final String PARTICLE_EMITTER_INFLUENCER_FIXED_DURATION;
    public static final String PARTICLE_EMITTER_INFLUENCER_RANDOM_START_COLOR;
    public static final String PARTICLE_EMITTER_INFLUENCER_RANDOM_START_SIZE;
    public static final String PARTICLE_EMITTER_INFLUENCER_SIZE_VARIATION_FACTOR;
    public static final String PARTICLE_EMITTER_INFLUENCER_RANDOM_START_DESTINATION;
    public static final String PARTICLE_EMITTER_INFLUENCER_CHANCE;
    public static final String PARTICLE_EMITTER_INFLUENCER_STRENGTH;
    public static final String PARTICLE_EMITTER_INFLUENCER_MAGNITUDE;
    public static final String PARTICLE_EMITTER_INFLUENCER_GRAVITY;
    public static final String PARTICLE_EMITTER_INFLUENCER_ALIGNMENT;
    public static final String PARTICLE_EMITTER_INFLUENCER_RANDOM_DIRECTION;
    public static final String PARTICLE_EMITTER_INFLUENCER_PULL_CENTER;
    public static final String PARTICLE_EMITTER_INFLUENCER_PULL_ALIGNMENT;
    public static final String PARTICLE_EMITTER_INFLUENCER_UP_ALIGNMENT;
    public static final String PARTICLE_EMITTER_INFLUENCER_RADIAL_PULL;
    public static final String PARTICLE_EMITTER_INFLUENCER_TANGENT_FORCE;
    public static final String PARTICLE_EMITTER_INFLUENCER_ALPHA_INTERPOLATION;
    public static final String PARTICLE_EMITTER_INFLUENCER_COLOR_INTERPOLATION;
    public static final String PARTICLE_EMITTER_INFLUENCER_DESTINATION_INTERPOLATION;
    public static final String PARTICLE_EMITTER_INFLUENCER_ROTATION_INTERPOLATION;
    public static final String PARTICLE_EMITTER_INFLUENCER_SIZE_INTERPOLATION;
    public static final String PARTICLE_EMITTER_INFLUENCER_ALPHA;
    public static final String PARTICLE_EMITTER_INFLUENCER_COLOR;
    public static final String PARTICLE_EMITTER_INFLUENCER_SPEED;
    public static final String PARTICLE_EMITTER_INFLUENCER_SIZE;
    public static final String PARTICLE_EMITTER_INFLUENCER_FRAME_SEQUENCE;
    public static final String PARTICLE_EMITTER_INFLUENCER_RANDOM_START_IMAGE;
    public static final String PARTICLE_EMITTER_INFLUENCER_ANIMATE;
    public static final String PARTICLE_EMITTER_INFLUENCER_REACTION;
    public static final String PARTICLE_EMITTER_INFLUENCER_RESTITUTION;
    public static final String PARTICLE_EMITTER_INFLUENCER_RANDOM_SPEED;
    public static final String PARTICLE_EMITTER_INFLUENCER_START_RANDOM_ROTATION_X;
    public static final String PARTICLE_EMITTER_INFLUENCER_INTERPOLATION;

    public static final String MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;

    public static final String RENAME_DIALOG_TITLE;
    public static final String RENAME_DIALOG_NEW_NAME_LABEL;
    public static final String RENAME_DIALOG_BUTTON_OK;
    public static final String RENAME_DIALOG_BUTTON_CANCEL;

    public static final String PLAY_ANIMATION_SETTINGS_DIALOG_TITLE;
    public static final String PLAY_ANIMATION_SETTINGS_DIALOG_LOOP_MODE;
    public static final String PLAY_ANIMATION_SETTINGS_DIALOG_SPEED;
    public static final String PLAY_ANIMATION_SETTINGS_DIALOG_BUTTON_OK;

    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_TITLE;
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_NAME;
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_NAME_EXAMPLE;
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_START_FRAME;
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_END_FRAME;
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_BUTTON_OK;

    public static final String QUESTION_DIALOG_TITLE;
    public static final String QUESTION_DIALOG_BUTTON_OK;
    public static final String QUESTION_DIALOG_BUTTON_CANCEL;

    public static final String FOLDER_CREATOR_DESCRIPTION;
    public static final String FOLDER_CREATOR_TITLE;
    public static final String FOLDER_CREATOR_FILE_NAME_LABEL;

    public static final String EMPTY_FILE_CREATOR_DESCRIPTION;
    public static final String EMPTY_FILE_CREATOR_TITLE;

    public static final String IMAGE_VIEWER_EDITOR_NAME;

    public static final String AUDIO_VIEWER_EDITOR_NAME;
    public static final String AUDIO_VIEWER_EDITOR_DURATION_LABEL;
    public static final String AUDIO_VIEWER_EDITOR_BITS_PER_SAMPLE_LABEL;
    public static final String AUDIO_VIEWER_EDITOR_CHANNELS_LABEL;
    public static final String AUDIO_VIEWER_EDITOR_DATA_TYPE_LABEL;
    public static final String AUDIO_VIEWER_EDITOR_SAMPLE_RATE_LABEL;

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
    public static final String MATERIAL_DEFINITION_FILE_EDITOR_NAME;

    public static final String GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED;
    public static final String GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL;
    public static final String GENERATE_TANGENTS_DIALOG_TITLE;
    public static final String GENERATE_TANGENTS_DIALOG_BUTTON_OK;

    public static final String GENERATE_LOD_DIALOG_TITLE;
    public static final String GENERATE_LOD_DIALOG_METHOD;
    public static final String GENERATE_LOD_DIALOG_BUTTON_GENERATE;

    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME;
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE;
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS;
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX;

    public static final String LOG_VIEW_TITLE;

    static {

        final Locale locale = Locale.getDefault();
        final ClassLoader classLoader = com.sun.javafx.scene.control.skin.resources.ControlResources.class.getClassLoader();

        final ResourceBundle controlBundle = ResourceBundle.getBundle("com/sun/javafx/scene/control/skin/resources/controls", locale, classLoader, ResourceControl.getInstance());
        final ResourceBundle overrideBundle = ResourceBundle.getBundle("com/sun/javafx/scene/control/skin/resources/controls", ResourceControl.getInstance());

        final Map override = ReflectionUtils.getFieldValue(overrideBundle, "lookup");
        final Map original = ReflectionUtils.getFieldValue(controlBundle, "lookup");
        original.putAll(override);

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, ResourceControl.getInstance());

        EDITOR_BAR_ASSET = bundle.getString("EditorBarComponentAsset");
        EDITOR_BAR_ASSET_OPEN_ASSET = bundle.getString("EditorBarComponentAssetOpenAsset");
        EDITOR_BAR_ASSET_OPEN_ASSET_DIRECTORY_CHOOSER = bundle.getString("EditorBarComponentAssetOpenAssetDirectoryChooser");
        EDITOR_BAR_ASSET_REOPEN_ASSET_FOLDER = bundle.getString("EditorBarComponentAssetReopen");
        EDITOR_BAR_ASSET_CLOSE_EDITOR = bundle.getString("EditorBarComponentAssetClose");

        EDITOR_BAR_SETTINGS = bundle.getString("EditorBarComponent.settings");

        EDITOR_TOOL_ASSET = bundle.getString("EditorToolAsset");

        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE = bundle.getString("AssetComponentResourceTreeContextMenuNewFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE = bundle.getString("AssetComponentResourceTreeContextMenuOpenFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE = bundle.getString("AssetComponentResourceTreeContextMenuOpenWithFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE = bundle.getString("AssetComponentResourceTreeContextMenuCopyFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE = bundle.getString("AssetComponentResourceTreeContextMenuCutFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE = bundle.getString("AssetComponentResourceTreeContextMenuPasteFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE = bundle.getString("AssetComponentResourceTreeContextMenuDeleteFile");
        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION = bundle.getString("AssetComponentResourceTreeContextMenuDeleteFileQuestion");
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

        SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE = bundle.getString("SingleColorTextureFileCreatorTitle");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH = bundle.getString("SingleColorTextureFileCreatorWidth");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT = bundle.getString("SingleColorTextureFileCreatorHeight");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR = bundle.getString("SingleColorTextureFileCreatorColor");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION = bundle.getString("SingleColorTextureFileCreatorDescription");

        POST_FILTER_VIEW_FILE_CREATOR_TITLE = bundle.getString("PostFilterViewFileCreatorTitle");
        POST_FILTER_VIEW_FILE_CREATOR_FILE_DESCRIPTION = bundle.getString("PostFilterViewFileCreatorFileDescription");

        SETTINGS_DIALOG_TITLE = bundle.getString("SettingsDialogTitle");
        SETTINGS_DIALOG_FXAA = bundle.getString("SettingsDialogFXAA");
        SETTINGS_DIALOG_DECORATED = bundle.getString("SettingsDialogDecorated");
        SETTINGS_DIALOG_FRAME_RATE = bundle.getString("SettingsDialogFrameRate");
        SETTINGS_DIALOG_GAMMA_CORRECTION = bundle.getString("SettingsDialogGammaCorrection");
        SETTINGS_DIALOG_TONEMAP_FILTER = bundle.getString("SettingsDialogToneMapFilter");
        SETTINGS_DIALOG_TONEMAP_FILTER_WHITE_POINT = bundle.getString("SettingsDialogToneMapFilterWhitePoint");
        SETTINGS_DIALOG_ANISOTROPY = bundle.getString("SettingsDialogAnisotropy");
        SETTINGS_DIALOG_BUTTON_OK = bundle.getString("SettingsDialogButtonOk");
        SETTINGS_DIALOG_BUTTON_CANCEL = bundle.getString("SettingsDialogButtonCancel");
        SETTINGS_DIALOG_MESSAGE = bundle.getString("SettingsDialogMessage");
        SETTINGS_DIALOG_GOOGLE_ANALYTICS = bundle.getString("SettingsDialogAnalytics");

        OTHER_SETTINGS_DIALOG_TITLE = bundle.getString("OtherSettingsDialogTitle");
        OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL = bundle.getString("OtherSettingsDialogClasspathFolderLabel");
        OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE = bundle.getString("OtherSettingsDialogClasspathFolderChooserTitle");
        OTHER_SETTINGS_DIALOG_BUTTON_OK = bundle.getString("OtherSettingsDialogButtonOk");
        OTHER_SETTINGS_DIALOG_BUTTON_CANCEL = bundle.getString("OtherSettingsDialogButtonCancel");

        BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("BlendToJ3oFileConverterDescription");
        FBX_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("FBXToJ3oFileConverterDescription");
        OBJ_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("ObjToJ3oFileConverterDescription");
        SCENE_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("SceneToJ3oFileConverterDescription");
        MESH_XML_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("MeshXmlToJ3oFileConverterDescription");
        XBUF_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("XBufToJ3oFileConverterDescription");

        MODEL_FILE_EDITOR_NAME = bundle.getString("ModelFileEditorName");
        MODEL_FILE_EDITOR_TOOL_OBJECTS = bundle.getString("ModelFileEditorToolObjects");
        MODEL_FILE_EDITOR_NO_SKY = bundle.getString("ModelFileEditorNoSky");
        MODEL_FILE_EDITOR_FAST_SKY = bundle.getString("ModelFileEditorFastSky");
        MODEL_FILE_EDITOR_NODE_MESH = bundle.getString("ModelFileEditorNodeMesh");
        MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT = bundle.getString("ModelFileEditorNodeAmbientLight");
        MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT = bundle.getString("ModelFileEditorNodeDirectionLight");
        MODEL_FILE_EDITOR_NODE_POINT_LIGHT = bundle.getString("ModelFileEditorNodePointLight");
        MODEL_FILE_EDITOR_NODE_SPOT_LIGHT = bundle.getString("ModelFileEditorNodeSpotLight");
        MODEL_FILE_EDITOR_NODE_LIGHT_PROBE = bundle.getString("ModelFileEditorNodeLightProbe");
        MODEL_FILE_EDITOR_NODE_ANIM_CONTROL = bundle.getString("ModelFileEditorNodeAnimControl");
        MODEL_FILE_EDITOR_NODE_EMITTER_INFLUENCERS = bundle.getString("ModelFileEditorNodeEmitterInfluencers");

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
        MODEL_NODE_TREE_ACTION_LOD_GENERATOR = bundle.getString("ModelNodeTreeActionLoDGenerator");
        MODEL_NODE_TREE_ACTION_LIGHT = bundle.getString("ModelNodeTreeActionLight");
        MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT = bundle.getString("ModelNodeTreeActionAmbientLight");
        MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT = bundle.getString("ModelNodeTreeActionDirectionLight");
        MODEL_NODE_TREE_ACTION_POINT_LIGHT = bundle.getString("ModelNodeTreeActionPointLight");
        MODEL_NODE_TREE_ACTION_SPOT_LIGHT = bundle.getString("ModelNodeTreeActionSpotLight");
        MODEL_NODE_TREE_ACTION_ANIMATION_PLAY = bundle.getString("ModelNodeTreeActionAnimationPlay");
        MODEL_NODE_TREE_ACTION_ANIMATION_PLAY_SETTINGS = bundle.getString("ModelNodeTreeActionAnimationPlaySettings");
        MODEL_NODE_TREE_ACTION_ANIMATION_STOP = bundle.getString("ModelNodeTreeActionAnimationStop");
        MODEL_NODE_TREE_ACTION_ANIMATION_MANUAL_EXTRAXT_SUB_ANIMATION = bundle.getString("ModelNodeTreeActionAnimationManualExtractSubAnimation");
        MODEL_NODE_TREE_ACTION_CREATE_AUDIO_NODE = bundle.getString("ModelNodeTreeActionCreateAudioNode");
        MODEL_NODE_TREE_ACTION_AUDIO_PLAY = bundle.getString("ModelNodeTreeActionAudioPlay");
        MODEL_NODE_TREE_ACTION_AUDIO_STOP = bundle.getString("ModelNodeTreeActionAudioStop");
        MODEL_NODE_TREE_ACTION_CREATE_TEMITTER = bundle.getString("ModelNodeTreeActionCreateTEmitter");
        MODEL_NODE_TREE_ACTION_CREATE_SOFT_TEMITTER = bundle.getString("ModelNodeTreeActionCreateSoftTEmitter");
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
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_ALPHA = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerAlpha");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_COLOR = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerColor");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_DESTINATION = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerDestination");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_GRAVITY = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerGravity");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_IMPULSE = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerImpulse");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_PHYSICS = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerPhysics");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_RADIAL_VELOCITY = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerRadialVelocity");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_ROTATION = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerRotation");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_SIZE = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerSize");
        MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_SPRITE = bundle.getString("ModelNodeTreeActionEmitterCreateInfluencerSprite");

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
        MODEL_PROPERTY_LOOPING = bundle.getString("ModelPropertyLooping");
        MODEL_PROPERTY_REVERB = bundle.getString("ModelPropertyReverb");
        MODEL_PROPERTY_DIRECTIONAL = bundle.getString("ModelPropertyDirectional");
        MODEL_PROPERTY_POSITIONAL = bundle.getString("ModelPropertyPositional");
        MODEL_PROPERTY_AUDIO_PITCH = bundle.getString("ModelPropertyAudioPitch");
        MODEL_PROPERTY_AUDIO_VOLUME = bundle.getString("ModelPropertyAudioVolume");
        MODEL_PROPERTY_TIME_OFFSET = bundle.getString("ModelPropertyTimeOffset");
        MODEL_PROPERTY_MAX_DISTANCE = bundle.getString("ModelPropertyMaxDistance");
        MODEL_PROPERTY_REF_DISTANCE = bundle.getString("ModelPropertyRefDistance");
        MODEL_PROPERTY_AUDIO_DATA = bundle.getString("ModelPropertyAudioData");
        MODEL_PROPERTY_VELOCITY = bundle.getString("ModelPropertyVelocity");
        MODEL_PROPERTY_LOD = bundle.getString("ModelPropertyLoD");
        MODEL_PROPERTY_TRIANGLE_COUNT = bundle.getString("ModelPropertyTriangleCount");
        MODEL_PROPERTY_LEVEL = bundle.getString("ModelPropertyLevel");

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
        PARTICLE_EMITTER_EMITTER_LIFE = bundle.getString("ParticleEmitterEmitterLife");
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
        PARTICLE_EMITTER_INFLUENCER_FIXED_DURATION = bundle.getString("ParticleEmitterInfluencerFixedDuration");
        PARTICLE_EMITTER_INFLUENCER_RANDOM_START_COLOR = bundle.getString("ParticleEmitterInfluencerRandomStartColor");
        PARTICLE_EMITTER_INFLUENCER_RANDOM_START_SIZE = bundle.getString("ParticleEmitterInfluencerRandomStartSize");
        PARTICLE_EMITTER_INFLUENCER_SIZE_VARIATION_FACTOR = bundle.getString("ParticleEmitterInfluencerSizeVariationFactor");
        PARTICLE_EMITTER_INFLUENCER_RANDOM_START_DESTINATION = bundle.getString("ParticleEmitterInfluencerRandomStartDestination");
        PARTICLE_EMITTER_INFLUENCER_CHANCE = bundle.getString("ParticleEmitterInfluencerChance");
        PARTICLE_EMITTER_INFLUENCER_STRENGTH = bundle.getString("ParticleEmitterInfluencerStrength");
        PARTICLE_EMITTER_INFLUENCER_MAGNITUDE = bundle.getString("ParticleEmitterInfluencerMagnitude");
        PARTICLE_EMITTER_INFLUENCER_GRAVITY = bundle.getString("ParticleEmitterInfluencerGravity");
        PARTICLE_EMITTER_INFLUENCER_ALIGNMENT = bundle.getString("ParticleEmitterInfluencerAlignment");
        PARTICLE_EMITTER_INFLUENCER_RANDOM_DIRECTION = bundle.getString("ParticleEmitterInfluencerRandomDirection");
        PARTICLE_EMITTER_INFLUENCER_PULL_CENTER = bundle.getString("ParticleEmitterInfluencerPullCenter");
        PARTICLE_EMITTER_INFLUENCER_PULL_ALIGNMENT = bundle.getString("ParticleEmitterInfluencerPullAlignment");
        PARTICLE_EMITTER_INFLUENCER_UP_ALIGNMENT = bundle.getString("ParticleEmitterInfluencerUpAlignment");
        PARTICLE_EMITTER_INFLUENCER_RADIAL_PULL = bundle.getString("ParticleEmitterInfluencerRadialPull");
        PARTICLE_EMITTER_INFLUENCER_TANGENT_FORCE = bundle.getString("ParticleEmitterInfluencerTangetForce");
        PARTICLE_EMITTER_INFLUENCER_ALPHA_INTERPOLATION = bundle.getString("ParticleEmitterInfluencerAlphaInterpolation");
        PARTICLE_EMITTER_INFLUENCER_COLOR_INTERPOLATION = bundle.getString("ParticleEmitterInfluencerColorInterpolation");
        PARTICLE_EMITTER_INFLUENCER_DESTINATION_INTERPOLATION = bundle.getString("ParticleEmitterInfluencerDestinationInterpolation");
        PARTICLE_EMITTER_INFLUENCER_ROTATION_INTERPOLATION = bundle.getString("ParticleEmitterInfluencerRotationInterpolation");
        PARTICLE_EMITTER_INFLUENCER_SIZE_INTERPOLATION = bundle.getString("ParticleEmitterInfluencerSizeInterpolation");
        PARTICLE_EMITTER_INFLUENCER_ALPHA = bundle.getString("ParticleEmitterInfluencerAlpha");
        PARTICLE_EMITTER_INFLUENCER_COLOR = bundle.getString("ParticleEmitterInfluencerColor");
        PARTICLE_EMITTER_INFLUENCER_SPEED = bundle.getString("ParticleEmitterInfluencerSpeed");
        PARTICLE_EMITTER_INFLUENCER_SIZE = bundle.getString("ParticleEmitterInfluencerSize");
        PARTICLE_EMITTER_INFLUENCER_FRAME_SEQUENCE = bundle.getString("ParticleEmitterInfluencerFrameSequence");
        PARTICLE_EMITTER_INFLUENCER_RANDOM_START_IMAGE = bundle.getString("ParticleEmitterInfluencerRandomStartImage");
        PARTICLE_EMITTER_INFLUENCER_ANIMATE = bundle.getString("ParticleEmitterInfluencerAnimate");
        PARTICLE_EMITTER_INFLUENCER_REACTION = bundle.getString("ParticleEmitterInfluencerReaction");
        PARTICLE_EMITTER_INFLUENCER_RESTITUTION = bundle.getString("ParticleEmitterInfluencerRestitution");
        PARTICLE_EMITTER_INFLUENCER_RANDOM_SPEED = bundle.getString("ParticleEmitterInfluencerRandomSpeed");
        PARTICLE_EMITTER_INFLUENCER_START_RANDOM_ROTATION_X = bundle.getString("ParticleEmitterInfluencerStartRandomRotationX");
        PARTICLE_EMITTER_INFLUENCER_INTERPOLATION = bundle.getString("ParticleEmitterInfluencerInterpolation");

        MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL = bundle.getString("MaterialModelPropertyControlNoMaterial");

        RENAME_DIALOG_TITLE = bundle.getString("RenameDialogTitle");
        RENAME_DIALOG_NEW_NAME_LABEL = bundle.getString("RenameDialogNewNameLabel");
        RENAME_DIALOG_BUTTON_OK = bundle.getString("RenameDialogButtonOk");
        RENAME_DIALOG_BUTTON_CANCEL = bundle.getString("RenameDialogButtonCancel");

        PLAY_ANIMATION_SETTINGS_DIALOG_TITLE = bundle.getString("PlayAnimationSettingsDialogTitle");
        PLAY_ANIMATION_SETTINGS_DIALOG_LOOP_MODE = bundle.getString("PlayAnimationSettingsDialogLoopMode");
        PLAY_ANIMATION_SETTINGS_DIALOG_SPEED = bundle.getString("PlayAnimationSettingsDialogSpeed");
        PLAY_ANIMATION_SETTINGS_DIALOG_BUTTON_OK = bundle.getString("PlayAnimationSettingsDialogButtonOk");

        MANUAL_EXTRACT_ANIMATION_DIALOG_TITLE = bundle.getString("ManualExtractAnimationDialogTitle");
        MANUAL_EXTRACT_ANIMATION_DIALOG_NAME = bundle.getString("ManualExtractAnimationDialogName");
        MANUAL_EXTRACT_ANIMATION_DIALOG_NAME_EXAMPLE = bundle.getString("ManualExtractAnimationDialogNameExample");
        MANUAL_EXTRACT_ANIMATION_DIALOG_START_FRAME = bundle.getString("ManualExtractAnimationDialogStartFrame");
        MANUAL_EXTRACT_ANIMATION_DIALOG_END_FRAME = bundle.getString("ManualExtractAnimationDialogEndFrame");
        MANUAL_EXTRACT_ANIMATION_DIALOG_BUTTON_OK = bundle.getString("ManualExtractAnimationDialogButtonOk");

        QUESTION_DIALOG_TITLE = bundle.getString("QuestionDialogTitle");
        QUESTION_DIALOG_BUTTON_OK = bundle.getString("QuestionDialogButtonOk");
        QUESTION_DIALOG_BUTTON_CANCEL = bundle.getString("QuestionDialogButtonCancel");

        FOLDER_CREATOR_DESCRIPTION = bundle.getString("FolderCreatorDescription");
        FOLDER_CREATOR_TITLE = bundle.getString("FolderCreatorTitle");
        FOLDER_CREATOR_FILE_NAME_LABEL = bundle.getString("FolderCreatorFileNameLabel");

        EMPTY_FILE_CREATOR_DESCRIPTION = bundle.getString("EmptyFileCreatorDescription");
        EMPTY_FILE_CREATOR_TITLE = bundle.getString("EmptyFileCreatorTitle");

        IMAGE_VIEWER_EDITOR_NAME = bundle.getString("ImageViewerEditorName");

        AUDIO_VIEWER_EDITOR_NAME = bundle.getString("AudioViewerEditorName");
        AUDIO_VIEWER_EDITOR_DURATION_LABEL = bundle.getString("AudioViewerEditorDurationLabel");
        AUDIO_VIEWER_EDITOR_BITS_PER_SAMPLE_LABEL = bundle.getString("AudioViewerEditorBitsPerSampleLabel");
        AUDIO_VIEWER_EDITOR_CHANNELS_LABEL = bundle.getString("AudioViewerEditorChannelsLabel");
        AUDIO_VIEWER_EDITOR_DATA_TYPE_LABEL = bundle.getString("AudioViewerEditorDataTypeLabel");
        AUDIO_VIEWER_EDITOR_SAMPLE_RATE_LABEL = bundle.getString("AudioViewerEditorSampleRateLabel");

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
        MATERIAL_DEFINITION_FILE_EDITOR_NAME = bundle.getString("MaterialDefinitionFileEditorName");

        GENERATE_TANGENTS_DIALOG_TITLE = bundle.getString("GenerateTangentsDialogTitle");
        GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL = bundle.getString("GenerateTangentsDialogAlgorithmLabel");
        GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED = bundle.getString("GenerateTangentsDialogSplitMirrored");
        GENERATE_TANGENTS_DIALOG_BUTTON_OK = bundle.getString("GenerateTangentsDialogButtonOk");

        GENERATE_LOD_DIALOG_TITLE = bundle.getString("GenerateLoDDialogTitle");
        GENERATE_LOD_DIALOG_METHOD = bundle.getString("GenerateLoDDialogMethod");
        GENERATE_LOD_DIALOG_BUTTON_GENERATE = bundle.getString("GenerateLoDDialogButtonGenerate");

        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME = bundle.getString("BoundingVolumeModelPropertyControlName");
        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE = bundle.getString("BoundingVolumeModelPropertyControlSphere");
        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS = bundle.getString("BoundingVolumeModelPropertyControlSphereRadius");
        BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX = bundle.getString("BoundingVolumeModelPropertyControlBox");

        LOG_VIEW_TITLE = bundle.getString("LogViewTitle");
    }
}