package blockchain;

import java.util.Random;

public class Client {
    private static final Random rand = new Random();
    private final BlockchainModelInterface model;
    private final String name;
    private boolean online = false;

    public Client(String name, BlockchainModelInterface model) {
        this.model = model;
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
            sendData(generateMessage(name));
        }
    }

    public void setOffline() {
        online = false;
    }

    private static String generateMessage(String name) {
        return ("\n" + name + ": ...").repeat(rand.nextInt(3) + 1);
    }

    private void sendData(String data) {
      // model.
    }
}
