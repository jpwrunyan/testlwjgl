#version 330

/* Vertex shader for 2d UI display */

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 fragTextureCoord;

uniform mat4 projModelMatrix;

void main() {
    gl_Position = projModelMatrix * vec4(position, 1.0);
    fragTextureCoord = textureCoord;
}