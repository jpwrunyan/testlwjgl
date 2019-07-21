package graphics;

import graphics.model.Material;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private final int vaoId;
    private final int posVboId;
    private final int colorTextureVboId;
    private final int normalsVboId;
    private final int indicesVboId;
    private final int vertexCount;

    //private Texture texture;
    private Material material;

    private Vector3f color = new Vector3f(1, 1, 1);

    /**
     * Constructor for using textures.
     *
     * @param positions
     * @param indices
     * @param textCoords
     * @param normals
     */
    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        vertexCount = indices.length;
        //this.texture = texture;

        //Create the VBA.
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        //Create the position VBO.
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
        verticesBuffer.put(positions).flip();
        posVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, posVboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, verticesBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        //Unbind the VBO.
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(verticesBuffer);

        //Create the textures VBO.
        FloatBuffer textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
        textCoordsBuffer.put(textCoords).flip();
        colorTextureVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, colorTextureVboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, textCoordsBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
        //We don't unbind the VBO here either?
        MemoryUtil.memFree(textCoordsBuffer);

        //Create vertex normals VBO.
        FloatBuffer normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalsBuffer.put(normals).flip();
        normalsVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, normalsVboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, normalsBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(2, 3, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(normalsBuffer);

        //Create the indices VBO.
        //This is different from the others because it doesn't use a vertex attribute pointer.
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        indicesVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL30.GL_STATIC_DRAW);
        //We don't have to attribute the pointer?
        //Unbind the VBO...?
        //GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(indicesBuffer);

        //Unbind the last VBO...?
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        //Unbind the VAO.
        GL30.glBindVertexArray(0);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void render() {
        if (material.hasTexture() == 1) {
            // Activate first texture unit
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            // Bind the texture
            //GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture.id);
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, material.texture.id);
        }

        //Draw the mesh.
        GL30.glBindVertexArray(vaoId);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);

        GL30.glDrawElements(GL30.GL_TRIANGLES, vertexCount, GL30.GL_UNSIGNED_INT, 0);

        //Restore state.
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);

        //Unbind?
        //GL30.glActiveTexture(0);

    }

    @Deprecated
    public int getVaoId() {
        return vaoId;
    }

    @Deprecated
    public int getVertexCount() {
        return vertexCount;
    }


    public void cleanup() {
        GL30.glDisableVertexAttribArray(0);

        //Delete VBO.
        //Unbinding is probably redundant.
        //GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(posVboId);
        GL30.glDeleteBuffers(colorTextureVboId);
        GL30.glDeleteBuffers(normalsVboId);
        GL30.glDeleteBuffers(indicesVboId);

        //GL30.glDeleteBuffers(colorTextureVboId);
        if (material.hasTexture() == 1) {
            material.texture.cleanup();
        }

        //Delete the VAO.
        //Unbinding is probably redundant.
        //GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }
}
