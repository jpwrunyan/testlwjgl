package utils;

import java.io.InputStream;
import java.util.Scanner;

public class ShaderFileUtil {
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Class.forName(ShaderFileUtil.class.getName()).getResourceAsStream(fileName);
            Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }
}
