package blockchain;

import java.util.Random;

public class Client {
    private static final Random rand = new Random();
    private final BlockchainInterface blockchain;
    private final String name;
    private boolean online = false;
    private long id = 0;

    public Client(String name, BlockchainInterface blockchain) {
        this.blockchain = blockchain;
        this.name = name;
    }

    public void setOnline() {
        online = true;
        while (online) {
            try {
                Thread.sleep(rand.nextInt(25000) + 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendData(generateMessage(name, ++id));
        }
    }

    public void setOffline() {
        online = false;
    }

    private static Message generateMessage(String name, long id) {
        return new Message(("\n" + name + ": ...").repeat(rand.nextInt(3) + 1), id);
    }

    private void sendData(BlockData data) {
        blockchain.receiveNextData(data);
    }
}
