package utils;

import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {
    public static Mesh loadMesh(String filename) throws Exception {
        List<String> lines = readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    //Geometric vertex
                    vertices.add(new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    ));
                    break;
                case "vt":
                    //graphics.Texture coordinate
                    textures.add(new Vector2f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2])
                    ));
                    break;
                case "vn":
                    //Vertex normal
                    normals.add(new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    ));
                    break;
                case "f":
                    faces.add(new Face(tokens[1], tokens[2], tokens[3]));
                    break;
                default:
                    //Ignore other lines
                    break;
            }
        }
        return reorderLists(vertices, textures, normals, faces);
    }

    private static Mesh reorderLists(List<Vector3f> positionsList, List<Vector2f> textureCoordList, List<Vector3f> normalsList, List<Face> facesList) {
        List<Integer> indicesList = new ArrayList<>();
        //Create position array in the order it has been declared.
        float[] positions = new float[positionsList.size() * 3];
        int i = 0;
        for (Vector3f pos : positionsList) {
            positions[i * 3] = pos.x;
            positions[i * 3 + 1] = pos.y;
            positions[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textureCoords = new float[positionsList.size() * 2];
        float[] normals = new float[positionsList.size() * 3];
        for (Face face : facesList) {
            IndexGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IndexGroup indexGroup : faceVertexIndices) {
                processFaceVertex(
                    indexGroup,
                    textureCoordList,
                    normalsList,
                    indicesList,
                    textureCoords,
                    normals
                );
            }
        }
        //Pretty sure we don't need this instantiation if we use stream below.
        int[] indices = new int[indicesList.size()];
        //There's got to be a less verbose way to do this...
        indices = indicesList.stream().mapToInt((Integer v) -> v).toArray();
        return new Mesh(positions, textureCoords, normals, indices);
    }

    private static void processFaceVertex(
        IndexGroup indexGroup,
        List<Vector2f> textureCoordList,
        List<Vector3f> normalsList,
        List<Integer> indicesList,
        float[] textureCoords,
        float[] normals
    ) {
        //Set index for vertex coordinates.
        int posIndex = indexGroup.indexPos;
        indicesList.add(posIndex);

        //Reorder texture coordinates.
        if (indexGroup.indexTextureCoord >= 0) {
            Vector2f textureCoord = textureCoordList.get(indexGroup.indexTextureCoord);
            textureCoords[posIndex * 2] = textureCoord.x;
            //graphics.Texture coordinates are in UV format so y coordinates need to be calculated as 1 minus the value contained in the file.
            textureCoords[posIndex * 2 + 1] = 1 - textureCoord.y;
        }

        //Reorder vector normals.
        if (indexGroup.indexVectorNormal >= 0) {
            Vector3f normal = normalsList.get(indexGroup.indexVectorNormal);
            normals[posIndex * 3] = normal.x;
            normals[posIndex * 3 + 1] = normal.y;
            normals[posIndex * 3 + 2] = normal.z;
        }
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(OBJLoader.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    /**
     * List of IndexGroup groups for a face triangle (3 vertices per face)
     *
     * A face is composed of a list of indices groups:
     * "f 11/1/1" first index group "17/2/1" second index group "13/3/1" third index group
     *
     * When parsing faces we may encounter objects with no textures but with vector normals.
     * In this case a face line could be like f 11//1 17//1 13//1, so we need to detect those cases.
     */
    protected static class Face {
        private final IndexGroup[] indexGroups = new IndexGroup[3];

        public Face(String v1, String v2, String v3) {
            indexGroups[0] = parseLine(v1);
            indexGroups[1] = parseLine(v2);
            indexGroups[2] = parseLine(v3);
        }

        private IndexGroup parseLine(String line) {
            IndexGroup indexGroup = new IndexGroup();
            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            indexGroup.indexPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                //It can be empty of the OBJ does not define text coordinates.
                String textureCoord = lineTokens[1];
                indexGroup.indexTextureCoord = textureCoord.length() > 0 ? Integer.parseInt(textureCoord) - 1 : IndexGroup.NO_VALUE;
                if (length > 2) {
                    indexGroup.indexVectorNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }
            return indexGroup;
        }

        public IndexGroup[] getFaceVertexIndices() {
            return indexGroups;
        }
    }

    protected static class IndexGroup {
        public static final int NO_VALUE = -1;
        public int indexPos;
        public int indexTextureCoord;
        public int indexVectorNormal;

        public IndexGroup() {
            indexPos = NO_VALUE;
            indexTextureCoord = NO_VALUE;
            indexVectorNormal = NO_VALUE;
        }
    }
}
