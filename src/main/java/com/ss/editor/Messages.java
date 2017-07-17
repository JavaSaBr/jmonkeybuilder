package com.ss.editor;

import static java.util.ResourceBundle.getBundle;
import static com.ss.rlib.util.ReflectionUtils.getUnsafeFieldValue;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import com.ss.rlib.util.PropertyLoader;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Messages {

    /**
     * The constant BUNDLE_NAME.
     */
    public static final String BUNDLE_NAME = "messages/messages";

    /**
     * The constant EDITOR_MENU_FILE.
     */
    public static final String EDITOR_MENU_FILE;
    /**
     * The constant EDITOR_MENU_FILE_EXIT.
     */
    public static final String EDITOR_MENU_FILE_EXIT;
    /**
     * The constant EDITOR_MENU_FILE_OPEN_ASSET.
     */
    public static final String EDITOR_MENU_FILE_OPEN_ASSET;
    /**
     * The constant EDITOR_MENU_FILE_OPEN_ASSET_DIRECTORY_CHOOSER.
     */
    public static final String EDITOR_MENU_FILE_OPEN_ASSET_DIRECTORY_CHOOSER;
    /**
     * The constant EDITOR_MENU_FILE_REOPEN_ASSET_FOLDER.
     */
    public static final String EDITOR_MENU_FILE_REOPEN_ASSET_FOLDER;
    /**
     * The constant EDITOR_MENU_OTHER.
     */
    public static final String EDITOR_MENU_OTHER;
    /**
     * The constant EDITOR_MENU_OTHER_SETTINGS.
     */
    public static final String EDITOR_MENU_OTHER_SETTINGS;
    /**
     * The constant EDITOR_MENU_HELP.
     */
    public static final String EDITOR_MENU_HELP;
    /**
     * The constant EDITOR_MENU_HELP_ABOUT.
     */
    public static final String EDITOR_MENU_HELP_ABOUT;

    /**
     * The constant EDITOR_TOOL_ASSET.
     */
    public static final String EDITOR_TOOL_ASSET;

    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_NEW_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_WITH_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CONVERT_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CONVERT_FILE;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_EXTERNAL_EDITOR.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_EXTERNAL_EDITOR;
    /**
     * The constant ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_RENAME_FILE.
     */
    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_RENAME_FILE;

    /**
     * The constant FILE_EDITOR_ACTION_SAVE.
     */
    public static final String FILE_EDITOR_ACTION_SAVE;

    /**
     * The constant SCENE_FILE_EDITOR_ACTION_SELECTION.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_SELECTION;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_GRID.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_GRID;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_STATISTICS.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_STATISTICS;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_MOVE_TOOL.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_MOVE_TOOL;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_SCALE_TOOL.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_SCALE_TOOL;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_ROTATION_TOOL.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_ROTATION_TOOL;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_CAMERA_LIGHT.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_CAMERA_LIGHT;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_SHOW_LIGHTS.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_SHOW_LIGHTS;
    /**
     * The constant SCENE_FILE_EDITOR_ACTION_SHOW_AUDIO.
     */
    public static final String SCENE_FILE_EDITOR_ACTION_SHOW_AUDIO;

    /**
     * The constant MATERIAL_FILE_EDITOR_ACTION_CUBE.
     */
    public static final String MATERIAL_FILE_EDITOR_ACTION_CUBE;
    /**
     * The constant MATERIAL_FILE_EDITOR_ACTION_SPHERE.
     */
    public static final String MATERIAL_FILE_EDITOR_ACTION_SPHERE;
    /**
     * The constant MATERIAL_FILE_EDITOR_ACTION_PLANE.
     */
    public static final String MATERIAL_FILE_EDITOR_ACTION_PLANE;
    /**
     * The constant MATERIAL_FILE_EDITOR_ACTION_LIGHT.
     */
    public static final String MATERIAL_FILE_EDITOR_ACTION_LIGHT;

    /**
     * The constant ASSET_EDITOR_DIALOG_TITLE.
     */
    public static final String ASSET_EDITOR_DIALOG_TITLE;
    /**
     * The constant ASSET_EDITOR_DIALOG_BUTTON_OK.
     */
    public static final String ASSET_EDITOR_DIALOG_BUTTON_OK;
    /**
     * The constant ASSET_EDITOR_DIALOG_BUTTON_CANCEL.
     */
    public static final String ASSET_EDITOR_DIALOG_BUTTON_CANCEL;

    /**
     * The constant PARTICLE_ASSET_EDITOR_DIALOG_TEXTURE_PARAM_LABEL.
     */
    public static final String PARTICLE_ASSET_EDITOR_DIALOG_TEXTURE_PARAM_LABEL;
    /**
     * The constant PARTICLE_ASSET_EDITOR_DIALOG_LIGHTING_TRANSFORM_LABEL.
     */
    public static final String PARTICLE_ASSET_EDITOR_DIALOG_LIGHTING_TRANSFORM_LABEL;

    /**
     * The constant MATERIAL_EDITOR_MATERIAL_TYPE_LABEL.
     */
    public static final String MATERIAL_EDITOR_MATERIAL_TYPE_LABEL;
    /**
     * The constant MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL.
     */
    public static final String MATERIAL_FILE_EDITOR_BUCKET_TYPE_LABEL;

    /**
     * The constant MATERIAL_FILE_EDITOR_TEXTURES_COMPONENT_TITLE.
     */
    public static final String MATERIAL_FILE_EDITOR_TEXTURES_COMPONENT_TITLE;
    /**
     * The constant MATERIAL_FILE_EDITOR_COLORS_COMPONENT_TITLE.
     */
    public static final String MATERIAL_FILE_EDITOR_COLORS_COMPONENT_TITLE;
    /**
     * The constant MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE.
     */
    public static final String MATERIAL_FILE_EDITOR_OTHER_COMPONENT_TITLE;
    /**
     * The constant MATERIAL_FILE_EDITOR_RENDER_PARAMS_COMPONENT_TITLE.
     */
    public static final String MATERIAL_FILE_EDITOR_RENDER_PARAMS_COMPONENT_TITLE;

    /**
     * The constant TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT.
     */
    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT;
    /**
     * The constant TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP.
     */
    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP;
    /**
     * The constant TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD.
     */
    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD;
    /**
     * The constant TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE.
     */
    public static final String TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE;

    /**
     * The constant COLOR_MATERIAL_PARAM_CONTROL_REMOVE.
     */
    public static final String COLOR_MATERIAL_PARAM_CONTROL_REMOVE;

    /**
     * The constant MATERIAL_RENDER_STATE_FACE_CULL_MODE.
     */
    public static final String MATERIAL_RENDER_STATE_FACE_CULL_MODE;
    /**
     * The constant MATERIAL_RENDER_STATE_BLEND_MODE.
     */
    public static final String MATERIAL_RENDER_STATE_BLEND_MODE;
    /**
     * The constant MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR.
     */
    public static final String MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR;
    /**
     * The constant MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS.
     */
    public static final String MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS;
    /**
     * The constant MATERIAL_RENDER_STATE_POINT_SPRITE.
     */
    public static final String MATERIAL_RENDER_STATE_POINT_SPRITE;
    /**
     * The constant MATERIAL_RENDER_STATE_DEPTH_WRITE.
     */
    public static final String MATERIAL_RENDER_STATE_DEPTH_WRITE;
    /**
     * The constant MATERIAL_RENDER_STATE_COLOR_WRITE.
     */
    public static final String MATERIAL_RENDER_STATE_COLOR_WRITE;
    /**
     * The constant MATERIAL_RENDER_STATE_DEPTH_TEST.
     */
    public static final String MATERIAL_RENDER_STATE_DEPTH_TEST;
    /**
     * The constant MATERIAL_RENDER_STATE_WIREFRAME.
     */
    public static final String MATERIAL_RENDER_STATE_WIREFRAME;

    /**
     * The constant TEXT_FILE_EDITOR_NAME.
     */
    public static final String TEXT_FILE_EDITOR_NAME;
    /**
     * The constant MATERIAL_EDITOR_NAME.
     */
    public static final String MATERIAL_EDITOR_NAME;

    /**
     * The constant FILE_CREATOR_BUTTON_OK.
     */
    public static final String FILE_CREATOR_BUTTON_OK;
    /**
     * The constant FILE_CREATOR_FILE_NAME_LABEL.
     */
    public static final String FILE_CREATOR_FILE_NAME_LABEL;

    /**
     * The constant MATERIAL_FILE_CREATOR_TITLE.
     */
    public static final String MATERIAL_FILE_CREATOR_TITLE;
    /**
     * The constant MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL.
     */
    public static final String MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL;
    /**
     * The constant MATERIAL_FILE_CREATOR_FILE_DESCRIPTION.
     */
    public static final String MATERIAL_FILE_CREATOR_FILE_DESCRIPTION;
    /**
     * The constant MATERIAL_DEFINITION_FILE_CREATOR_FILE_DESCRIPTION.
     */
    public static final String MATERIAL_DEFINITION_FILE_CREATOR_FILE_DESCRIPTION;
    /**
     * The constant MATERIAL_DEFINITION_FILE_CREATOR_TITLE.
     */
    public static final String MATERIAL_DEFINITION_FILE_CREATOR_TITLE;
    /**
     * The constant MATERIAL_DEFINITION_FILE_CREATOR_GLSL_LABEL.
     */
    public static final String MATERIAL_DEFINITION_FILE_CREATOR_GLSL_LABEL;

    /**
     * The constant SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE.
     */
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE;
    /**
     * The constant SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH.
     */
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH;
    /**
     * The constant SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT.
     */
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT;
    /**
     * The constant SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR.
     */
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR;
    /**
     * The constant SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION.
     */
    public static final String SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION;

    /**
     * The constant SETTINGS_DIALOG_TITLE.
     */
    public static final String SETTINGS_DIALOG_TITLE;
    /**
     * The constant SETTINGS_DIALOG_FXAA.
     */
    public static final String SETTINGS_DIALOG_FXAA;
    /**
     * The constant SETTINGS_DIALOG_STOP_RENDER_ON_LOST_FOCUS.
     */
    public static final String SETTINGS_DIALOG_STOP_RENDER_ON_LOST_FOCUS;
    /**
     * The constant SETTINGS_DIALOG_FRAME_RATE.
     */
    public static final String SETTINGS_DIALOG_FRAME_RATE;
    /**
     * The constant SETTINGS_DIALOG_GAMMA_CORRECTION.
     */
    public static final String SETTINGS_DIALOG_GAMMA_CORRECTION;
    /**
     * The constant SETTINGS_DIALOG_TONEMAP_FILTER.
     */
    public static final String SETTINGS_DIALOG_TONEMAP_FILTER;
    /**
     * The constant SETTINGS_DIALOG_TONEMAP_FILTER_WHITE_POINT.
     */
    public static final String SETTINGS_DIALOG_TONEMAP_FILTER_WHITE_POINT;
    /**
     * The constant SETTINGS_DIALOG_ANISOTROPY.
     */
    public static final String SETTINGS_DIALOG_ANISOTROPY;
    /**
     * The constant SETTINGS_DIALOG_BUTTON_OK.
     */
    public static final String SETTINGS_DIALOG_BUTTON_OK;
    /**
     * The constant SETTINGS_DIALOG_BUTTON_CANCEL.
     */
    public static final String SETTINGS_DIALOG_BUTTON_CANCEL;
    /**
     * The constant SETTINGS_DIALOG_MESSAGE.
     */
    public static final String SETTINGS_DIALOG_MESSAGE;
    /**
     * The constant SETTINGS_DIALOG_GOOGLE_ANALYTICS.
     */
    public static final String SETTINGS_DIALOG_GOOGLE_ANALYTICS;
    /**
     * The constant SETTINGS_DIALOG_CAMERA_ANGLE.
     */
    public static final String SETTINGS_DIALOG_CAMERA_ANGLE;
    /**
     * The constant SETTINGS_DIALOG_AUTO_TANGENT_GENERATING.
     */
    public static final String SETTINGS_DIALOG_AUTO_TANGENT_GENERATING;
    /**
     * The constant SETTINGS_DIALOG_DEFAULT_FLIPPED_TEXTURE.
     */
    public static final String SETTINGS_DIALOG_DEFAULT_FLIPPED_TEXTURE;
    /**
     * The constant SETTINGS_DIALOG_DEFAULT_EDITOR_CAMERA_LAMP_ENABLED.
     */
    public static final String SETTINGS_DIALOG_DEFAULT_EDITOR_CAMERA_LAMP_ENABLED;
    /**
     * The constant SETTINGS_DIALOG_TAB_GRAPHICS.
     */
    public static final String SETTINGS_DIALOG_TAB_GRAPHICS;
    /**
     * The constant SETTINGS_DIALOG_TAB_OTHER.
     */
    public static final String SETTINGS_DIALOG_TAB_OTHER;
    /**
     * The constant SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL.
     */
    public static final String SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL;
    /**
     * The constant SETTINGS_DIALOG_THEME_LABEL.
     */
    public static final String SETTINGS_DIALOG_THEME_LABEL;
    /**
     * The constant SETTINGS_DIALOG_OPEN_GL_LABEL.
     */
    public static final String SETTINGS_DIALOG_OPEN_GL_LABEL;
    /**
     * The constant SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE.
     */
    public static final String SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE;
    /**
     * The constant SETTINGS_DIALOG_ENVS_FOLDER_LABEL.
     */
    public static final String SETTINGS_DIALOG_ENVS_FOLDER_LABEL;
    /**
     * The constant SETTINGS_DIALOG_ENVS_FOLDER_CHOOSER_TITLE.
     */
    public static final String SETTINGS_DIALOG_ENVS_FOLDER_CHOOSER_TITLE;

    /**
     * The constant BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION.
     */
    public static final String BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    /**
     * The constant FBX_TO_J3O_FILE_CONVERTER_DESCRIPTION.
     */
    public static final String FBX_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    /**
     * The constant OBJ_TO_J3O_FILE_CONVERTER_DESCRIPTION.
     */
    public static final String OBJ_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    /**
     * The constant XBUF_TO_J3O_FILE_CONVERTER_DESCRIPTION.
     */
    public static final String XBUF_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    /**
     * The constant SCENE_TO_J3O_FILE_CONVERTER_DESCRIPTION.
     */
    public static final String SCENE_TO_J3O_FILE_CONVERTER_DESCRIPTION;
    /**
     * The constant MESH_XML_TO_J3O_FILE_CONVERTER_DESCRIPTION.
     */
    public static final String MESH_XML_TO_J3O_FILE_CONVERTER_DESCRIPTION;

    /**
     * The constant MODEL_FILE_EDITOR_NAME.
     */
    public static final String MODEL_FILE_EDITOR_NAME;
    /**
     * The constant MODEL_FILE_EDITOR_NO_SKY.
     */
    public static final String MODEL_FILE_EDITOR_NO_SKY;
    /**
     * The constant MODEL_FILE_EDITOR_FAST_SKY.
     */
    public static final String MODEL_FILE_EDITOR_FAST_SKY;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_MESH.
     */
    public static final String MODEL_FILE_EDITOR_NODE_MESH;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT.
     */
    public static final String MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT.
     */
    public static final String MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_POINT_LIGHT.
     */
    public static final String MODEL_FILE_EDITOR_NODE_POINT_LIGHT;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_SPOT_LIGHT.
     */
    public static final String MODEL_FILE_EDITOR_NODE_SPOT_LIGHT;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_LIGHT_PROBE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_LIGHT_PROBE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_ANIM_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_ANIM_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCERS.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCERS;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_EMPTY.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_EMPTY;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_DEFAULT.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_DEFAULT;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_RADIAL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_RADIAL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_BOX.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_BOX;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_SPHERE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_SPHERE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_POINT.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_POINT;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_VERTEX.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_VERTEX;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_FACE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_FACE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_CONVEX_HULL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_CONVEX_HULL;

    /**
     * The constant MODEL_FILE_EDITOR_NODE_STATIC_RIGID_BODY_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_STATIC_RIGID_BODY_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_RIGID_BODY_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_RIGID_BODY_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_CHARACTER_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_CHARACTER_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_SKELETON_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_SKELETON_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_VEHICLE_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_VEHICLE_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_RAGDOLL_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_RAGDOLL_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_BOX_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_BOX_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_CAPSULE_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_CAPSULE_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_CHILD_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_CHILD_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_COMPUTED_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_COMPUTED_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_CONE_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_CONE_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_CYLINDER_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_CYLINDER_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_GIMPACT_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_GIMPACT_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_HEIGHT_FIELD_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_HEIGHT_FIELD_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_HULL_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_HULL_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_MESH_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_MESH_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_PLANE_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_PLANE_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_SPHERE_COLLISION_SHAPE.
     */
    public static final String MODEL_FILE_EDITOR_NODE_SPHERE_COLLISION_SHAPE;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_WHEEL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_WHEEL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_MOTION_CONTROL.
     */
    public static final String MODEL_FILE_EDITOR_NODE_MOTION_CONTROL;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_MOTION_PATH.
     */
    public static final String MODEL_FILE_EDITOR_NODE_MOTION_PATH;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_WAY_POINT.
     */
    public static final String MODEL_FILE_EDITOR_NODE_WAY_POINT;
    /**
     * The constant MODEL_FILE_EDITOR_NODE_VERTEX_BUFFER.
     */
    public static final String MODEL_FILE_EDITOR_NODE_VERTEX_BUFFER;

    /**
     * The constant SCENE_FILE_EDITOR_NAME.
     */
    public static final String SCENE_FILE_EDITOR_NAME;
    /**
     * The constant SCENE_FILE_EDITOR_TOOL_OBJECTS.
     */
    public static final String SCENE_FILE_EDITOR_TOOL_OBJECTS;
    /**
     * The constant SCENE_FILE_EDITOR_TOOL_EDITING.
     */
    public static final String SCENE_FILE_EDITOR_TOOL_EDITING;
    /**
     * The constant SCENE_FILE_EDITOR_TOOL_SCRIPTING.
     */
    public static final String SCENE_FILE_EDITOR_TOOL_SCRIPTING;
    /**
     * The constant SCENE_FILE_EDITOR_TOOL_APP_STATES.
     */
    public static final String SCENE_FILE_EDITOR_TOOL_APP_STATES;
    /**
     * The constant SCENE_FILE_EDITOR_TOOL_FILTERS.
     */
    public static final String SCENE_FILE_EDITOR_TOOL_FILTERS;
    /**
     * The constant SCENE_FILE_EDITOR_TOOL_LAYERS.
     */
    public static final String SCENE_FILE_EDITOR_TOOL_LAYERS;

    /**
     * The constant MODEL_NODE_TREE_ACTION_REMOVE.
     */
    public static final String MODEL_NODE_TREE_ACTION_REMOVE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_RENAME.
     */
    public static final String MODEL_NODE_TREE_ACTION_RENAME;
    /**
     * The constant MODEL_NODE_TREE_ACTION_OPTIMIZE_GEOMETRY.
     */
    public static final String MODEL_NODE_TREE_ACTION_OPTIMIZE_GEOMETRY;
    /**
     * The constant MODEL_NODE_TREE_ACTION_TOOLS.
     */
    public static final String MODEL_NODE_TREE_ACTION_TOOLS;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_NODE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_NODE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_SKY.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_SKY;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_BOX.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_BOX;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_SPHERE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_SPHERE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_QUAD.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_QUAD;
    /**
     * The constant MODEL_NODE_TREE_ACTION_LOAD_MODEL.
     */
    public static final String MODEL_NODE_TREE_ACTION_LOAD_MODEL;
    /**
     * The constant MODEL_NODE_TREE_ACTION_LINK_MODEL.
     */
    public static final String MODEL_NODE_TREE_ACTION_LINK_MODEL;
    /**
     * The constant MODEL_NODE_TREE_ACTION_TANGENT_GENERATOR.
     */
    public static final String MODEL_NODE_TREE_ACTION_TANGENT_GENERATOR;
    /**
     * The constant MODEL_NODE_TREE_ACTION_LOD_GENERATOR.
     */
    public static final String MODEL_NODE_TREE_ACTION_LOD_GENERATOR;
    /**
     * The constant MODEL_NODE_TREE_ACTION_LIGHT.
     */
    public static final String MODEL_NODE_TREE_ACTION_LIGHT;
    /**
     * The constant MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT.
     */
    public static final String MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT;
    /**
     * The constant MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT.
     */
    public static final String MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT;
    /**
     * The constant MODEL_NODE_TREE_ACTION_POINT_LIGHT.
     */
    public static final String MODEL_NODE_TREE_ACTION_POINT_LIGHT;
    /**
     * The constant MODEL_NODE_TREE_ACTION_SPOT_LIGHT.
     */
    public static final String MODEL_NODE_TREE_ACTION_SPOT_LIGHT;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ANIMATION_PLAY.
     */
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_PLAY;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ANIMATION_PLAY_SETTINGS.
     */
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_PLAY_SETTINGS;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ANIMATION_STOP.
     */
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_STOP;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ANIMATION_MANUAL_EXTRAXT_SUB_ANIMATION.
     */
    public static final String MODEL_NODE_TREE_ACTION_ANIMATION_MANUAL_EXTRAXT_SUB_ANIMATION;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_AUDIO_NODE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_AUDIO_NODE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_AUDIO_PLAY.
     */
    public static final String MODEL_NODE_TREE_ACTION_AUDIO_PLAY;
    /**
     * The constant MODEL_NODE_TREE_ACTION_AUDIO_STOP.
     */
    public static final String MODEL_NODE_TREE_ACTION_AUDIO_STOP;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_TONEG0D_PARTICLE_EMITTER.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_TONEG0D_PARTICLE_EMITTER;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_SOFT_TONEG0D_PARTICLE_EMITTER.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_SOFT_TONEG0D_PARTICLE_EMITTER;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_DEFAULT_PARTICLE_EMITTER.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_DEFAULT_PARTICLE_EMITTER;
    /**
     * The constant MODEL_NODE_TREE_ACTION_RESET_PARTICLE_EMITTERS.
     */
    public static final String MODEL_NODE_TREE_ACTION_RESET_PARTICLE_EMITTERS;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TRIANGLE_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TRIANGLE_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_INFLUENCER.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_INFLUENCER;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_DEFAULT.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_DEFAULT;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_EMPTY.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_EMPTY;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_RADIAL.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_RADIAL;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_POINT_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_POINT_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_BOX_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_BOX_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_SPHERE_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_SPHERE_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_VERTEX_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_VERTEX_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_FACE_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_FACE_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_CONVEX_HULL_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_CONVEX_HULL_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CYLINDER_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CYLINDER_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_DOME_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_DOME_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_QUAD_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_QUAD_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TORUS_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TORUS_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MODEL_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MODEL_SHAPE;

    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_PARTICLES_MESH.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_PARTICLES_MESH;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_QUAD.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_QUAD;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_POINT.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_POINT;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_IMPOSTOR.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_IMPOSTOR;
    /**
     * The constant MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_MODEL.
     */
    public static final String MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_MODEL;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CREATE_LAYER.
     */
    public static final String MODEL_NODE_TREE_ACTION_CREATE_LAYER;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_USER_DATA.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_USER_DATA;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL_RIGID_BODY.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL_RIGID_BODY;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL_STATIC_RIGID_BODY.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL_STATIC_RIGID_BODY;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL_NOTION.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL_NOTION;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL_CHARACTER.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL_CHARACTER;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL_CUSTOM.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL_CUSTOM;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL_VEHICLE.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL_VEHICLE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_CONTROL_KINEMATIC_RAGDOLL.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_CONTROL_KINEMATIC_RAGDOLL;
    /**
     * The constant MODEL_NODE_TREE_ACTION_REACTIVATE.
     */
    public static final String MODEL_NODE_TREE_ACTION_REACTIVATE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CHANGE_COLLISION_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CHANGE_COLLISION_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_GENERATE_COLLISION_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_GENERATE_COLLISION_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_BOX_COLLISION_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_BOX_COLLISION_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CAPSULE_COLLISION_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CAPSULE_COLLISION_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CONE_COLLISION_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CONE_COLLISION_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_CYLINDER_COLLISION_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_CYLINDER_COLLISION_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_SPHERE_COLLISION_SHAPE.
     */
    public static final String MODEL_NODE_TREE_ACTION_SPHERE_COLLISION_SHAPE;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_WHEEL.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_WHEEL;
    /**
     * The constant MODEL_NODE_TREE_ACTION_ADD_TERRAIN.
     */
    public static final String MODEL_NODE_TREE_ACTION_ADD_TERRAIN;

    /**
     * The constant MODEL_PROPERTY_CULL_HINT.
     */
    public static final String MODEL_PROPERTY_CULL_HINT;
    /**
     * The constant MODEL_PROPERTY_SHADOW_MODE.
     */
    public static final String MODEL_PROPERTY_SHADOW_MODE;
    /**
     * The constant MODEL_PROPERTY_QUEUE_BUCKET.
     */
    public static final String MODEL_PROPERTY_QUEUE_BUCKET;
    /**
     * The constant MODEL_PROPERTY_LOCATION.
     */
    public static final String MODEL_PROPERTY_LOCATION;
    /**
     * The constant MODEL_PROPERTY_SCALE.
     */
    public static final String MODEL_PROPERTY_SCALE;
    /**
     * The constant MODEL_PROPERTY_ROTATION.
     */
    public static final String MODEL_PROPERTY_ROTATION;
    /**
     * The constant MODEL_PROPERTY_MATERIAL.
     */
    public static final String MODEL_PROPERTY_MATERIAL;
    /**
     * The constant MODEL_PROPERTY_DIRECTION.
     */
    public static final String MODEL_PROPERTY_DIRECTION;
    /**
     * The constant MODEL_PROPERTY_RADIUS.
     */
    public static final String MODEL_PROPERTY_RADIUS;
    /**
     * The constant MODEL_PROPERTY_COLOR.
     */
    public static final String MODEL_PROPERTY_COLOR;
    /**
     * The constant MODEL_PROPERTY_INNER_ANGLE.
     */
    public static final String MODEL_PROPERTY_INNER_ANGLE;
    /**
     * The constant MODEL_PROPERTY_OUTER_ANGLE.
     */
    public static final String MODEL_PROPERTY_OUTER_ANGLE;
    /**
     * The constant MODEL_PROPERTY_MIN.
     */
    public static final String MODEL_PROPERTY_MIN;
    /**
     * The constant MODEL_PROPERTY_MAX.
     */
    public static final String MODEL_PROPERTY_MAX;
    /**
     * The constant MODEL_PROPERTY_IS_LOOPING.
     */
    public static final String MODEL_PROPERTY_IS_LOOPING;
    /**
     * The constant MODEL_PROPERTY_IS_REVERB.
     */
    public static final String MODEL_PROPERTY_IS_REVERB;
    /**
     * The constant MODEL_PROPERTY_IS_DIRECTIONAL.
     */
    public static final String MODEL_PROPERTY_IS_DIRECTIONAL;
    /**
     * The constant MODEL_PROPERTY_IS_POSITIONAL.
     */
    public static final String MODEL_PROPERTY_IS_POSITIONAL;
    /**
     * The constant MODEL_PROPERTY_AUDIO_PITCH.
     */
    public static final String MODEL_PROPERTY_AUDIO_PITCH;
    /**
     * The constant MODEL_PROPERTY_AUDIO_VOLUME.
     */
    public static final String MODEL_PROPERTY_AUDIO_VOLUME;
    /**
     * The constant MODEL_PROPERTY_TIME_OFFSET.
     */
    public static final String MODEL_PROPERTY_TIME_OFFSET;
    /**
     * The constant MODEL_PROPERTY_MAX_DISTANCE.
     */
    public static final String MODEL_PROPERTY_MAX_DISTANCE;
    /**
     * The constant MODEL_PROPERTY_REF_DISTANCE.
     */
    public static final String MODEL_PROPERTY_REF_DISTANCE;
    /**
     * The constant MODEL_PROPERTY_AUDIO_DATA.
     */
    public static final String MODEL_PROPERTY_AUDIO_DATA;
    /**
     * The constant MODEL_PROPERTY_VELOCITY.
     */
    public static final String MODEL_PROPERTY_VELOCITY;
    /**
     * The constant MODEL_PROPERTY_LOD.
     */
    public static final String MODEL_PROPERTY_LOD;
    /**
     * The constant MODEL_PROPERTY_TRIANGLE_COUNT.
     */
    public static final String MODEL_PROPERTY_TRIANGLE_COUNT;
    /**
     * The constant MODEL_PROPERTY_LEVEL.
     */
    public static final String MODEL_PROPERTY_LEVEL;
    /**
     * The constant MODEL_PROPERTY_LAYER.
     */
    public static final String MODEL_PROPERTY_LAYER;
    /**
     * The constant MODEL_PROPERTY_VALUE.
     */
    public static final String MODEL_PROPERTY_VALUE;
    /**
     * The constant MODEL_PROPERTY_ID.
     */
    public static final String MODEL_PROPERTY_ID;
    /**
     * The constant MODEL_PROPERTY_INSTANCE_COUNT.
     */
    public static final String MODEL_PROPERTY_INSTANCE_COUNT;
    /**
     * The constant MODEL_PROPERTY_VERTEX_COUNT.
     */
    public static final String MODEL_PROPERTY_VERTEX_COUNT;
    /**
     * The constant MODEL_PROPERTY_NUM_LOD_LEVELS.
     */
    public static final String MODEL_PROPERTY_NUM_LOD_LEVELS;
    /**
     * The constant MODEL_PROPERTY_MODE.
     */
    public static final String MODEL_PROPERTY_MODE;
    /**
     * The constant MODEL_PROPERTY_TYPE.
     */
    public static final String MODEL_PROPERTY_TYPE;
    /**
     * The constant MODEL_PROPERTY_FORMAT.
     */
    public static final String MODEL_PROPERTY_FORMAT;
    /**
     * The constant MODEL_PROPERTY_USAGE.
     */
    public static final String MODEL_PROPERTY_USAGE;
    /**
     * The constant MODEL_PROPERTY_UNIQ_ID.
     */
    public static final String MODEL_PROPERTY_UNIQ_ID;
    /**
     * The constant MODEL_PROPERTY_BASE_INSTANCE_COUNT.
     */
    public static final String MODEL_PROPERTY_BASE_INSTANCE_COUNT;
    /**
     * The constant MODEL_PROPERTY_INSTANCE_SPAN.
     */
    public static final String MODEL_PROPERTY_INSTANCE_SPAN;
    /**
     * The constant MODEL_PROPERTY_NUM_COMPONENTS.
     */
    public static final String MODEL_PROPERTY_NUM_COMPONENTS;
    /**
     * The constant MODEL_PROPERTY_NUM_ELEMENTS.
     */
    public static final String MODEL_PROPERTY_NUM_ELEMENTS;
    /**
     * The constant MODEL_PROPERTY_OFFSET.
     */
    public static final String MODEL_PROPERTY_OFFSET;
    /**
     * The constant MODEL_PROPERTY_STRIDE.
     */
    public static final String MODEL_PROPERTY_STRIDE;
    /**
     * The constant MODEL_PROPERTY_CAPACITY.
     */
    public static final String MODEL_PROPERTY_CAPACITY;
    /**
     * The constant MODEL_PROPERTY_IS_ENABLED.
     */
    public static final String MODEL_PROPERTY_IS_ENABLED;
    /**
     * The constant MODEL_PROPERTY_IS_HARDWARE_SKINNING_PREFERRED.
     */
    public static final String MODEL_PROPERTY_IS_HARDWARE_SKINNING_PREFERRED;
    /**
     * The constant MODEL_PROPERTY_VIEW_DIRECTION.
     */
    public static final String MODEL_PROPERTY_VIEW_DIRECTION;
    /**
     * The constant MODEL_PROPERTY_WALK_DIRECTION.
     */
    public static final String MODEL_PROPERTY_WALK_DIRECTION;
    /**
     * The constant MODEL_PROPERTY_FALL_SPEED.
     */
    public static final String MODEL_PROPERTY_FALL_SPEED;
    /**
     * The constant MODEL_PROPERTY_GRAVITY.
     */
    public static final String MODEL_PROPERTY_GRAVITY;
    /**
     * The constant MODEL_PROPERTY_JUMP_SPEED.
     */
    public static final String MODEL_PROPERTY_JUMP_SPEED;
    /**
     * The constant MODEL_PROPERTY_MAX_SLOPE.
     */
    public static final String MODEL_PROPERTY_MAX_SLOPE;
    /**
     * The constant MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL.
     */
    public static final String MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL;
    /**
     * The constant MODEL_PROPERTY_IS_USE_VIEW_DIRECTION.
     */
    public static final String MODEL_PROPERTY_IS_USE_VIEW_DIRECTION;
    /**
     * The constant MODEL_PROPERTY_IS_KINEMATIC_SPATIAL.
     */
    public static final String MODEL_PROPERTY_IS_KINEMATIC_SPATIAL;
    /**
     * The constant MODEL_PROPERTY_IS_KINEMATIC.
     */
    public static final String MODEL_PROPERTY_IS_KINEMATIC;
    /**
     * The constant MODEL_PROPERTY_ANGULAR_VELOCITY.
     */
    public static final String MODEL_PROPERTY_ANGULAR_VELOCITY;
    /**
     * The constant MODEL_PROPERTY_LINEAR_FACTOR.
     */
    public static final String MODEL_PROPERTY_LINEAR_FACTOR;
    /**
     * The constant MODEL_PROPERTY_ANGULAR_DAMPING.
     */
    public static final String MODEL_PROPERTY_ANGULAR_DAMPING;
    /**
     * The constant MODEL_PROPERTY_ANGULAR_FACTOR.
     */
    public static final String MODEL_PROPERTY_ANGULAR_FACTOR;
    /**
     * The constant MODEL_PROPERTY_FRICTION.
     */
    public static final String MODEL_PROPERTY_FRICTION;
    /**
     * The constant MODEL_PROPERTY_LINEAR_DAMPING.
     */
    public static final String MODEL_PROPERTY_LINEAR_DAMPING;
    /**
     * The constant MODEL_PROPERTY_MASS.
     */
    public static final String MODEL_PROPERTY_MASS;
    /**
     * The constant MODEL_PROPERTY_RESTITUTION.
     */
    public static final String MODEL_PROPERTY_RESTITUTION;
    /**
     * The constant MODEL_PROPERTY_CURRENT_VALUE.
     */
    public static final String MODEL_PROPERTY_CURRENT_VALUE;
    /**
     * The constant MODEL_PROPERTY_CURRENT_WAY_POINT.
     */
    public static final String MODEL_PROPERTY_CURRENT_WAY_POINT;
    /**
     * The constant MODEL_PROPERTY_DIRECTION_TYPE.
     */
    public static final String MODEL_PROPERTY_DIRECTION_TYPE;
    /**
     * The constant MODEL_PROPERTY_ANGULAR_SLEEPING_THRESHOLD.
     */
    public static final String MODEL_PROPERTY_ANGULAR_SLEEPING_THRESHOLD;
    /**
     * The constant MODEL_PROPERTY_LOOP_MODE.
     */
    public static final String MODEL_PROPERTY_LOOP_MODE;
    /**
     * The constant MODEL_PROPERTY_INITIAL_DURATION.
     */
    public static final String MODEL_PROPERTY_INITIAL_DURATION;
    /**
     * The constant MODEL_PROPERTY_SPEED.
     */
    public static final String MODEL_PROPERTY_SPEED;
    /**
     * The constant MODEL_PROPERTY_TIME.
     */
    public static final String MODEL_PROPERTY_TIME;
    /**
     * The constant MODEL_PROPERTY_MARGIN.
     */
    public static final String MODEL_PROPERTY_MARGIN;
    /**
     * The constant MODEL_PROPERTY_HALF_EXTENTS.
     */
    public static final String MODEL_PROPERTY_HALF_EXTENTS;
    /**
     * The constant MODEL_PROPERTY_HEIGHT.
     */
    public static final String MODEL_PROPERTY_HEIGHT;
    /**
     * The constant MODEL_PROPERTY_AXIS.
     */
    public static final String MODEL_PROPERTY_AXIS;
    /**
     * The constant MODEL_PROPERTY_OBJECT_ID.
     */
    public static final String MODEL_PROPERTY_OBJECT_ID;
    /**
     * The constant MODEL_PROPERTY_AXLE.
     */
    public static final String MODEL_PROPERTY_AXLE;
    /**
     * The constant MODEL_PROPERTY_REST_LENGTH.
     */
    public static final String MODEL_PROPERTY_REST_LENGTH;
    /**
     * The constant MODEL_PROPERTY_IS_FRONT.
     */
    public static final String MODEL_PROPERTY_IS_FRONT;
    /**
     * The constant MODEL_PROPERTY_DAMPING_COMPRESSION.
     */
    public static final String MODEL_PROPERTY_DAMPING_COMPRESSION;
    /**
     * The constant MODEL_PROPERTY_FRICTION_SLIP.
     */
    public static final String MODEL_PROPERTY_FRICTION_SLIP;
    /**
     * The constant MODEL_PROPERTY_MAX_SUSPENSION_FORCE.
     */
    public static final String MODEL_PROPERTY_MAX_SUSPENSION_FORCE;
    /**
     * The constant MODEL_PROPERTY_MAX_SUSPENSION_TRAVEL_CM.
     */
    public static final String MODEL_PROPERTY_MAX_SUSPENSION_TRAVEL_CM;
    /**
     * The constant MODEL_PROPERTY_DAMPING_RELAXATION.
     */
    public static final String MODEL_PROPERTY_DAMPING_RELAXATION;
    /**
     * The constant MODEL_PROPERTY_SUSPENSION_STIFFNESS.
     */
    public static final String MODEL_PROPERTY_SUSPENSION_STIFFNESS;
    /**
     * The constant MODEL_PROPERTY_ROLL_INFLUENCE.
     */
    public static final String MODEL_PROPERTY_ROLL_INFLUENCE;
    /**
     * The constant MODEL_PROPERTY_WHEEL_SPATIAL.
     */
    public static final String MODEL_PROPERTY_WHEEL_SPATIAL;
    /**
     * The constant MODEL_PROPERTY_LENGTH.
     */
    public static final String MODEL_PROPERTY_LENGTH;
    /**
     * The constant MODEL_PROPERTY_CURRENT_TIME.
     */
    public static final String MODEL_PROPERTY_CURRENT_TIME;
    /**
     * The constant MODEL_PROPERTY_POINT.
     */
    public static final String MODEL_PROPERTY_POINT;
    /**
     * The constant MODEL_PROPERTY_CENTER.
     */
    public static final String MODEL_PROPERTY_CENTER;
    /**
     * The constant MODEL_PROPERTY_VELOCITY_VARIATION.
     */
    public static final String MODEL_PROPERTY_VELOCITY_VARIATION;
    /**
     * The constant MODEL_PROPERTY_INITIAL_VELOCITY.
     */
    public static final String MODEL_PROPERTY_INITIAL_VELOCITY;
    /**
     * The constant MODEL_PROPERTY_ORIGIN.
     */
    public static final String MODEL_PROPERTY_ORIGIN;
    /**
     * The constant MODEL_PROPERTY_RADIAL_VELOCITY.
     */
    public static final String MODEL_PROPERTY_RADIAL_VELOCITY;
    /**
     * The constant MODEL_PROPERTY_IS_HORIZONTAL.
     */
    public static final String MODEL_PROPERTY_IS_HORIZONTAL;
    /**
     * The constant MODEL_PROPERTY_IS_TEST_MODE.
     */
    public static final String MODEL_PROPERTY_IS_TEST_MODE;
    /**
     * The constant MODEL_PROPERTY_IS_FACING_VELOCITY.
     */
    public static final String MODEL_PROPERTY_IS_FACING_VELOCITY;
    /**
     * The constant MODEL_PROPERTY_IS_IN_WORLD_SPACE.
     */
    public static final String MODEL_PROPERTY_IS_IN_WORLD_SPACE;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_ANGLE.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_ANGLE;
    /**
     * The constant MODEL_PROPERTY_IS_SELECT_RANDOM_IMAGE.
     */
    public static final String MODEL_PROPERTY_IS_SELECT_RANDOM_IMAGE;
    /**
     * The constant MODEL_PROPERTY_SIZE.
     */
    public static final String MODEL_PROPERTY_SIZE;
    /**
     * The constant MODEL_PROPERTY_ROTATE_SPEED.
     */
    public static final String MODEL_PROPERTY_ROTATE_SPEED;
    /**
     * The constant MODEL_PROPERTY_START_COLOR.
     */
    public static final String MODEL_PROPERTY_START_COLOR;
    /**
     * The constant MODEL_PROPERTY_END_COLOR.
     */
    public static final String MODEL_PROPERTY_END_COLOR;
    /**
     * The constant MODEL_PROPERTY_MESH_TYPE.
     */
    public static final String MODEL_PROPERTY_MESH_TYPE;
    /**
     * The constant MODEL_PROPERTY_FACE_NORMAL.
     */
    public static final String MODEL_PROPERTY_FACE_NORMAL;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_POINT.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_POINT;
    /**
     * The constant MODEL_PROPERTY_IS_SEQUENTIAL_FACE.
     */
    public static final String MODEL_PROPERTY_IS_SEQUENTIAL_FACE;
    /**
     * The constant MODEL_PROPERTY_IS_SKIP_PATTERN.
     */
    public static final String MODEL_PROPERTY_IS_SKIP_PATTERN;
    /**
     * The constant MODEL_PROPERTY_EMISSION_POINT.
     */
    public static final String MODEL_PROPERTY_EMISSION_POINT;
    /**
     * The constant MODEL_PROPERTY_MAX_PARTICLES.
     */
    public static final String MODEL_PROPERTY_MAX_PARTICLES;
    /**
     * The constant MODEL_PROPERTY_EMISSION_PER_SECOND.
     */
    public static final String MODEL_PROPERTY_EMISSION_PER_SECOND;
    /**
     * The constant MODEL_PROPERTY_PARTICLES_PER_SECOND.
     */
    public static final String MODEL_PROPERTY_PARTICLES_PER_SECOND;
    /**
     * The constant MODEL_PROPERTY_EMITTER_LIFE.
     */
    public static final String MODEL_PROPERTY_EMITTER_LIFE;
    /**
     * The constant MODEL_PROPERTY_EMITTER_DELAY.
     */
    public static final String MODEL_PROPERTY_EMITTER_DELAY;
    /**
     * The constant MODEL_PROPERTY_IS_TEST_PARTICLES.
     */
    public static final String MODEL_PROPERTY_IS_TEST_PARTICLES;
    /**
     * The constant MODEL_PROPERTY_IS_FOLLOW_EMITTER.
     */
    public static final String MODEL_PROPERTY_IS_FOLLOW_EMITTER;
    /**
     * The constant MODEL_PROPERTY_STRETCHING.
     */
    public static final String MODEL_PROPERTY_STRETCHING;
    /**
     * The constant MODEL_PROPERTY_MAGNITUDE.
     */
    public static final String MODEL_PROPERTY_MAGNITUDE;
    /**
     * The constant MODEL_PROPERTY_BILLBOARD.
     */
    public static final String MODEL_PROPERTY_BILLBOARD;
    /**
     * The constant MODEL_PROPERTY_INITIAL_FORCE.
     */
    public static final String MODEL_PROPERTY_INITIAL_FORCE;
    /**
     * The constant MODEL_PROPERTY_LIFE.
     */
    public static final String MODEL_PROPERTY_LIFE;
    /**
     * The constant MODEL_PROPERTY_COLUMNS.
     */
    public static final String MODEL_PROPERTY_COLUMNS;
    /**
     * The constant MODEL_PROPERTY_ROWS.
     */
    public static final String MODEL_PROPERTY_ROWS;
    /**
     * The constant MODEL_PROPERTY_SPRITE_COUNT.
     */
    public static final String MODEL_PROPERTY_SPRITE_COUNT;
    /**
     * The constant MODEL_PROPERTY_FIXED_DURATION.
     */
    public static final String MODEL_PROPERTY_FIXED_DURATION;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_START_COLOR.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_START_COLOR;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_START_SIZE.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_START_SIZE;
    /**
     * The constant MODEL_PROPERTY_SIZE_VARIATION_FACTOR.
     */
    public static final String MODEL_PROPERTY_SIZE_VARIATION_FACTOR;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_START_DESTINATION.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_START_DESTINATION;
    /**
     * The constant MODEL_PROPERTY_CHANCE.
     */
    public static final String MODEL_PROPERTY_CHANCE;
    /**
     * The constant MODEL_PROPERTY_STRENGTH.
     */
    public static final String MODEL_PROPERTY_STRENGTH;
    /**
     * The constant MODEL_PROPERTY_ALIGNMENT.
     */
    public static final String MODEL_PROPERTY_ALIGNMENT;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_DIRECTION.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_DIRECTION;
    /**
     * The constant MODEL_PROPERTY_PULL_CENTER.
     */
    public static final String MODEL_PROPERTY_PULL_CENTER;
    /**
     * The constant MODEL_PROPERTY_PULL_ALIGNMENT.
     */
    public static final String MODEL_PROPERTY_PULL_ALIGNMENT;
    /**
     * The constant MODEL_PROPERTY_UP_ALIGNMENT.
     */
    public static final String MODEL_PROPERTY_UP_ALIGNMENT;
    /**
     * The constant MODEL_PROPERTY_RADIAL_PULL.
     */
    public static final String MODEL_PROPERTY_RADIAL_PULL;
    /**
     * The constant MODEL_PROPERTY_TANGENT_FORCE.
     */
    public static final String MODEL_PROPERTY_TANGENT_FORCE;
    /**
     * The constant MODEL_PROPERTY_ALPHA_INTERPOLATION.
     */
    public static final String MODEL_PROPERTY_ALPHA_INTERPOLATION;
    /**
     * The constant MODEL_PROPERTY_COLOR_INTERPOLATION.
     */
    public static final String MODEL_PROPERTY_COLOR_INTERPOLATION;
    /**
     * The constant MODEL_PROPERTY_DESTINATION_INTERPOLATION.
     */
    public static final String MODEL_PROPERTY_DESTINATION_INTERPOLATION;
    /**
     * The constant MODEL_PROPERTY_ROTATION_INTERPOLATION.
     */
    public static final String MODEL_PROPERTY_ROTATION_INTERPOLATION;
    /**
     * The constant MODEL_PROPERTY_SIZE_INTERPOLATION.
     */
    public static final String MODEL_PROPERTY_SIZE_INTERPOLATION;
    /**
     * The constant MODEL_PROPERTY_ALPHA.
     */
    public static final String MODEL_PROPERTY_ALPHA;
    /**
     * The constant MODEL_PROPERTY_FRAME_SEQUENCE.
     */
    public static final String MODEL_PROPERTY_FRAME_SEQUENCE;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_START_IMAGE.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_START_IMAGE;
    /**
     * The constant MODEL_PROPERTY_IS_ANIMATE.
     */
    public static final String MODEL_PROPERTY_IS_ANIMATE;
    /**
     * The constant MODEL_PROPERTY_REACTION.
     */
    public static final String MODEL_PROPERTY_REACTION;
    /**
     * The constant MODEL_PROPERTY_IS_RANDOM_SPEED.
     */
    public static final String MODEL_PROPERTY_IS_RANDOM_SPEED;
    /**
     * The constant MODEL_PROPERTY_IS_START_RANDOM_ROTATION_X.
     */
    public static final String MODEL_PROPERTY_IS_START_RANDOM_ROTATION_X;
    /**
     * The constant MODEL_PROPERTY_INTERPOLATION.
     */
    public static final String MODEL_PROPERTY_INTERPOLATION;
    /**
     * The constant MODEL_PROPERTY_GEOMETRY_LIST.
     */
    public static final String MODEL_PROPERTY_GEOMETRY_LIST;
    /**
     * The constant MODEL_PROPERTY_GEOMETRY.
     */
    public static final String MODEL_PROPERTY_GEOMETRY;
    /**
     * The constant MODEL_PROPERTY_AXIS_SAMPLES.
     */
    public static final String MODEL_PROPERTY_AXIS_SAMPLES;
    /**
     * The constant MODEL_PROPERTY_RADIAL_SAMPLES.
     */
    public static final String MODEL_PROPERTY_RADIAL_SAMPLES;
    /**
     * The constant MODEL_PROPERTY_PLANES.
     */
    public static final String MODEL_PROPERTY_PLANES;
    /**
     * The constant MODEL_PROPERTY_WIDTH.
     */
    public static final String MODEL_PROPERTY_WIDTH;
    /**
     * The constant MODEL_PROPERTY_FLIP_COORDS.
     */
    public static final String MODEL_PROPERTY_FLIP_COORDS;
    /**
     * The constant MODEL_PROPERTY_Z_SAMPLES.
     */
    public static final String MODEL_PROPERTY_Z_SAMPLES;
    /**
     * The constant MODEL_PROPERTY_CIRCLE_SAMPLES.
     */
    public static final String MODEL_PROPERTY_CIRCLE_SAMPLES;
    /**
     * The constant MODEL_PROPERTY_INNER_RADIUS.
     */
    public static final String MODEL_PROPERTY_INNER_RADIUS;
    /**
     * The constant MODEL_PROPERTY_OUTER_RADIUS.
     */
    public static final String MODEL_PROPERTY_OUTER_RADIUS;
    /**
     * The constant MODEL_PROPERTY_NAME.
     */
    public static final String MODEL_PROPERTY_NAME;
    /**
     * The constant MODEL_PROPERTY_DATA_TYPE.
     */
    public static final String MODEL_PROPERTY_DATA_TYPE;
    /**
     * The constant MATERIAL_MODEL_PROPERTY_CONTROL_NO_TEXTURE.
     */
    public static final String MATERIAL_MODEL_PROPERTY_CONTROL_NO_TEXTURE;
    /**
     * The constant MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL.
     */
    public static final String MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;
    /**
     * The constant ABSTRACT_ELEMENT_PROPERTY_CONTROL_NO_ELEMENT.
     */
    public static final String ABSTRACT_ELEMENT_PROPERTY_CONTROL_NO_ELEMENT;
    /**
     * The constant LAYER_PROPERTY_CONTROL_NO_LAYER.
     */
    public static final String LAYER_PROPERTY_CONTROL_NO_LAYER;
    /**
     * The constant AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO.
     */
    public static final String AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO;
    /**
     * The constant CHOOSE_FOLDER_CONTROL_NO_FOLDER.
     */
    public static final String CHOOSE_FOLDER_CONTROL_NO_FOLDER;

    /**
     * The constant RENAME_DIALOG_TITLE.
     */
    public static final String RENAME_DIALOG_TITLE;
    /**
     * The constant RENAME_DIALOG_NEW_NAME_LABEL.
     */
    public static final String RENAME_DIALOG_NEW_NAME_LABEL;
    /**
     * The constant RENAME_DIALOG_BUTTON_OK.
     */
    public static final String RENAME_DIALOG_BUTTON_OK;
    /**
     * The constant RENAME_DIALOG_BUTTON_CANCEL.
     */
    public static final String RENAME_DIALOG_BUTTON_CANCEL;

    /**
     * The constant PLAY_ANIMATION_SETTINGS_DIALOG_TITLE.
     */
    public static final String PLAY_ANIMATION_SETTINGS_DIALOG_TITLE;
    /**
     * The constant PLAY_ANIMATION_SETTINGS_DIALOG_BUTTON_OK.
     */
    public static final String PLAY_ANIMATION_SETTINGS_DIALOG_BUTTON_OK;

    /**
     * The constant MANUAL_EXTRACT_ANIMATION_DIALOG_TITLE.
     */
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_TITLE;
    /**
     * The constant MANUAL_EXTRACT_ANIMATION_DIALOG_NAME.
     */
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_NAME;
    /**
     * The constant MANUAL_EXTRACT_ANIMATION_DIALOG_NAME_EXAMPLE.
     */
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_NAME_EXAMPLE;
    /**
     * The constant MANUAL_EXTRACT_ANIMATION_DIALOG_START_FRAME.
     */
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_START_FRAME;
    /**
     * The constant MANUAL_EXTRACT_ANIMATION_DIALOG_END_FRAME.
     */
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_END_FRAME;
    /**
     * The constant MANUAL_EXTRACT_ANIMATION_DIALOG_BUTTON_OK.
     */
    public static final String MANUAL_EXTRACT_ANIMATION_DIALOG_BUTTON_OK;

    /**
     * The constant QUESTION_DIALOG_TITLE.
     */
    public static final String QUESTION_DIALOG_TITLE;
    /**
     * The constant QUESTION_DIALOG_BUTTON_OK.
     */
    public static final String QUESTION_DIALOG_BUTTON_OK;
    /**
     * The constant QUESTION_DIALOG_BUTTON_CANCEL.
     */
    public static final String QUESTION_DIALOG_BUTTON_CANCEL;

    /**
     * The constant FOLDER_CREATOR_DESCRIPTION.
     */
    public static final String FOLDER_CREATOR_DESCRIPTION;
    /**
     * The constant FOLDER_CREATOR_TITLE.
     */
    public static final String FOLDER_CREATOR_TITLE;
    /**
     * The constant FOLDER_CREATOR_FILE_NAME_LABEL.
     */
    public static final String FOLDER_CREATOR_FILE_NAME_LABEL;

    /**
     * The constant EMPTY_FILE_CREATOR_DESCRIPTION.
     */
    public static final String EMPTY_FILE_CREATOR_DESCRIPTION;
    /**
     * The constant EMPTY_FILE_CREATOR_TITLE.
     */
    public static final String EMPTY_FILE_CREATOR_TITLE;

    /**
     * The constant IMAGE_VIEWER_EDITOR_NAME.
     */
    public static final String IMAGE_VIEWER_EDITOR_NAME;

    /**
     * The constant AUDIO_VIEWER_EDITOR_NAME.
     */
    public static final String AUDIO_VIEWER_EDITOR_NAME;
    /**
     * The constant AUDIO_VIEWER_EDITOR_DURATION_LABEL.
     */
    public static final String AUDIO_VIEWER_EDITOR_DURATION_LABEL;
    /**
     * The constant AUDIO_VIEWER_EDITOR_BITS_PER_SAMPLE_LABEL.
     */
    public static final String AUDIO_VIEWER_EDITOR_BITS_PER_SAMPLE_LABEL;
    /**
     * The constant AUDIO_VIEWER_EDITOR_CHANNELS_LABEL.
     */
    public static final String AUDIO_VIEWER_EDITOR_CHANNELS_LABEL;
    /**
     * The constant AUDIO_VIEWER_EDITOR_DATA_TYPE_LABEL.
     */
    public static final String AUDIO_VIEWER_EDITOR_DATA_TYPE_LABEL;
    /**
     * The constant AUDIO_VIEWER_EDITOR_SAMPLE_RATE_LABEL.
     */
    public static final String AUDIO_VIEWER_EDITOR_SAMPLE_RATE_LABEL;

    /**
     * The constant CREATE_SKY_DIALOG_TITLE.
     */
    public static final String CREATE_SKY_DIALOG_TITLE;
    /**
     * The constant CREATE_SKY_DIALOG_SKY_TYPE_SINGLE.
     */
    public static final String CREATE_SKY_DIALOG_SKY_TYPE_SINGLE;
    /**
     * The constant CREATE_SKY_DIALOG_SKY_TYPE_MULTIPLE.
     */
    public static final String CREATE_SKY_DIALOG_SKY_TYPE_MULTIPLE;
    /**
     * The constant CREATE_SKY_DIALOG_SKY_TYPE_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_SKY_TYPE_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_NORMAL_SCALE_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_NORMAL_SCALE_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_TEXTURE_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_TEXTURE_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_TEXTURE_TYPE_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_TEXTURE_TYPE_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_FLIP_Y_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_FLIP_Y_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_NORTH_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_NORTH_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_SOUTH_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_SOUTH_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_EAST_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_EAST_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_WEST_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_WEST_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_TOP_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_TOP_LABEL;
    /**
     * The constant CREATE_SKY_DIALOG_BOTTOM_LABEL.
     */
    public static final String CREATE_SKY_DIALOG_BOTTOM_LABEL;

    /**
     * The constant SIMPLE_DIALOG_BUTTON_OK.
     */
    public static final String SIMPLE_DIALOG_BUTTON_OK;
    /**
     * The constant SIMPLE_DIALOG_BUTTON_CANCEL.
     */
    public static final String SIMPLE_DIALOG_BUTTON_CANCEL;

    /**
     * The constant EMPTY_MODEL_CREATOR_DESCRIPTION.
     */
    public static final String EMPTY_MODEL_CREATOR_DESCRIPTION;
    /**
     * The constant EMPTY_MODEL_CREATOR_TITLE.
     */
    public static final String EMPTY_MODEL_CREATOR_TITLE;

    /**
     * The constant EMPTY_SCENE_CREATOR_DESCRIPTION.
     */
    public static final String EMPTY_SCENE_CREATOR_DESCRIPTION;
    /**
     * The constant DEFAULT_SCENE_CREATOR_DESCRIPTION.
     */
    public static final String DEFAULT_SCENE_CREATOR_DESCRIPTION;
    /**
     * The constant EMPTY_SCENE_CREATOR_TITLE.
     */
    public static final String EMPTY_SCENE_CREATOR_TITLE;
    /**
     * The constant DEFAULT_SCENE_CREATOR_TITLE.
     */
    public static final String DEFAULT_SCENE_CREATOR_TITLE;

    /**
     * The constant GLSL_FILE_EDITOR_NAME.
     */
    public static final String GLSL_FILE_EDITOR_NAME;
    /**
     * The constant MATERIAL_DEFINITION_FILE_EDITOR_NAME.
     */
    public static final String MATERIAL_DEFINITION_FILE_EDITOR_NAME;

    /**
     * The constant GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED.
     */
    public static final String GENERATE_TANGENTS_DIALOG_SPLIT_MIRRORED;
    /**
     * The constant GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL.
     */
    public static final String GENERATE_TANGENTS_DIALOG_ALGORITHM_LABEL;
    /**
     * The constant GENERATE_TANGENTS_DIALOG_TITLE.
     */
    public static final String GENERATE_TANGENTS_DIALOG_TITLE;
    /**
     * The constant GENERATE_TANGENTS_DIALOG_BUTTON_OK.
     */
    public static final String GENERATE_TANGENTS_DIALOG_BUTTON_OK;

    /**
     * The constant GENERATE_LOD_DIALOG_TITLE.
     */
    public static final String GENERATE_LOD_DIALOG_TITLE;
    /**
     * The constant GENERATE_LOD_DIALOG_METHOD.
     */
    public static final String GENERATE_LOD_DIALOG_METHOD;
    /**
     * The constant GENERATE_LOD_DIALOG_BUTTON_GENERATE.
     */
    public static final String GENERATE_LOD_DIALOG_BUTTON_GENERATE;

    /**
     * The constant BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME.
     */
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME;
    /**
     * The constant BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE.
     */
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE;
    /**
     * The constant BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS.
     */
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS;
    /**
     * The constant BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX.
     */
    public static final String BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX;

    /**
     * The constant NODE_SELECTOR_DIALOG_TITLE.
     */
    public static final String NODE_SELECTOR_DIALOG_TITLE;
    /**
     * The constant NODE_SELECTOR_DIALOG_BUTTON.
     */
    public static final String NODE_SELECTOR_DIALOG_BUTTON;

    /**
     * The constant GEOMETRY_SELECTOR_DIALOG_TITLE.
     */
    public static final String GEOMETRY_SELECTOR_DIALOG_TITLE;
    /**
     * The constant LIGHT_SELECTOR_DIALOG_TITLE.
     */
    public static final String LIGHT_SELECTOR_DIALOG_TITLE;

    /**
     * The constant LOG_VIEW_TITLE.
     */
    public static final String LOG_VIEW_TITLE;

    /**
     * The constant CREATE_SCENE_APP_STATE_DIALOG_TITLE.
     */
    public static final String CREATE_SCENE_APP_STATE_DIALOG_TITLE;
    /**
     * The constant CREATE_SCENE_APP_STATE_DIALOG_BUILT_IN.
     */
    public static final String CREATE_SCENE_APP_STATE_DIALOG_BUILT_IN;
    /**
     * The constant CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_BOX.
     */
    public static final String CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_BOX;
    /**
     * The constant CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_FIELD.
     */
    public static final String CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_FIELD;

    /**
     * The constant CREATE_SCENE_FILTER_DIALOG_TITLE.
     */
    public static final String CREATE_SCENE_FILTER_DIALOG_TITLE;
    /**
     * The constant CREATE_SCENE_FILTER_DIALOG_BUILT_IN.
     */
    public static final String CREATE_SCENE_FILTER_DIALOG_BUILT_IN;
    /**
     * The constant CREATE_SCENE_FILTER_DIALOG_CUSTOM_BOX.
     */
    public static final String CREATE_SCENE_FILTER_DIALOG_CUSTOM_BOX;
    /**
     * The constant CREATE_SCENE_FILTER_DIALOG_CUSTOM_FIELD.
     */
    public static final String CREATE_SCENE_FILTER_DIALOG_CUSTOM_FIELD;

    /**
     * The constant ADD_USER_DATA_DIALOG_TITLE.
     */
    public static final String ADD_USER_DATA_DIALOG_TITLE;
    /**
     * The constant ADD_USER_DATA_DIALOG_BUTTON_OK.
     */
    public static final String ADD_USER_DATA_DIALOG_BUTTON_OK;

    /**
     * The constant CREATE_CUSTOM_CONTROL_DIALOG_TITLE.
     */
    public static final String CREATE_CUSTOM_CONTROL_DIALOG_TITLE;
    /**
     * The constant CREATE_CUSTOM_CONTROL_DIALOG_BUILT_IN.
     */
    public static final String CREATE_CUSTOM_CONTROL_DIALOG_BUILT_IN;
    /**
     * The constant CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_BOX.
     */
    public static final String CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_BOX;
    /**
     * The constant CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_FIELD.
     */
    public static final String CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_FIELD;

    /**
     * The constant ANALYTICS_CONFIRM_DIALOG_MESSAGE.
     */
    public static final String ANALYTICS_CONFIRM_DIALOG_MESSAGE;

    /**
     * The constant CREATE_BOX_COLLISION_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_BOX_COLLISION_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_SPHERE_COLLISION_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_SPHERE_COLLISION_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_CYLINDER_COLLISION_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_CYLINDER_COLLISION_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_CONE_COLLISION_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_CONE_COLLISION_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_CAPSULE_COLLISION_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_CAPSULE_COLLISION_SHAPE_DIALOG_TITLE;

    /**
     * The constant ADD_VEHICLE_WHEEL_DIALOG_TITLE.
     */
    public static final String ADD_VEHICLE_WHEEL_DIALOG_TITLE;

    /**
     * The constant CREATE_TERRAIN_DIALOG_TITLE.
     */
    public static final String CREATE_TERRAIN_DIALOG_TITLE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_BASE_TEXTURE.
     */
    public static final String CREATE_TERRAIN_DIALOG_BASE_TEXTURE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_FOLDER_ALPHA_TEXTURE.
     */
    public static final String CREATE_TERRAIN_DIALOG_FOLDER_ALPHA_TEXTURE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_TOTAL_SIZE.
     */
    public static final String CREATE_TERRAIN_DIALOG_TOTAL_SIZE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_PATCH_SIZE.
     */
    public static final String CREATE_TERRAIN_DIALOG_PATCH_SIZE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_ALPHA_BLEND_TEXTURE_SIZE.
     */
    public static final String CREATE_TERRAIN_DIALOG_ALPHA_BLEND_TEXTURE_SIZE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_TERRAIN_TYPE.
     */
    public static final String CREATE_TERRAIN_DIALOG_TERRAIN_TYPE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_FLAT.
     */
    public static final String CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_FLAT;
    /**
     * The constant CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_IMAGE_BASED.
     */
    public static final String CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_IMAGE_BASED;
    /**
     * The constant CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_HILL.
     */
    public static final String CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_HILL;
    /**
     * The constant CREATE_TERRAIN_DIALOG_HEIGHT_MAP_IMAGE.
     */
    public static final String CREATE_TERRAIN_DIALOG_HEIGHT_MAP_IMAGE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_HEIGHT_SMOOTH.
     */
    public static final String CREATE_TERRAIN_DIALOG_HEIGHT_SMOOTH;
    /**
     * The constant CREATE_TERRAIN_DIALOG_HEIGHT_SCALE.
     */
    public static final String CREATE_TERRAIN_DIALOG_HEIGHT_SCALE;
    /**
     * The constant CREATE_TERRAIN_DIALOG_ITERATIONS.
     */
    public static final String CREATE_TERRAIN_DIALOG_ITERATIONS;
    /**
     * The constant CREATE_TERRAIN_DIALOG_FLATTENING.
     */
    public static final String CREATE_TERRAIN_DIALOG_FLATTENING;
    /**
     * The constant CREATE_TERRAIN_DIALOG_MIN_RADIUS.
     */
    public static final String CREATE_TERRAIN_DIALOG_MIN_RADIUS;
    /**
     * The constant CREATE_TERRAIN_DIALOG_MAX_RADIUS.
     */
    public static final String CREATE_TERRAIN_DIALOG_MAX_RADIUS;

    /**
     * The constant CREATE_PARTICLE_EMITTER_POINT_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_POINT_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_PARTICLE_EMITTER_BOX_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_BOX_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_PARTICLE_EMITTER_SPHERE_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_SPHERE_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_PARTICLE_EMITTER_CYLINDER_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_CYLINDER_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_PARTICLE_EMITTER_DOME_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_DOME_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_PARTICLE_EMITTER_QUAD_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_QUAD_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_PARTICLE_EMITTER_TORUS_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_TORUS_SHAPE_DIALOG_TITLE;
    /**
     * The constant CREATE_PARTICLE_EMITTER_TRIANGLE_SHAPE_DIALOG_TITLE.
     */
    public static final String CREATE_PARTICLE_EMITTER_TRIANGLE_SHAPE_DIALOG_TITLE;
    /**
     * The constant EDITING_COMPONENT_BRUSH_SIZE.
     */
    public static final String EDITING_COMPONENT_BRUSH_SIZE;
    /**
     * The constant EDITING_COMPONENT_BRUSH_POWER.
     */
    public static final String EDITING_COMPONENT_BRUSH_POWER;
    /**
     * The constant EDITING_COMPONENT_SMOOTHLY.
     */
    public static final String EDITING_COMPONENT_SMOOTHLY;
    /**
     * The constant EDITING_COMPONENT_LIMITED.
     */
    public static final String EDITING_COMPONENT_LIMITED;
    /**
     * The constant EDITING_COMPONENT_USE_MARKER.
     */
    public static final String EDITING_COMPONENT_USE_MARKER;
    /**
     * The constant EDITING_COMPONENT_LEVEL.
     */
    public static final String EDITING_COMPONENT_LEVEL;
    /**
     * The constant EDITING_COMPONENT_ROUGHNESS.
     */
    public static final String EDITING_COMPONENT_ROUGHNESS;
    /**
     * The constant EDITING_COMPONENT_FREQUENCY.
     */
    public static final String EDITING_COMPONENT_FREQUENCY;
    /**
     * The constant EDITING_COMPONENT_LACUNARITY.
     */
    public static final String EDITING_COMPONENT_LACUNARITY;
    /**
     * The constant EDITING_COMPONENT_OCTAVES.
     */
    public static final String EDITING_COMPONENT_OCTAVES;
    /**
     * The constant EDITING_COMPONENT_SCALE.
     */
    public static final String EDITING_COMPONENT_SCALE;
    /**
     * The constant EDITING_COMPONENT_TRI_PLANAR.
     */
    public static final String EDITING_COMPONENT_TRI_PLANAR;
    /**
     * The constant EDITING_COMPONENT_SHININESS.
     */
    public static final String EDITING_COMPONENT_SHININESS;
    /**
     * The constant EDITING_COMPONENT_LAYER.
     */
    public static final String EDITING_COMPONENT_LAYER;

    /**
     * The constant MODEL_CONVERTER_DIALOG_TITLE.
     */
    public static final String MODEL_CONVERTER_DIALOG_TITLE;
    /**
     * The constant MODEL_CONVERTER_DIALOG_RESULT_NAME.
     */
    public static final String MODEL_CONVERTER_DIALOG_RESULT_NAME;
    /**
     * The constant MODEL_CONVERTER_DIALOG_DESTINATION_FOLDER.
     */
    public static final String MODEL_CONVERTER_DIALOG_DESTINATION_FOLDER;
    /**
     * The constant MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS.
     */
    public static final String MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS;
    /**
     * The constant MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER.
     */
    public static final String MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER;
    /**
     * The constant MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS.
     */
    public static final String MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS;
    /**
     * The constant MODEL_CONVERTER_DIALOG_BUTTON_OK.
     */
    public static final String MODEL_CONVERTER_DIALOG_BUTTON_OK;

    /**
     * The constant FILE_DELETE_HANDLER_DELETE_MATERIALS.
     */
    public static final String FILE_DELETE_HANDLER_DELETE_MATERIALS;

    /**
     * The constant CHECK_NEW_VERSION_DIALOG_TITLE.
     */
    public static final String CHECK_NEW_VERSION_DIALOG_TITLE;
    /**
     * The constant CHECK_NEW_VERSION_DIALOG_HYPERLINK.
     */
    public static final String CHECK_NEW_VERSION_DIALOG_HYPERLINK;
    /**
     * The constant CHECK_NEW_VERSION_DIALOG_HEADER_TEXT.
     */
    public static final String CHECK_NEW_VERSION_DIALOG_HEADER_TEXT;

    /**
     * The constant EDITOR_SCRIPTING_COMPONENT_HEADERS.
     */
    public static final String EDITOR_SCRIPTING_COMPONENT_HEADERS;
    /**
     * The constant EDITOR_SCRIPTING_COMPONENT_BODY.
     */
    public static final String EDITOR_SCRIPTING_COMPONENT_BODY;
    /**
     * The constant EDITOR_SCRIPTING_COMPONENT_RUN.
     */
    public static final String EDITOR_SCRIPTING_COMPONENT_RUN;

    /**
     * The constant ABOUT_DIALOG_TITLE.
     */
    public static final String ABOUT_DIALOG_TITLE;
    /**
     * The constant ABOUT_DIALOG_BUTTON_OK.
     */
    public static final String ABOUT_DIALOG_BUTTON_OK;
    /**
     * The constant ABOUT_DIALOG_VERSION.
     */
    public static final String ABOUT_DIALOG_VERSION;
    /**
     * The constant ABOUT_DIALOG_PROJECT_HOME.
     */
    public static final String ABOUT_DIALOG_PROJECT_HOME;
    /**
     * The constant ABOUT_DIALOG_FORUM_THREAD.
     */
    public static final String ABOUT_DIALOG_FORUM_THREAD;
    /**
     * The constant ABOUT_DIALOG_USED_LIBRARIES.
     */
    public static final String ABOUT_DIALOG_USED_LIBRARIES;
    /**
     * The constant ABOUT_DIALOG_USED_ICONS.
     */
    public static final String ABOUT_DIALOG_USED_ICONS;

    /**
     * The constant RESOURCE_PROPERTY_EDIT_CONTROL_NOTHING_IS_SELECTED.
     */
    public static final String RESOURCE_PROPERTY_EDIT_CONTROL_NOTHING_IS_SELECTED;

    static {

        final Locale locale = Locale.getDefault();
        final ClassLoader classLoader = ControlResources.class.getClassLoader();

        final ResourceBundle controlBundle = getBundle("com/sun/javafx/scene/control/skin/resources/controls",
                locale, classLoader, PropertyLoader.getInstance());

        final ResourceBundle overrideBundle = getBundle("com/sun/javafx/scene/control/skin/resources/controls",
                PropertyLoader.getInstance());

        final Map override = getUnsafeFieldValue(overrideBundle, "lookup");
        final Map original = getUnsafeFieldValue(controlBundle, "lookup");

        //noinspection ConstantConditions,ConstantConditions,unchecked
        original.putAll(override);

        final ResourceBundle bundle = getBundle(BUNDLE_NAME, PropertyLoader.getInstance());

        EDITOR_MENU_FILE = bundle.getString("EditorMenuFile");
        EDITOR_MENU_FILE_OPEN_ASSET = bundle.getString("EditorMenuFileOpenAsset");
        EDITOR_MENU_FILE_OPEN_ASSET_DIRECTORY_CHOOSER = bundle.getString("EditorMenuFileOpenAssetDirectoryChooser");
        EDITOR_MENU_FILE_REOPEN_ASSET_FOLDER = bundle.getString("EditorMenuFileReopenAssetFolder");
        EDITOR_MENU_FILE_EXIT = bundle.getString("EditorMenuFileExit");
        EDITOR_MENU_OTHER = bundle.getString("EditorMenuOther");
        EDITOR_MENU_OTHER_SETTINGS = bundle.getString("EditorMenuOtherSettings");
        EDITOR_MENU_HELP = bundle.getString("EditorMenuHelp");
        EDITOR_MENU_HELP_ABOUT = bundle.getString("EditorMenuHelpAbout");

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

        SCENE_FILE_EDITOR_ACTION_SELECTION = bundle.getString("SceneFileEditorActionSelection");
        SCENE_FILE_EDITOR_ACTION_GRID = bundle.getString("SceneFileEditorActionGrid");
        SCENE_FILE_EDITOR_ACTION_STATISTICS = bundle.getString("SceneFileEditorActionStatistics");
        SCENE_FILE_EDITOR_ACTION_MOVE_TOOL = bundle.getString("SceneFileEditorActionMoveTool");
        SCENE_FILE_EDITOR_ACTION_SCALE_TOOL = bundle.getString("SceneFileEditorActionScaleTool");
        SCENE_FILE_EDITOR_ACTION_ROTATION_TOOL = bundle.getString("SceneFileEditorActionRotationTool");
        SCENE_FILE_EDITOR_ACTION_CAMERA_LIGHT = bundle.getString("SceneFileEditorActionCameraLight");
        SCENE_FILE_EDITOR_ACTION_SHOW_LIGHTS = bundle.getString("SceneFileEditorActionShowLights");
        SCENE_FILE_EDITOR_ACTION_SHOW_AUDIO = bundle.getString("SceneFileEditorActionShowAudio");

        MATERIAL_FILE_EDITOR_ACTION_CUBE = bundle.getString("MaterialFileEditorActionCube");
        MATERIAL_FILE_EDITOR_ACTION_SPHERE = bundle.getString("MaterialFileEditorActionSphere");
        MATERIAL_FILE_EDITOR_ACTION_PLANE = bundle.getString("MaterialFileEditorActionPlane");
        MATERIAL_FILE_EDITOR_ACTION_LIGHT = bundle.getString("MaterialFileEditorActionLight");

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
        MATERIAL_EDITOR_NAME = bundle.getString("MaterialFileEditorName");

        FILE_CREATOR_BUTTON_OK = bundle.getString("FileCreatorButtonOk");
        FILE_CREATOR_FILE_NAME_LABEL = bundle.getString("FileCreatorFileNameLabel");

        MATERIAL_FILE_CREATOR_TITLE = bundle.getString("MaterialFileCreatorTitle");
        MATERIAL_FILE_CREATOR_MATERIAL_TYPE_LABEL = bundle.getString("MaterialFileCreatorMaterialTypeLabel");
        MATERIAL_FILE_CREATOR_FILE_DESCRIPTION = bundle.getString("MaterialFileCreatorFileDescription");

        MATERIAL_DEFINITION_FILE_CREATOR_FILE_DESCRIPTION = bundle.getString("MaterialDefinitionFileCreatorFileDescription");
        MATERIAL_DEFINITION_FILE_CREATOR_TITLE = bundle.getString("MaterialDefinitionFileCreatorTitle");
        MATERIAL_DEFINITION_FILE_CREATOR_GLSL_LABEL = bundle.getString("MaterialDefinitionFileCreatorGlslLabel");

        SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE = bundle.getString("SingleColorTextureFileCreatorTitle");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH = bundle.getString("SingleColorTextureFileCreatorWidth");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT = bundle.getString("SingleColorTextureFileCreatorHeight");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR = bundle.getString("SingleColorTextureFileCreatorColor");
        SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION = bundle.getString("SingleColorTextureFileCreatorDescription");

        SETTINGS_DIALOG_TITLE = bundle.getString("SettingsDialogTitle");
        SETTINGS_DIALOG_FXAA = bundle.getString("SettingsDialogFXAA");
        SETTINGS_DIALOG_STOP_RENDER_ON_LOST_FOCUS = bundle.getString("SettingsDialogStopRenderOnLostFocus");
        SETTINGS_DIALOG_FRAME_RATE = bundle.getString("SettingsDialogFrameRate");
        SETTINGS_DIALOG_GAMMA_CORRECTION = bundle.getString("SettingsDialogGammaCorrection");
        SETTINGS_DIALOG_TONEMAP_FILTER = bundle.getString("SettingsDialogToneMapFilter");
        SETTINGS_DIALOG_TONEMAP_FILTER_WHITE_POINT = bundle.getString("SettingsDialogToneMapFilterWhitePoint");
        SETTINGS_DIALOG_ANISOTROPY = bundle.getString("SettingsDialogAnisotropy");
        SETTINGS_DIALOG_BUTTON_OK = bundle.getString("SettingsDialogButtonOk");
        SETTINGS_DIALOG_BUTTON_CANCEL = bundle.getString("SettingsDialogButtonCancel");
        SETTINGS_DIALOG_MESSAGE = bundle.getString("SettingsDialogMessage");
        SETTINGS_DIALOG_GOOGLE_ANALYTICS = bundle.getString("SettingsDialogAnalytics");
        SETTINGS_DIALOG_CAMERA_ANGLE = bundle.getString("SettingsDialogCameraAngle");
        SETTINGS_DIALOG_AUTO_TANGENT_GENERATING = bundle.getString("SettingsDialogAutoTangentGenerating");
        SETTINGS_DIALOG_DEFAULT_FLIPPED_TEXTURE = bundle.getString("SettingsDialogUseFlipTexture");
        SETTINGS_DIALOG_DEFAULT_EDITOR_CAMERA_LAMP_ENABLED = bundle.getString("SettingsDialogEditorCameraLampEnabled");
        SETTINGS_DIALOG_TAB_GRAPHICS = bundle.getString("SettingsDialogTabGraphics");
        SETTINGS_DIALOG_TAB_OTHER = bundle.getString("SettingsDialogTabOther");
        SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL = bundle.getString("SettingsDialogClasspathFolderLabel");
        SETTINGS_DIALOG_THEME_LABEL = bundle.getString("SettingsDialogThemeLabel");
        SETTINGS_DIALOG_OPEN_GL_LABEL = bundle.getString("SettingsDialogOpenGLLabel");
        SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE = bundle.getString("SettingsDialogClasspathFolderChooserTitle");
        SETTINGS_DIALOG_ENVS_FOLDER_LABEL = bundle.getString("SettingsDialogEnvsFolderLabel");
        SETTINGS_DIALOG_ENVS_FOLDER_CHOOSER_TITLE = bundle.getString("SettingsDialogEnvsFolderChooserTitle");

        BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("BlendToJ3oFileConverterDescription");
        FBX_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("FBXToJ3oFileConverterDescription");
        OBJ_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("ObjToJ3oFileConverterDescription");
        SCENE_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("SceneToJ3oFileConverterDescription");
        MESH_XML_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("MeshXmlToJ3oFileConverterDescription");
        XBUF_TO_J3O_FILE_CONVERTER_DESCRIPTION = bundle.getString("XBufToJ3oFileConverterDescription");

        MODEL_FILE_EDITOR_NAME = bundle.getString("ModelFileEditorName");
        MODEL_FILE_EDITOR_NO_SKY = bundle.getString("ModelFileEditorNoSky");
        MODEL_FILE_EDITOR_FAST_SKY = bundle.getString("ModelFileEditorFastSky");
        MODEL_FILE_EDITOR_NODE_MESH = bundle.getString("ModelFileEditorNodeMesh");
        MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT = bundle.getString("ModelFileEditorNodeAmbientLight");
        MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT = bundle.getString("ModelFileEditorNodeDirectionLight");
        MODEL_FILE_EDITOR_NODE_POINT_LIGHT = bundle.getString("ModelFileEditorNodePointLight");
        MODEL_FILE_EDITOR_NODE_SPOT_LIGHT = bundle.getString("ModelFileEditorNodeSpotLight");
        MODEL_FILE_EDITOR_NODE_LIGHT_PROBE = bundle.getString("ModelFileEditorNodeLightProbe");
        MODEL_FILE_EDITOR_NODE_ANIM_CONTROL = bundle.getString("ModelFileEditorNodeAnimControl");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCERS = bundle.getString("ModelFileEditorNodeParticleEmitterInfluencers");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_EMPTY = bundle.getString("ModelFileEditorNodeParticleEmitterInfluencerEmpty");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_DEFAULT = bundle.getString("ModelFileEditorNodeParticleEmitterInfluencerDefault");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_RADIAL = bundle.getString("ModelFileEditorNodeParticleEmitterInfluencerRadial");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_BOX = bundle.getString("ModelFileEditorNodeParticleEmitterShapeBox");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_SPHERE = bundle.getString("ModelFileEditorNodeParticleEmitterShapeSphere");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_POINT = bundle.getString("ModelFileEditorNodeParticleEmitterShapePoint");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_VERTEX = bundle.getString("ModelFileEditorNodeParticleEmitterShapeMeshVertex");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_FACE = bundle.getString("ModelFileEditorNodeParticleEmitterShapeMeshFace");
        MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_CONVEX_HULL = bundle.getString("ModelFileEditorNodeParticleEmitterShapeMeshConvexHull");
        MODEL_FILE_EDITOR_NODE_STATIC_RIGID_BODY_CONTROL = bundle.getString("ModelFileEditorNodeStaticRigidBodyControl");
        MODEL_FILE_EDITOR_NODE_RIGID_BODY_CONTROL = bundle.getString("ModelFileEditorNodeRigidBodyControl");
        MODEL_FILE_EDITOR_NODE_CHARACTER_CONTROL = bundle.getString("ModelFileEditorNodeCharacterControl");
        MODEL_FILE_EDITOR_NODE_SKELETON_CONTROL = bundle.getString("ModelFileEditorNodeSkeletonControl");
        MODEL_FILE_EDITOR_NODE_VEHICLE_CONTROL = bundle.getString("ModelFileEditorNodeVehicleControl");
        MODEL_FILE_EDITOR_NODE_RAGDOLL_CONTROL = bundle.getString("ModelFileEditorNodeRagDollControl");
        MODEL_FILE_EDITOR_NODE_BOX_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeBoxCollisionShape");
        MODEL_FILE_EDITOR_NODE_CAPSULE_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeCapsuleCollisionShape");
        MODEL_FILE_EDITOR_NODE_CHILD_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeChildCollisionShape");
        MODEL_FILE_EDITOR_NODE_COMPUTED_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeComputedCollisionShape");
        MODEL_FILE_EDITOR_NODE_CONE_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeConeCollisionShape");
        MODEL_FILE_EDITOR_NODE_CYLINDER_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeCylinderCollisionShape");
        MODEL_FILE_EDITOR_NODE_GIMPACT_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeGImpactCollisionShape");
        MODEL_FILE_EDITOR_NODE_HEIGHT_FIELD_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeHeightFieldCollisionShape");
        MODEL_FILE_EDITOR_NODE_HULL_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeHullCollisionShape");
        MODEL_FILE_EDITOR_NODE_MESH_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeMeshCollisionShape");
        MODEL_FILE_EDITOR_NODE_PLANE_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodePlaneCollisionShape");
        MODEL_FILE_EDITOR_NODE_SPHERE_COLLISION_SHAPE = bundle.getString("ModelFileEditorNodeSphereCollisionShape");
        MODEL_FILE_EDITOR_NODE_WHEEL = bundle.getString("ModelFileEditorNodeWheel");
        MODEL_FILE_EDITOR_NODE_MOTION_CONTROL = bundle.getString("ModelFileEditorNodeMotionControl");
        MODEL_FILE_EDITOR_NODE_MOTION_PATH = bundle.getString("ModelFileEditorNodeMotionPath");
        MODEL_FILE_EDITOR_NODE_WAY_POINT = bundle.getString("ModelFileEditorNodeWayPoint");
        MODEL_FILE_EDITOR_NODE_VERTEX_BUFFER = bundle.getString("ModelFileEditorNodeVertexBuffer");

        SCENE_FILE_EDITOR_NAME = bundle.getString("SceneFileEditorName");
        SCENE_FILE_EDITOR_TOOL_OBJECTS = bundle.getString("SceneFileEditorToolObjects");
        SCENE_FILE_EDITOR_TOOL_EDITING = bundle.getString("SceneFileEditorToolEditing");
        SCENE_FILE_EDITOR_TOOL_SCRIPTING = bundle.getString("SceneFileEditorToolScripting");
        SCENE_FILE_EDITOR_TOOL_APP_STATES = bundle.getString("SceneFileEditorToolAppStates");
        SCENE_FILE_EDITOR_TOOL_FILTERS = bundle.getString("SceneFileEditorToolFilters");
        SCENE_FILE_EDITOR_TOOL_LAYERS = bundle.getString("SceneFileEditorToolLayers");

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
        MODEL_NODE_TREE_ACTION_LINK_MODEL = bundle.getString("ModelNodeTreeActionLinkModel");
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
        MODEL_NODE_TREE_ACTION_CREATE_TONEG0D_PARTICLE_EMITTER = bundle.getString("ModelNodeTreeActionCreateToneg0dParticleEmitter");
        MODEL_NODE_TREE_ACTION_CREATE_SOFT_TONEG0D_PARTICLE_EMITTER = bundle.getString("ModelNodeTreeActionCreateSoftToneg0dParticleEmitter");
        MODEL_NODE_TREE_ACTION_CREATE_DEFAULT_PARTICLE_EMITTER = bundle.getString("ModelNodeTreeActionCreateDefaultParticleEmitter");
        MODEL_NODE_TREE_ACTION_RESET_PARTICLE_EMITTERS = bundle.getString("ModelNodeTreeActionResetParticleEmitters");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterChangeShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TRIANGLE_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterTriangleShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_BOX_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterBoxShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_POINT_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterPointShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CYLINDER_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterCylinderShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_DOME_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterDomeShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_QUAD_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterQuadShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_SPHERE_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterSphereShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_VERTEX_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterMeshVertexShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_FACE_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterMeshFaceShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_CONVEX_HULL_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterMeshConvexHullShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_INFLUENCER = bundle.getString("ModelNodeTreeActionParticleEmitterChangeInfluencer");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_DEFAULT = bundle.getString("ModelNodeTreeActionParticleEmitterInfluencerDefault");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_EMPTY = bundle.getString("ModelNodeTreeActionParticleEmitterInfluencerEmpty");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_RADIAL = bundle.getString("ModelNodeTreeActionParticleEmitterInfluencerRadial");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_TORUS_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterTorusShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MODEL_SHAPE = bundle.getString("ModelNodeTreeActionParticleEmitterModelShape");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_PARTICLES_MESH = bundle.getString("ModelNodeTreeActionParticleEmitterChangeParticlesMesh");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_QUAD = bundle.getString("ModelNodeTreeActionParticleEmitterParticlesMeshQuad");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_POINT = bundle.getString("ModelNodeTreeActionParticleEmitterParticlesMeshPoint");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_IMPOSTOR = bundle.getString("ModelNodeTreeActionParticleEmitterParticlesMeshImpostor");
        MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_MODEL = bundle.getString("ModelNodeTreeActionParticleEmitterParticlesMeshModel");
        MODEL_NODE_TREE_ACTION_CREATE_LAYER = bundle.getString("ModelNodeTreeActionCreateLayer");
        MODEL_NODE_TREE_ACTION_ADD_USER_DATA = bundle.getString("ModelNodeTreeActionAddUserData");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL = bundle.getString("ModelNodeTreeActionAddControl");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL_RIGID_BODY = bundle.getString("ModelNodeTreeActionAddControlRigidBody");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL_STATIC_RIGID_BODY = bundle.getString("ModelNodeTreeActionAddControlStaticRigidBody");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL_NOTION = bundle.getString("ModelNodeTreeActionAddControlMotion");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL_CHARACTER = bundle.getString("ModelNodeTreeActionAddControlCharacter");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL_CUSTOM = bundle.getString("ModelNodeTreeActionAddControlCustom");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL_VEHICLE = bundle.getString("ModelNodeTreeActionAddControlVehicle");
        MODEL_NODE_TREE_ACTION_ADD_CONTROL_KINEMATIC_RAGDOLL = bundle.getString("ModelNodeTreeActionAddControlKinematicRagdoll");
        MODEL_NODE_TREE_ACTION_REACTIVATE = bundle.getString("ModelNodeTreeActionReactivate");
        MODEL_NODE_TREE_ACTION_CHANGE_COLLISION_SHAPE = bundle.getString("ModelNodeTreeActionChangeCollisionShape");
        MODEL_NODE_TREE_ACTION_GENERATE_COLLISION_SHAPE = bundle.getString("ModelNodeTreeActionGenerateCollisionShape");
        MODEL_NODE_TREE_ACTION_BOX_COLLISION_SHAPE = bundle.getString("ModelNodeTreeActionBoxCollisionShape");
        MODEL_NODE_TREE_ACTION_CAPSULE_COLLISION_SHAPE = bundle.getString("ModelNodeTreeActionCapsuleCollisionShape");
        MODEL_NODE_TREE_ACTION_CONE_COLLISION_SHAPE = bundle.getString("ModelNodeTreeActionConeCollisionShape");
        MODEL_NODE_TREE_ACTION_CYLINDER_COLLISION_SHAPE = bundle.getString("ModelNodeTreeActionCylinderCollisionShape");
        MODEL_NODE_TREE_ACTION_SPHERE_COLLISION_SHAPE = bundle.getString("ModelNodeTreeActionSphereCollisionShape");
        MODEL_NODE_TREE_ACTION_ADD_WHEEL = bundle.getString("ModelNodeTreeActionAddWheel");
        MODEL_NODE_TREE_ACTION_ADD_TERRAIN = bundle.getString("ModelNodeTreeActionAddTerrain");

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
        MODEL_PROPERTY_IS_LOOPING = bundle.getString("ModelPropertyIsLooping");
        MODEL_PROPERTY_IS_REVERB = bundle.getString("ModelPropertyIsReverb");
        MODEL_PROPERTY_IS_DIRECTIONAL = bundle.getString("ModelPropertyIsDirectional");
        MODEL_PROPERTY_IS_POSITIONAL = bundle.getString("ModelPropertyIsPositional");
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
        MODEL_PROPERTY_LAYER = bundle.getString("ModelPropertyLayer");
        MODEL_PROPERTY_VALUE = bundle.getString("ModelPropertyValue");
        MODEL_PROPERTY_ID = bundle.getString("ModelPropertyId");
        MODEL_PROPERTY_INSTANCE_COUNT = bundle.getString("ModelPropertyInstanceCount");
        MODEL_PROPERTY_VERTEX_COUNT = bundle.getString("ModelPropertyVertexCount");
        MODEL_PROPERTY_NUM_LOD_LEVELS = bundle.getString("ModelPropertyNumLodLevels");
        MODEL_PROPERTY_MODE = bundle.getString("ModelPropertyMode");
        MODEL_PROPERTY_TYPE = bundle.getString("ModelPropertyType");
        MODEL_PROPERTY_FORMAT = bundle.getString("ModelPropertyFormat");
        MODEL_PROPERTY_USAGE = bundle.getString("ModelPropertyUsage");
        MODEL_PROPERTY_UNIQ_ID = bundle.getString("ModelPropertyUniqId");
        MODEL_PROPERTY_BASE_INSTANCE_COUNT = bundle.getString("ModelPropertyBaseInstanceCount");
        MODEL_PROPERTY_INSTANCE_SPAN = bundle.getString("ModelPropertyInstanceSpan");
        MODEL_PROPERTY_NUM_COMPONENTS = bundle.getString("ModelPropertyNumComponents");
        MODEL_PROPERTY_NUM_ELEMENTS = bundle.getString("ModelPropertyNumElements");
        MODEL_PROPERTY_OFFSET = bundle.getString("ModelPropertyOffset");
        MODEL_PROPERTY_STRIDE = bundle.getString("ModelPropertyStride");
        MODEL_PROPERTY_CAPACITY = bundle.getString("ModelPropertyCapacity");
        MODEL_PROPERTY_IS_ENABLED = bundle.getString("ModelPropertyIsEnabled");
        MODEL_PROPERTY_IS_HARDWARE_SKINNING_PREFERRED = bundle.getString("ModelPropertyIsHardwareSkinningPreferred");
        MODEL_PROPERTY_VIEW_DIRECTION = bundle.getString("ModelPropertyViewDirection");
        MODEL_PROPERTY_WALK_DIRECTION = bundle.getString("ModelPropertyWalkDirection");
        MODEL_PROPERTY_FALL_SPEED = bundle.getString("ModelPropertyFallSpeed");
        MODEL_PROPERTY_GRAVITY = bundle.getString("ModelPropertyGravity");
        MODEL_PROPERTY_JUMP_SPEED = bundle.getString("ModelPropertyJumpSpeed");
        MODEL_PROPERTY_MAX_SLOPE = bundle.getString("ModelPropertyMaxSlope");
        MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL = bundle.getString("ModelPropertyIsApplyPhysicsLocal");
        MODEL_PROPERTY_IS_USE_VIEW_DIRECTION = bundle.getString("ModelPropertyIsUseViewDirection");
        MODEL_PROPERTY_IS_KINEMATIC_SPATIAL = bundle.getString("ModelPropertyIsKinematicSpatial");
        MODEL_PROPERTY_IS_KINEMATIC = bundle.getString("ModelPropertyIsKinematic");
        MODEL_PROPERTY_ANGULAR_VELOCITY = bundle.getString("ModelPropertyAngularVelocity");
        MODEL_PROPERTY_LINEAR_FACTOR = bundle.getString("ModelPropertyLinearFactor");
        MODEL_PROPERTY_ANGULAR_DAMPING = bundle.getString("ModelPropertyAngularDamping");
        MODEL_PROPERTY_ANGULAR_FACTOR = bundle.getString("ModelPropertyAngularFactor");
        MODEL_PROPERTY_FRICTION = bundle.getString("ModelPropertyFriction");
        MODEL_PROPERTY_LINEAR_DAMPING = bundle.getString("ModelPropertyLinearDamping");
        MODEL_PROPERTY_MASS = bundle.getString("ModelPropertyMass");
        MODEL_PROPERTY_RESTITUTION = bundle.getString("ModelPropertyRestitution");
        MODEL_PROPERTY_CURRENT_VALUE = bundle.getString("ModelPropertyCurrentValue");
        MODEL_PROPERTY_CURRENT_WAY_POINT = bundle.getString("ModelPropertyCurrentWayPoint");
        MODEL_PROPERTY_DIRECTION_TYPE = bundle.getString("ModelPropertyDirectionType");
        MODEL_PROPERTY_ANGULAR_SLEEPING_THRESHOLD = bundle.getString("ModelPropertyAngularSleepingThreshold");
        MODEL_PROPERTY_LOOP_MODE = bundle.getString("ModelPropertyLoopMode");
        MODEL_PROPERTY_INITIAL_DURATION = bundle.getString("ModelPropertyInitialDuration");
        MODEL_PROPERTY_SPEED = bundle.getString("ModelPropertySpeed");
        MODEL_PROPERTY_TIME = bundle.getString("ModelPropertyTime");
        MODEL_PROPERTY_MARGIN = bundle.getString("ModelPropertyMargin");
        MODEL_PROPERTY_HALF_EXTENTS = bundle.getString("ModelPropertyHalfExtents");
        MODEL_PROPERTY_HEIGHT = bundle.getString("ModelPropertyHeight");
        MODEL_PROPERTY_AXIS = bundle.getString("ModelPropertyAxis");
        MODEL_PROPERTY_OBJECT_ID = bundle.getString("ModelPropertyObjectId");
        MODEL_PROPERTY_AXLE = bundle.getString("ModelPropertyAxle");
        MODEL_PROPERTY_REST_LENGTH = bundle.getString("ModelPropertyRestLength");
        MODEL_PROPERTY_IS_FRONT = bundle.getString("ModelPropertyIsFront");
        MODEL_PROPERTY_DAMPING_COMPRESSION = bundle.getString("ModelPropertyDampingCompression");
        MODEL_PROPERTY_FRICTION_SLIP = bundle.getString("ModelPropertyFrictionSlip");
        MODEL_PROPERTY_MAX_SUSPENSION_FORCE = bundle.getString("ModelPropertyMaxSuspensionForce");
        MODEL_PROPERTY_MAX_SUSPENSION_TRAVEL_CM = bundle.getString("ModelPropertyMaxSuspensionTravelCm");
        MODEL_PROPERTY_DAMPING_RELAXATION = bundle.getString("ModelPropertyDampingRelaxation");
        MODEL_PROPERTY_SUSPENSION_STIFFNESS = bundle.getString("ModelPropertySuspensionStiffness");
        MODEL_PROPERTY_ROLL_INFLUENCE = bundle.getString("ModelPropertyRollInfluence");
        MODEL_PROPERTY_WHEEL_SPATIAL = bundle.getString("ModelPropertyWheelSpatial");
        MODEL_PROPERTY_LENGTH = bundle.getString("ModelPropertyLength");
        MODEL_PROPERTY_CURRENT_TIME = bundle.getString("ModelPropertyCurrentTime");
        MODEL_PROPERTY_POINT = bundle.getString("ModelPropertyPoint");
        MODEL_PROPERTY_CENTER = bundle.getString("ModelPropertyCenter");
        MODEL_PROPERTY_VELOCITY_VARIATION = bundle.getString("ModelPropertyVelocityVariation");
        MODEL_PROPERTY_INITIAL_VELOCITY = bundle.getString("ModelPropertyInitialVelocity");
        MODEL_PROPERTY_ORIGIN = bundle.getString("ModelPropertyOrigin");
        MODEL_PROPERTY_RADIAL_VELOCITY = bundle.getString("ModelPropertyRadialVelocity");
        MODEL_PROPERTY_IS_HORIZONTAL = bundle.getString("ModelPropertyIsHorizontal");
        MODEL_PROPERTY_IS_TEST_MODE = bundle.getString("ModelPropertyIsTestMode");
        MODEL_PROPERTY_IS_RANDOM_POINT = bundle.getString("ModelPropertyIsRandomPoint");
        MODEL_PROPERTY_IS_SEQUENTIAL_FACE = bundle.getString("ModelPropertyIsSequentialFace");
        MODEL_PROPERTY_IS_SKIP_PATTERN = bundle.getString("ModelPropertyIsSkipPattern");
        MODEL_PROPERTY_EMISSION_POINT = bundle.getString("ModelPropertyEmissionPoint");
        MODEL_PROPERTY_MAX_PARTICLES = bundle.getString("ModelPropertyMaxParticles");
        MODEL_PROPERTY_EMISSION_PER_SECOND = bundle.getString("ModelPropertyEmissionPerSecond");
        MODEL_PROPERTY_PARTICLES_PER_SECOND = bundle.getString("ModelPropertyParticlesPerSecond");
        MODEL_PROPERTY_EMITTER_LIFE = bundle.getString("ModelPropertyEmitterLife");
        MODEL_PROPERTY_EMITTER_DELAY = bundle.getString("ModelPropertyEmitterDelay");
        MODEL_PROPERTY_IS_TEST_PARTICLES = bundle.getString("ModelPropertyIsTestParticles");
        MODEL_PROPERTY_IS_FOLLOW_EMITTER = bundle.getString("ModelPropertyIsFollowEmitter");
        MODEL_PROPERTY_STRETCHING = bundle.getString("ModelPropertyStretching");
        MODEL_PROPERTY_MAGNITUDE = bundle.getString("ModelPropertyMagnitude");
        MODEL_PROPERTY_BILLBOARD = bundle.getString("ModelPropertyBillboard");
        MODEL_PROPERTY_INITIAL_FORCE = bundle.getString("ModelPropertyInitialForce");
        MODEL_PROPERTY_LIFE = bundle.getString("ModelPropertyLife");
        MODEL_PROPERTY_COLUMNS = bundle.getString("ModelPropertyColumns");
        MODEL_PROPERTY_ROWS = bundle.getString("ModelPropertyRows");
        MODEL_PROPERTY_SPRITE_COUNT = bundle.getString("ModelPropertySpriteCount");
        MODEL_PROPERTY_IS_FACING_VELOCITY = bundle.getString("ModelPropertyIsFacingVelocity");
        MODEL_PROPERTY_IS_IN_WORLD_SPACE = bundle.getString("ModelPropertyIsInWorldSpace");
        MODEL_PROPERTY_IS_RANDOM_ANGLE = bundle.getString("ModelPropertyIsRandomAngle");
        MODEL_PROPERTY_IS_SELECT_RANDOM_IMAGE = bundle.getString("ModelPropertyIsSelectRandomImage");
        MODEL_PROPERTY_SIZE = bundle.getString("ModelPropertySize");
        MODEL_PROPERTY_ROTATE_SPEED = bundle.getString("ModelPropertyRotateSpeed");
        MODEL_PROPERTY_START_COLOR = bundle.getString("ModelPropertyStartColor");
        MODEL_PROPERTY_END_COLOR = bundle.getString("ModelPropertyEndColor");
        MODEL_PROPERTY_MESH_TYPE = bundle.getString("ModelPropertyMeshType");
        MODEL_PROPERTY_FACE_NORMAL = bundle.getString("ModelPropertyFaceNormal");
        MODEL_PROPERTY_FIXED_DURATION = bundle.getString("ModelPropertyFixedDuration");
        MODEL_PROPERTY_IS_RANDOM_START_COLOR = bundle.getString("ModelPropertyIsRandomStartColor");
        MODEL_PROPERTY_IS_RANDOM_START_SIZE = bundle.getString("ModelPropertyIsRandomStartSize");
        MODEL_PROPERTY_SIZE_VARIATION_FACTOR = bundle.getString("ModelPropertySizeVariationFactor");
        MODEL_PROPERTY_IS_RANDOM_START_DESTINATION = bundle.getString("ModelPropertyIsRandomStartDestination");
        MODEL_PROPERTY_CHANCE = bundle.getString("ModelPropertyChance");
        MODEL_PROPERTY_STRENGTH = bundle.getString("ModelPropertyStrength");
        MODEL_PROPERTY_ALIGNMENT = bundle.getString("ModelPropertyAlignment");
        MODEL_PROPERTY_IS_RANDOM_DIRECTION = bundle.getString("ModelPropertyIsRandomDirection");
        MODEL_PROPERTY_PULL_CENTER = bundle.getString("ModelPropertyPullCenter");
        MODEL_PROPERTY_PULL_ALIGNMENT = bundle.getString("ModelPropertyPullAlignment");
        MODEL_PROPERTY_UP_ALIGNMENT = bundle.getString("ModelPropertyUpAlignment");
        MODEL_PROPERTY_RADIAL_PULL = bundle.getString("ModelPropertyRadialPull");
        MODEL_PROPERTY_TANGENT_FORCE = bundle.getString("ModelPropertyTangetForce");
        MODEL_PROPERTY_ALPHA_INTERPOLATION = bundle.getString("ModelPropertyAlphaInterpolation");
        MODEL_PROPERTY_COLOR_INTERPOLATION = bundle.getString("ModelPropertyColorInterpolation");
        MODEL_PROPERTY_DESTINATION_INTERPOLATION = bundle.getString("ModelPropertyDestinationInterpolation");
        MODEL_PROPERTY_ROTATION_INTERPOLATION = bundle.getString("ModelPropertyRotationInterpolation");
        MODEL_PROPERTY_SIZE_INTERPOLATION = bundle.getString("ModelPropertySizeInterpolation");
        MODEL_PROPERTY_ALPHA = bundle.getString("ModelPropertyInfluencerAlpha");
        MODEL_PROPERTY_FRAME_SEQUENCE = bundle.getString("ModelPropertyFrameSequence");
        MODEL_PROPERTY_IS_RANDOM_START_IMAGE = bundle.getString("ModelPropertyIsRandomStartImage");
        MODEL_PROPERTY_IS_ANIMATE = bundle.getString("ModelPropertyIsAnimate");
        MODEL_PROPERTY_REACTION = bundle.getString("ModelPropertyReaction");
        MODEL_PROPERTY_IS_RANDOM_SPEED = bundle.getString("ModelPropertyIsRandomSpeed");
        MODEL_PROPERTY_IS_START_RANDOM_ROTATION_X = bundle.getString("ModelPropertyIsStartRandomRotationX");
        MODEL_PROPERTY_INTERPOLATION = bundle.getString("ModelPropertyInterpolation");
        MODEL_PROPERTY_GEOMETRY_LIST = bundle.getString("ModelPropertyGeometryList");
        MODEL_PROPERTY_GEOMETRY = bundle.getString("ModelPropertyGeometry");
        MODEL_PROPERTY_AXIS_SAMPLES = bundle.getString("ModelPropertyAxisSamples");
        MODEL_PROPERTY_RADIAL_SAMPLES = bundle.getString("ModelPropertyRadialSamples");
        MODEL_PROPERTY_PLANES = bundle.getString("ModelPropertyPlanes");
        MODEL_PROPERTY_WIDTH = bundle.getString("ModelPropertyWidth");
        MODEL_PROPERTY_FLIP_COORDS = bundle.getString("ModelPropertyFlipCoords");
        MODEL_PROPERTY_Z_SAMPLES = bundle.getString("ModelPropertyZSamples");
        MODEL_PROPERTY_CIRCLE_SAMPLES = bundle.getString("ModelPropertyCircleSamples");
        MODEL_PROPERTY_INNER_RADIUS = bundle.getString("ModelPropertyInnerRadius");
        MODEL_PROPERTY_OUTER_RADIUS = bundle.getString("ModelPropertyOuterRadius");
        MODEL_PROPERTY_NAME = bundle.getString("ModelPropertyName");
        MODEL_PROPERTY_DATA_TYPE = bundle.getString("ModelPropertyDataType");

        MATERIAL_MODEL_PROPERTY_CONTROL_NO_TEXTURE = bundle.getString("MaterialModelPropertyControlNoTexture");
        MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL = bundle.getString("MaterialModelPropertyControlNoMaterial");
        ABSTRACT_ELEMENT_PROPERTY_CONTROL_NO_ELEMENT = bundle.getString("AbstractElementPropertyControlNoElement");
        LAYER_PROPERTY_CONTROL_NO_LAYER = bundle.getString("LayerPropertyControlNoLayer");
        AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO = bundle.getString("AudioKeyPropertyControlNoAudio");
        CHOOSE_FOLDER_CONTROL_NO_FOLDER = bundle.getString("ChooseFolderControlNoFolder");

        RENAME_DIALOG_TITLE = bundle.getString("RenameDialogTitle");
        RENAME_DIALOG_NEW_NAME_LABEL = bundle.getString("RenameDialogNewNameLabel");
        RENAME_DIALOG_BUTTON_OK = bundle.getString("RenameDialogButtonOk");
        RENAME_DIALOG_BUTTON_CANCEL = bundle.getString("RenameDialogButtonCancel");

        PLAY_ANIMATION_SETTINGS_DIALOG_TITLE = bundle.getString("PlayAnimationSettingsDialogTitle");
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

        SIMPLE_DIALOG_BUTTON_OK = bundle.getString("SimpleDialogButtonOk");
        SIMPLE_DIALOG_BUTTON_CANCEL = bundle.getString("SimpleDialogButtonCancel");

        EMPTY_MODEL_CREATOR_DESCRIPTION = bundle.getString("EmptyModelCreatorDescription");
        EMPTY_MODEL_CREATOR_TITLE = bundle.getString("EmptyModelCreatorTitle");

        EMPTY_SCENE_CREATOR_DESCRIPTION = bundle.getString("EmptySceneCreatorDescription");
        DEFAULT_SCENE_CREATOR_DESCRIPTION = bundle.getString("DefaultSceneCreatorDescription");
        EMPTY_SCENE_CREATOR_TITLE = bundle.getString("EmptySceneCreatorTitle");
        DEFAULT_SCENE_CREATOR_TITLE = bundle.getString("DefaultSceneCreatorTitle");

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

        NODE_SELECTOR_DIALOG_TITLE = bundle.getString("NodeSelectorDialogTitle");
        NODE_SELECTOR_DIALOG_BUTTON = bundle.getString("NodeSelectorDialogButton");

        GEOMETRY_SELECTOR_DIALOG_TITLE = bundle.getString("GeometrySelectorDialogTitle");
        LIGHT_SELECTOR_DIALOG_TITLE = bundle.getString("LightSelectorDialogTitle");

        LOG_VIEW_TITLE = bundle.getString("LogViewTitle");

        CREATE_SCENE_APP_STATE_DIALOG_TITLE = bundle.getString("CreateSceneAppStateDialogTitle");
        CREATE_SCENE_APP_STATE_DIALOG_BUILT_IN = bundle.getString("CreateSceneAppStateDialogBuiltIn");
        CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_BOX = bundle.getString("CreateSceneAppStateDialogCustomBox");
        CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_FIELD = bundle.getString("CreateSceneAppStateDialogCustomField");

        CREATE_SCENE_FILTER_DIALOG_TITLE = bundle.getString("CreateSceneFilterDialogTitle");
        CREATE_SCENE_FILTER_DIALOG_BUILT_IN = bundle.getString("CreateSceneFilterDialogBuiltIn");
        CREATE_SCENE_FILTER_DIALOG_CUSTOM_BOX = bundle.getString("CreateSceneFilterDialogCustomBox");
        CREATE_SCENE_FILTER_DIALOG_CUSTOM_FIELD = bundle.getString("CreateSceneFilterDialogCustomField");

        ADD_USER_DATA_DIALOG_TITLE = bundle.getString("AddUserDataDialogTitle");
        ADD_USER_DATA_DIALOG_BUTTON_OK = bundle.getString("AddUserDataDialogButtonOk");

        CREATE_CUSTOM_CONTROL_DIALOG_TITLE = bundle.getString("CreateCustomControlDialogTitle");
        CREATE_CUSTOM_CONTROL_DIALOG_BUILT_IN = bundle.getString("CreateCustomControlDialogBuiltIn");
        CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_BOX = bundle.getString("CreateCustomControlDialogCustomBox");
        CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_FIELD = bundle.getString("CreateCustomControlDialogCustomField");

        ANALYTICS_CONFIRM_DIALOG_MESSAGE = bundle.getString("AnalyticsConfirmDialogMessage");

        CREATE_BOX_COLLISION_SHAPE_DIALOG_TITLE = bundle.getString("CreateBoxCollisionShapeDialogTitle");
        CREATE_SPHERE_COLLISION_SHAPE_DIALOG_TITLE = bundle.getString("CreateSphereCollisionShapeDialogTitle");
        CREATE_CYLINDER_COLLISION_SHAPE_DIALOG_TITLE = bundle.getString("CreateCylinderCollisionShapeDialogTitle");
        CREATE_CONE_COLLISION_SHAPE_DIALOG_TITLE = bundle.getString("CreateConeCollisionShapeDialogTitle");
        CREATE_CAPSULE_COLLISION_SHAPE_DIALOG_TITLE = bundle.getString("CreateCapsuleCollisionShapeDialogTitle");

        ADD_VEHICLE_WHEEL_DIALOG_TITLE = bundle.getString("AddVehicleWheelDialogTitle");

        CREATE_TERRAIN_DIALOG_TITLE = bundle.getString("CreateTerrainDialogTitle");
        CREATE_TERRAIN_DIALOG_BASE_TEXTURE = bundle.getString("CreateTerrainDialogBaseTexture");
        CREATE_TERRAIN_DIALOG_FOLDER_ALPHA_TEXTURE = bundle.getString("CreateTerrainDialogFolderAlphaTexture");
        CREATE_TERRAIN_DIALOG_TOTAL_SIZE = bundle.getString("CreateTerrainDialogTotalSize");
        CREATE_TERRAIN_DIALOG_PATCH_SIZE = bundle.getString("CreateTerrainDialogPatchSize");
        CREATE_TERRAIN_DIALOG_ALPHA_BLEND_TEXTURE_SIZE = bundle.getString("CreateTerrainDialogAlphaBlendTextureSize");
        CREATE_TERRAIN_DIALOG_TERRAIN_TYPE = bundle.getString("CreateTerrainDialogTerrainType");
        CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_FLAT = bundle.getString("CreateTerrainDialogTerrainTypeFlat");
        CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_IMAGE_BASED = bundle.getString("CreateTerrainDialogTerrainTypeImageBased");
        CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_HILL = bundle.getString("CreateTerrainDialogTerrainTypeHill");
        CREATE_TERRAIN_DIALOG_HEIGHT_MAP_IMAGE = bundle.getString("CreateTerrainDialogHeightMapImage");
        CREATE_TERRAIN_DIALOG_HEIGHT_SMOOTH = bundle.getString("CreateTerrainDialogHeightSmooth");
        CREATE_TERRAIN_DIALOG_HEIGHT_SCALE = bundle.getString("CreateTerrainDialogHeightScale");
        CREATE_TERRAIN_DIALOG_ITERATIONS = bundle.getString("CreateTerrainDialogIterations");
        CREATE_TERRAIN_DIALOG_FLATTENING = bundle.getString("CreateTerrainDialogFlattening");
        CREATE_TERRAIN_DIALOG_MIN_RADIUS = bundle.getString("CreateTerrainDialogMinRadius");
        CREATE_TERRAIN_DIALOG_MAX_RADIUS = bundle.getString("CreateTerrainDialogMaxRadius");

        CREATE_PARTICLE_EMITTER_POINT_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterPointShapeDialogTitle");
        CREATE_PARTICLE_EMITTER_BOX_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterBoxShapeDialogTitle");
        CREATE_PARTICLE_EMITTER_SPHERE_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterSphereShapeDialogTitle");
        CREATE_PARTICLE_EMITTER_CYLINDER_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterSphereShapeDialogTitle");
        CREATE_PARTICLE_EMITTER_DOME_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterDomeShapeDialogTitle");
        CREATE_PARTICLE_EMITTER_QUAD_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterQuadShapeDialogTitle");
        CREATE_PARTICLE_EMITTER_TORUS_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterTorusShapeDialogTitle");
        CREATE_PARTICLE_EMITTER_TRIANGLE_SHAPE_DIALOG_TITLE = bundle.getString("CreateParticleEmitterTriangleShapeDialogTitle");

        EDITING_COMPONENT_BRUSH_SIZE = bundle.getString("EditingComponentBrushSize");
        EDITING_COMPONENT_BRUSH_POWER = bundle.getString("EditingComponentBrushPower");
        EDITING_COMPONENT_SMOOTHLY = bundle.getString("EditingComponentSmoothly");
        EDITING_COMPONENT_LIMITED = bundle.getString("EditingComponentLimited");
        EDITING_COMPONENT_USE_MARKER = bundle.getString("EditingComponentUseMarker");
        EDITING_COMPONENT_LEVEL = bundle.getString("EditingComponentLevel");
        EDITING_COMPONENT_ROUGHNESS = bundle.getString("EditingComponentRoughness");
        EDITING_COMPONENT_FREQUENCY = bundle.getString("EditingComponentFrequency");
        EDITING_COMPONENT_LACUNARITY = bundle.getString("EditingComponentLacunarity");
        EDITING_COMPONENT_OCTAVES = bundle.getString("EditingComponentOctaves");
        EDITING_COMPONENT_SCALE = bundle.getString("EditingComponentScale");
        EDITING_COMPONENT_TRI_PLANAR = bundle.getString("EditingComponentTriPlanar");
        EDITING_COMPONENT_SHININESS = bundle.getString("EditingComponentShininess");
        EDITING_COMPONENT_LAYER = bundle.getString("EditingComponentLayer");

        MODEL_CONVERTER_DIALOG_TITLE = bundle.getString("ModelConverterDialogTitle");
        MODEL_CONVERTER_DIALOG_RESULT_NAME = bundle.getString("ModelConverterDialogResultName");
        MODEL_CONVERTER_DIALOG_DESTINATION_FOLDER = bundle.getString("ModelConverterDialogDestinationFolder");
        MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS = bundle.getString("ModelConverterDialogExportMaterials");
        MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER = bundle.getString("ModelConverterDialogMaterialsFolder");
        MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS = bundle.getString("ModelConverterDialogOverwriteMaterials");
        MODEL_CONVERTER_DIALOG_BUTTON_OK = bundle.getString("ModelConverterDialogButtonOk");

        FILE_DELETE_HANDLER_DELETE_MATERIALS = bundle.getString("FileDeleteHandlerDeleteMaterials");

        CHECK_NEW_VERSION_DIALOG_TITLE = bundle.getString("CheckNewVersionDialogTitle");
        CHECK_NEW_VERSION_DIALOG_HYPERLINK = bundle.getString("CheckNewVersionDialogHyperText");
        CHECK_NEW_VERSION_DIALOG_HEADER_TEXT = bundle.getString("CheckNewVersionDialogHeaderText");

        EDITOR_SCRIPTING_COMPONENT_HEADERS = bundle.getString("EditorScriptingComponentHeaders");
        EDITOR_SCRIPTING_COMPONENT_BODY = bundle.getString("EditorScriptingComponentBody");
        EDITOR_SCRIPTING_COMPONENT_RUN = bundle.getString("EditorScriptingComponentRun");

        ABOUT_DIALOG_TITLE = bundle.getString("AboutDialogTitle");
        ABOUT_DIALOG_VERSION = bundle.getString("AboutDialogVersion");
        ABOUT_DIALOG_PROJECT_HOME = bundle.getString("AboutDialogProjectHome");
        ABOUT_DIALOG_FORUM_THREAD = bundle.getString("AboutDialogForumThread");
        ABOUT_DIALOG_USED_LIBRARIES = bundle.getString("AboutDialogUsedLibraries");
        ABOUT_DIALOG_USED_ICONS = bundle.getString("AboutDialogUsedIcons");
        ABOUT_DIALOG_BUTTON_OK = bundle.getString("AboutDialogButtonOk");

        RESOURCE_PROPERTY_EDIT_CONTROL_NOTHING_IS_SELECTED = bundle.getString("ResourcePropertyEditControlNothingIsSelected");
    }
}