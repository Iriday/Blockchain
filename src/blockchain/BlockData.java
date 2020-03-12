package blockchain;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;

public abstract class BlockData implements Serializable {
    private final long id;
    private final String data;
    private final byte[] signature;
    private final PublicKey publicKey;

    public BlockData(String data, long id, KeyPair keyPair) {
        this.id = id;
        this.data = data;
        try {
            this.publicKey = keyPair.getPublic();
            this.signature = SignatureUtils.createSignature((getData() + id), keyPair.getPrivate());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public byte[] getSignature() {
        return signature;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
