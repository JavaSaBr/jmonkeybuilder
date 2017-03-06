package com.ss.editor.ui;

import com.ss.editor.manager.FileIconManager;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * The interface with all icons of this application.
 *
 * @author JavaSaBr
 */
public interface Icons {

    @NotNull
    FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    Image REMOVE_12 = ICON_MANAGER.getImage("/ui/icons/actions/svg/horizontal-line-remove-button.svg", 12, false);
    Image ADD_12 = ICON_MANAGER.getImage("/ui/icons/actions/svg/add-plus-button.svg", 12, false);

    Image SAVE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/save.png", 16);
    Image SCALE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/scale.png", 16);
    Image ROTATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/rotation.png", 16);
    Image CUBE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/hollow-cube.svg");
    Image MOVE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/move.png", 16);
    Image LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/idea.svg");
    Image INFLUENCER_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/influencer.png");
    Image SPHERE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/planet-sphere.svg");
    Image PLANE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/plane.png");
    Image NODE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/node.png");
    Image PARTICLES_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/particles.png");
    Image PARTICLE_GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/particle-geometry.png");
    Image GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/cube-divisions.svg");
    Image MESH_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/grid.svg");
    Image EDIT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/edit.png");
    Image AMBIENT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/ambient.png");
    Image LAMP_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/lamp.png");
    Image POINT_LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/point.png");
    Image SUN_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sun.png");
    Image PLAY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/play.png");
    Image STOP_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/stop.png");
    Image ANIMATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/animation.png");
    Image ANI_CHANNEL_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/anim-channel.png");
    Image GEAR_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/gear.png");
    Image BONE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/bone.png");
    Image AUDIO_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/audio-volume.svg");
    Image SETTINGS_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/settings.png");
    Image PASTE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/paste.png");
    Image VIEW_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/view.png");
    Image NEW_FILE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/new_file.png");
    Image CUT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/cut.png");
    Image COPY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/copy.png");
    Image TRANSFORMATION_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/transformation.png");
    Image EXTRACT_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/extract.png");
    Image GENERATE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/generate_16.png");
    Image SCENE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/scene.png");
    Image LAYERS_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/layers.png");
    Image OPEN_FILE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/open-folder-with-document.svg");
    Image BACKGROUND_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/background.png");
    Image EMITTER_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/emitter.png");
    Image SKY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sky.png");
    Image INVISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/invisible.png");
    Image VISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/visible.png");
    Image STATIC_RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/static-rigid-body.png");
    Image RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/rigid-body.png");
    Image REPLAY_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/reply.png");
    Image CHARACTER_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/character.png");
    Image SKELETON_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/skeleton.png");
    Image VEHICLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/vehicle.png");
    Image ATOM_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/atom.png");
    Image PHYSICS_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/physics.png");
    Image DOLL_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/doll.png");
    Image CAPSULE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/capsule.png");
    Image CONE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/cone.png");
    Image CYLINDER_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/cylinder.svg");
    Image TERRAIN_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/terrain.svg");
    Image WHEEL_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/wheel.svg");
    Image TRIANGLE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/triangle.svg");
    Image DOME_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/reichstag-dome.svg");
    Image QUAD_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/basic-square.svg");
    Image RHOMB_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/rhombus.svg");
    Image TORUS_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/circle.svg");
    Image POINTS_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/because-mathematical-symbol.svg");
    Image IMPOSTOR_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/plus.svg");
    Image REMOVE_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/horizontal-line-remove-button.svg", 16, false);
    Image ADD_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/add-plus-button.svg", 16, false);
    Image MOTION_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/horse-in-running-motion-silhouette.svg", 16, false);
    Image PATH_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/map-location.svg", 16, false);
    Image WAY_POINT_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/placeholder.svg", 16, false);
    Image VERTEX_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/graphene.svg", 16, false);
    Image DATA_16 = ICON_MANAGER.getImage("/ui/icons/actions/svg/database.svg", 16, false);

    Image REFRESH_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/refresh.png", 18);
    Image CLOSE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/close.png", 18);

    Image SAVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/save.png", 24);
    Image ADD_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add.png", 24);
    Image LIGHT_24 = ICON_MANAGER.getImage("/ui/icons/actions/svg/idea.svg", 24);
    Image ROTATION_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/rotation.png", 24);
    Image MOVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/move.png", 24);
    Image SCALE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/scale.png", 24);
    Image CUBE_24 = ICON_MANAGER.getImage("/ui/icons/actions/svg/hollow-cube.svg", 24);
    Image SPHERE_24 = ICON_MANAGER.getImage("/ui/icons/actions/svg/planet-sphere.svg", 24);
    Image PLANE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/plane.png", 24);
    Image ADD_CIRCLE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add_circle.png", 24);
    Image IMAGE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/image.png", 24);
    Image WARNING_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/warning.png", 24);
    Image EDIT_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/edit.png", 24);
    Image FROM_FULLSCREEN_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/from_fullscreen.png", 24);
    Image TO_FULLSCREEN_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/to_fullscreen.png", 24);

    Image TERRAIN_LEVEL_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/level_terrain.svg", 32);
    Image TERRAIN_PAINT_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/paint_terrain.svg", 32);
    Image TERRAIN_ROUGH_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/rough_terrain.svg", 32);
    Image TERRAIN_SLOPE_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/slope_terrain.svg", 32);
    Image TERRAIN_SMOOTH_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/smooth_terrain.svg", 32);
    Image TERRAIN_UP_32 = ICON_MANAGER.getImage("/ui/icons/actions/svg/raise_terrain.svg", 32);

    Image PLAY_128 = ICON_MANAGER.getImage("/ui/icons/actions/128/play.png", 128);
    Image PAUSE_128 = ICON_MANAGER.getImage("/ui/icons/actions/128/pause.png", 128);
    Image STOP_128 = ICON_MANAGER.getImage("/ui/icons/actions/128/stop.png", 128);

    Image IMAGE_512 = ICON_MANAGER.getImage("/ui/icons/512/image.png", 512);
}
