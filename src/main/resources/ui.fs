#version 330

/* Fragment shader for 2d UI display */

in vec2 fragTextureCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec4 color;
uniform int hasTexture;

void main() {
    if (hasTexture == 1) {
        fragColor = color * texture(texture_sampler, fragTextureCoord);
    } else {
        fragColor = color;
    }
}