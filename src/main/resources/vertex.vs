/* Shaders are written by using the GLSL language (OpenGL Shading Language) which is based on ANSI C */

/* The version of the GLSL language we are using */
#version 330

/* Specifies the input format for this shader. */
layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;

out vec2 fragTextureCoord;

/* disabled
layout (location=1) in vec3 inColor;
out vec3 color;
*/


/* Get a variable set in LWJGL */
uniform mat4 projectionMatrix;
/*
uniform mat4 worldMatrix;
*/
uniform mat4 modelViewMatrix;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    /*
    color = inColor;
    */
    fragTextureCoord = textureCoord;
}