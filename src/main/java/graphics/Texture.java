package graphics;

import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

public class Texture {

    public final int id;
    public final int width;
    public final int height;

    /**
     * Note: Oracle java convention seems to be camelcase fileName
     * @param fileName
     * @throws Exception
     */
    public Texture(String fileName) throws Exception {
        ByteBuffer byteBuffer;

        //Load texture file.
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            //I do not care for this way of loading files.
            URL url = Texture.class.getResource(fileName);
            File file = Paths.get(url.toURI()).toFile();
            String filePath = file.getAbsolutePath();
            byteBuffer = STBImage.stbi_load(filePath, w, h, channels, 4);
            if (byteBuffer == null) {
                throw new Exception("File [" + filePath + "] not loaded: " + STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();

            //Create a new OpenGL texture.
            id = GL30.glGenTextures();
            //Bind the texture.
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
            //Each component is one byte in size (RGBA).
            GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);

            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

            //Upload texture data.
            GL30.glTexImage2D(
                    GL30.GL_TEXTURE_2D, //The target texture type
                    0, //level-of-detail number: 0 is base image level, n is nth
                    GL30.GL_RGBA, //internal format specifies the number of color components
                    width,
                    height,
                    0, //border: this value must be 0
                    GL30.GL_RGBA, //format specifies the format of pixel data
                    GL30.GL_UNSIGNED_BYTE, //type specifies the data type of pixel data
                    byteBuffer //the buffer that stores our data
            );

            //Generate mipmaps (as opposed to setting filtering parameters).
            GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);

            //Free the memory of the raw image data.
            STBImage.stbi_image_free(byteBuffer);
        }
    }

    public void bind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
    }

    public void cleanup() {
        GL30.glDeleteTextures(id);
    }

}
