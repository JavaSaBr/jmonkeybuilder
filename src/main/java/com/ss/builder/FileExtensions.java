package com.ss.builder;

import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The list of all file extensions.
 *
 * @author JavaSaBr
 */
public interface FileExtensions {

    @NotNull String JME_MATERIAL = "j3m";
    @NotNull String JME_MATERIAL_DEFINITION = "j3md";
    @NotNull String JME_SHADER_NODE = "j3sn";
    @NotNull String JME_OBJECT = "j3o";
    @NotNull String JME_SCENE = "j3s";

    @NotNull String JAVA_LIBRARY = "jar";
    @NotNull String JAVA_CLASS = "class";

    @NotNull String IMAGE_PNG = "png";
    @NotNull String IMAGE_JPG = "jpg";
    @NotNull String IMAGE_JPEG = "jpeg";
    @NotNull String IMAGE_GIF = "gif";
    @NotNull String IMAGE_TGA = "tga";
    @NotNull String IMAGE_BMP = "bmp";
    @NotNull String IMAGE_TIFF = "tiff";
    @NotNull String IMAGE_DDS = "dds";
    @NotNull String IMAGE_HDR = "hdr";

    @NotNull String AUDIO_MP3 = "mp3";
    @NotNull String AUDIO_OGG = "ogg";
    @NotNull String AUDIO_WAV = "wav";

    @NotNull String GLSL_VERTEX = "vert";
    @NotNull String GLSL_FRAGMENT = "frag";
    @NotNull String GLSL_TESSELLATION_CONTROL = "tsctrl";
    @NotNull String GLSL_TESSELLATION_EVALUATION = "tseval";
    @NotNull String GLSL_GEOM = "geom";
    @NotNull String GLSL_LIB = "glsllib";

    @NotNull String MODEL_BLENDER = "blend";
    @NotNull String MODEL_FBX = "fbx";
    @NotNull String MODEL_GLTF = "gltf";
    @NotNull String MODEL_OBJ = "obj";
    @NotNull String MODEL_SCENE = "scene";
    @NotNull String MODEL_MESH_XML = "mesh.xml";
    @NotNull String MODEL_XBUF = "xbuf";
    
    @NotNull Array<String> IMAGE_EXTENSIONS = Array.of(
            IMAGE_PNG,
            IMAGE_JPG,
            IMAGE_JPEG,
            IMAGE_TGA,
            IMAGE_DDS,
            IMAGE_HDR,
            IMAGE_BMP,
            IMAGE_GIF,
            IMAGE_TIFF
    );

    @NotNull Array<String> TEXTURE_EXTENSIONS = Array.of(
            IMAGE_PNG,
            IMAGE_JPG,
            IMAGE_JPEG,
            IMAGE_TGA,
            IMAGE_DDS,
            IMAGE_HDR
    );

    @NotNull Array<String> SHADER_EXTENSIONS = Array.of(
            GLSL_FRAGMENT,
            GLSL_VERTEX,
            GLSL_TESSELLATION_CONTROL,
            GLSL_TESSELLATION_EVALUATION,
            GLSL_GEOM,
            GLSL_LIB
    );

    @NotNull Array<String> AUDIO_EXTENSIONS = Array.of(AUDIO_MP3, AUDIO_WAV, AUDIO_OGG);
}