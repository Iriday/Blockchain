package blockchain;

import java.io.Serializable;
import java.security.KeyPair;

public class Transaction extends BlockData implements Serializable {

    public Transaction(long id, String sender, long virtualCoins, String receiver, KeyPair keyPair) {
        super(sender + " " + virtualCoins + " " + receiver, id, keyPair);
    }

    public Transaction getEmptyData() {
        KeyPair keyPair;
        try {
            keyPair = SignatureUtils.generateKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Transaction(0, "\nNo transactions", -1, "No transactions", keyPair);
    }
}
