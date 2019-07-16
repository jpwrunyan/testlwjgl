package graphics.model;

import graphics.Texture;
import org.joml.Vector4f;

public class Material {

    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public Vector4f ambient;
    public Vector4f diffuse;
    public Vector4f specular;
    public float reflectance;

    public final Texture texture;

    public Material(Texture texture, float reflectance) {
        this.texture = texture;
        ambient = DEFAULT_COLOUR;
        diffuse = DEFAULT_COLOUR;
        specular = DEFAULT_COLOUR;
        this.reflectance = reflectance;
    }

    /**
     * @return 1 if textured, 0 if not
     */
    public int hasTexture() {
        return this.texture != null ? 1 : 0;
    }
}
