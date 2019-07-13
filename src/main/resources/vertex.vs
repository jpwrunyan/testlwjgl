/* Shaders are written by using the GLSL language (OpenGL Shading Language) which is based on ANSI C */

/* The version of the GLSL language we are using */
#version 330

/* Specifies the input format for this shader. */
layout (location=0) in vec3 position;
layout (location=1) in vec3 inColor;

out vec3 color;

void main() {
    gl_Position = vec4(position, 1.0);
    color = inColor;
}