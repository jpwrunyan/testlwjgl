package graphics.model;

import org.joml.Vector3f;

public class DirectionalLight {
    public Vector3f color;
    public Vector3f direction;
    public float intensity;

    public DirectionalLight() {
        color = new Vector3f(0, 0, 0);
        direction = new Vector3f(0, 0, 0);
        intensity = 1;
    }

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    /**
     * Copy constructor
     * @param light
     */
    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.color), new Vector3f(light.direction), light.intensity);
    }
}
