package blockchain;

import java.io.Serializable;
import java.security.KeyPair;

public class Message extends BlockData implements Serializable {

    public Message(String message, long id, KeyPair keyPair) {
        super(message, id, keyPair);
    }

    public static Message getEmptyData() {
        KeyPair keyPair;
        try {
            keyPair = SignatureUtils.generateKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Message("no data", 0, keyPair);
    }
}
