package graphics.model;

//Verified

/**
 * We can calculate the attenuation factor with this formula:
 * 1.0 / (constant + linear * dist + exponent * dist^2)
 */
public class Attenuation {
    public float constant;
    public float linear;
    public float exponent;
}
