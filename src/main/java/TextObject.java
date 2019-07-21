import display.DisplayObject;
import graphics.Mesh;
import graphics.Texture;
import graphics.model.Material;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextObject extends DisplayObject {
    private static final float ZPOS = 0.0f;
    private static final int VERTICES_PER_QUAD = 4;
    private String text;
    private final int numCols;
    private final int numRows;

    public TextObject(String text, String fontFileName, int numCols, int numRows) throws Exception {
        super(buildMesh(text, fontFileName, numCols, numRows));
        this.numCols = numCols;
        this.numRows = numRows;
    }

    private static Mesh buildMesh(String text, String fontFileName, int numCols, int numRows) throws Exception {
        Texture texture = new Texture(fontFileName);

        byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
        int numChars = chars.length;

        List<Float> positions = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        float[] normals = new float[0];
        List<Integer> indices = new ArrayList<>();

        float tileWidth = texture.width / numCols;
        float tileHeight = texture.height / numRows;

        for (int i = 0; i < numChars; i++) {
            byte currentChar = chars[i];
            int col = currentChar % numCols;
            int row = currentChar / numCols;

            //Build a character tile composed by two triangles.

            //Top left vertex
            positions.add(i * tileWidth); //x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textureCoords.add((float) col / numCols);
            textureCoords.add((float) row / numRows);
            indices.add(i * VERTICES_PER_QUAD);

            //Bottom left vertex
            positions.add(i * tileWidth);
            positions.add(tileHeight);
            positions.add(ZPOS);
            textureCoords.add((float) col / numCols);
            textureCoords.add((row + 1f) / numRows);
            indices.add(i * VERTICES_PER_QUAD + 1);

            //Bottom right vertex
            positions.add(i * tileWidth + tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(ZPOS); //z
            textureCoords.add((col + 1f) / numCols );
            textureCoords.add((row + 1f) / numRows );
            indices.add(i * VERTICES_PER_QUAD + 2);

            //Top Right vertex
            positions.add(i * tileWidth + tileWidth); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textureCoords.add((col + 1f) / numCols);
            textureCoords.add((float) row / numRows);
            indices.add(i * VERTICES_PER_QUAD + 3);

            //Add indices for top left and bottom right vertices.
            indices.add(i * VERTICES_PER_QUAD);
            indices.add(i * VERTICES_PER_QUAD + 2);
        }
        Mesh mesh = new Mesh(
            mapToFloat(positions),
            mapToFloat(textureCoords),
            normals,
            indices.stream().mapToInt(Integer::intValue).toArray()
        );
        mesh.setMaterial(new Material(texture, 1));
        //float[] positions, float[] textCoords, float[] normals, int[] indices
        //String[] strings = list.stream().toArray(String[]::new);
        //int[] l = positions.stream().(Float::floatValue).toArray();
        return mesh;

    }

    private static float[] mapToFloat(List<Float> floatList) {
        int n = floatList.size();
        float[] floatArray = new float[n];
        for (int i = 0; i < n; i++) {
            floatArray[i] = (floatList.get(i));
        }
        return floatArray;
    }
}
