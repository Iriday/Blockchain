package blockchain;

import java.io.*;

public class SerializationUtils {
    public static void serialize(Serializable object, String filepath) throws IOException {
        var stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)));
        stream.writeObject(object);
        stream.close();
    }

    public static Object deserialize(String filepath) throws IOException, ClassNotFoundException {
        var stream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filepath)));
        Object object = stream.readObject();
        stream.close();
        return object;
    }
}
