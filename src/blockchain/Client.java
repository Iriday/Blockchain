package blockchain;

import java.security.KeyPair;
import java.util.Random;

public class Client {
    private static final Random rand = new Random();
    private final BlockchainInterface blockchain;
    private final String name;
    private boolean online = false;
    private final KeyPair keyPair;

    public Client(String name, BlockchainInterface blockchain) {
        this.blockchain = blockchain;
        this.name = name;
        try {
            keyPair = SignatureUtils.generateKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setOnline() {
        online = true;
        while (online) {
            try {
                Thread.sleep(rand.nextInt(25000) + 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendData(generateMessage(name, blockchain.getNextBlockDataId(), keyPair));
        }
    }

    public void setOffline() {
        online = false;
    }

    private static Message generateMessage(String name, long id, KeyPair keyPair) {
        return new Message(("\n" + name + ": ...").repeat(rand.nextInt(3) + 1), id, keyPair);
    }

    private void sendData(BlockData data) {
        blockchain.receiveNextData(data);
    }
}
