package blockchain;

import java.io.Serializable;
import java.security.PublicKey;

public interface BlockData extends Serializable {
    long getId();

    String getData();

    byte[] getSignature();

    PublicKey getPublicKey();
}
