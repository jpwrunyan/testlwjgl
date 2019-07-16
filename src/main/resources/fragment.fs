#version 330

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 color;
    //Light position is assumed to be in view coordinates.
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};
/*
in vec3 color;
*/
in vec2 fragTextureCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
out vec4 fragColor;

uniform sampler2D texture_sampler;

//The color that will affect every fragment the same way.
uniform vec3 ambientLight;
//The exponent used in the equation for specular light.
uniform float specularPower;
//The material characteristics
uniform Material material;
//A point light
uniform PointLight pointLight;
//The camera position in view space coordinates
uniform vec3 camera_pos;
/*
uniform vec3 color;
uniform int useColorFlag;
*/

/*
These are global variables that will hold the material color components used in the ambeint, duffse, and specular components.
If the component has a texture, we will use the same clor for all the components and we do not want to perform redundant texutre lookups.
*/
vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textCoord) {
    if (material.hasTexture == 1) {
        ambientC = texture(texture_sampler, textCoord);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

/*
This function takes a point light, vertext posititon and normal then
returns the color contribution calculated for the diffuse and specular lgith components/
*/
vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColor = vec4(0, 0, 0, 0);

    //Diffuse light
    vec3 light_direction = light.position - position;
    vec3 to_light_source = normalize(light_direction);
    float diffuseFactor = max(dot(normal, to_light_source), 0.0);
    diffuseColor = diffuseC * vec4(light.color, 1.0) * light.intensity * diffuseFactor;

    //Specular light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_source = -to_light_source;
    vec3 reflected_light = normalize(reflect(from_light_source, normal));
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularC * specularFactor * material.reflectance * vec4(light.color, 1.0);

    //Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.exponent * distance * distance;
    return (diffuseColor + specColor) / attenuationInv;
}

void main() {
    setupColors(material, fragTextureCoord);
    vec4 diffuseSpecularComp = calcPointLight(pointLight, mvVertexPos, mvVertexNormal);
    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
    /*
    if (useColorFlag == 1) {
        fragColor = vec4(color, 1.0);
    } else {
        fragColor = texture(texture_sampler, fragTextureCoord);
    }
    */
}