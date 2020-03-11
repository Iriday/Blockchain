package blockchain;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;

public class Message implements BlockData, Serializable {

    private final String message;
    private final long id;
    private final byte[] signature;
    private final PublicKey publicKey;

    public Message(String message, long id) {
        this.message = message;
        this.id = id;
        try {
            KeyPair keyPair = SignatureUtils.generateKeys();
            this.publicKey = keyPair.getPublic();
            this.signature = SignatureUtils.createSignature((message + id), keyPair.getPrivate());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getData() {
        return message;
    }


    @Override
    public long getId() {
        return id;
    }

    @Override
    public byte[] getSignature() {
        return signature;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static Message getEmptyData() {
        return new Message("no data", 0);
    }
}
