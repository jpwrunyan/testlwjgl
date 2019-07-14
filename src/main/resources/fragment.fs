#version 330

/*
in vec3 color;
*/
in vec2 fragTextureCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main() {
    /*
    fragColor = vec4(color, 1.0);
    */
    fragColor = texture(texture_sampler, fragTextureCoord);
}