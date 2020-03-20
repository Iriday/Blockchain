package blockchain;

import java.security.KeyPair;
import java.util.Random;

public class Client {
    private static final Random rand = new Random();
    private final BlockchainInterface blockchain;
    private final String name;
    private volatile boolean online = false;
    private final KeyPair keyPair;

    public Client(String name, BlockchainInterface blockchain) {
        this.blockchain = blockchain;
        this.name = name;
        Clients.addClient(name);
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
                Thread.sleep(rand.nextInt(500) + 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            long coinsToSend = rand.nextInt(100) + 1;
            long hasCoins = blockchain.countUserCoins(name);
            if (hasCoins >= coinsToSend) {
                String randReceiver = Clients.getRandClientExceptThis(name);
                while (!blockchain.receiveNextData(new Transaction(blockchain.getNextBlockDataId(), name, coinsToSend, randReceiver, keyPair)))
                    ;
            }
            // String message = generateMessage(name);
            // while (!blockchain.receiveNextData(new Message(message, blockchain.getNextBlockDataId(), keyPair))) ;
        }
    }

    public void setOffline() {
        online = false;
    }

    private static String generateMessage(String senderName) {
        return ("\n" + senderName + ": ...").repeat(rand.nextInt(3) + 1);
    }
}
