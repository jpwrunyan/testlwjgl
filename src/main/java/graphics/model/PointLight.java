package graphics.model;
//Verified
import org.joml.Vector3f;

public class PointLight {
    public Vector3f color;
    public Vector3f position;
    public float intensity;
    public Attenuation attenuation;

    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.attenuation = attenuation;
    }

    public PointLight(PointLight pointLight) {
        this(
                new Vector3f(pointLight.color),
                new Vector3f(pointLight.position),
                pointLight.intensity,
                pointLight.attenuation
        );
    }
}