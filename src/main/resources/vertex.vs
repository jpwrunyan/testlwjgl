//Verified works

/* Shaders are written by using the GLSL language (OpenGL Shading Language) which is based on ANSI C */

/* The version of the GLSL language we are using */
#version 330

/* Specifies the input format for this shader. */
layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 fragTextureCoord;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main() {
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);
    //gl_Position is a reservered word. Don't misspell it!
    gl_Position = projectionMatrix * mvPos;
    mvVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    mvVertexPos = mvPos.xyz;
    fragTextureCoord = textureCoord;
}