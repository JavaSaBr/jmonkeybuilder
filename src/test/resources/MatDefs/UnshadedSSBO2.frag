#import "Common/ShaderLib/GLSLCompat.glsllib"

#if defined(HAS_GLOWMAP) || defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

#if defined(DISCARD_ALPHA)
    uniform float m_AlphaDiscardThreshold;
#endif

uniform vec4 m_Color;
uniform sampler2D m_ColorMap;
uniform sampler2D m_LightMap;

layout (std140, binding = 4) buffer m_TestSSBO
{
  int index;
  vec4 colors[3];
  float alp;
  mat3 matrix3;
  mat4 matrix4;
  vec3 positions[3];
  int index2;
  mat3 matrixes[2];
  vec2 vector2;
  float fvalue;
};

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;


void main(){

    vec4 color = vec4(1.0);

    #ifdef HAS_COLORMAP
        color *= texture2D(m_ColorMap, texCoord1);     
    #endif

    #ifdef HAS_VERTEXCOLOR
        color *= vertColor;
    #endif

    #ifdef HAS_COLOR
        color *= m_Color;
    #endif

    #ifdef HAS_LIGHTMAP
        #ifdef SEPARATE_TEXCOORD
            color.rgb *= texture2D(m_LightMap, texCoord2).rgb;
        #else
            color.rgb *= texture2D(m_LightMap, texCoord1).rgb;
        #endif
    #endif

    #if defined(DISCARD_ALPHA)
        if(color.a < m_AlphaDiscardThreshold){
           discard;
        }
    #endif

    float m00 =  matrix3[0][0];
    float m01 =  matrix3[0][1];
    float m02 =  matrix3[0][2];

    float m10 =  matrix3[1][0];
    float m11 =  matrix3[1][1];
    float m12 =  matrix3[1][2];

    float m20 =  matrix3[2][0];
    float m21 =  matrix3[2][1];
    float m22 =  matrix3[2][2];

   // gl_FragColor = vec4(positions[index2], 1.0) * alp;
   // gl_FragColor = vec4(m20, m21, m22, 1.0) * alp;
   // gl_FragColor = vec4(matrixes[1][1][0], matrixes[1][1][1], matrixes[1][1][2], 1.0) * alp;
    gl_FragColor = vec4(vector2.x, vector2.y, fvalue, 1.0) * alp;
}