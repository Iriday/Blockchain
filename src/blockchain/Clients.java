package blockchain;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Clients {
    private final static List<String> clients = new CopyOnWriteArrayList<>();
    public final static Random rand = new Random();

    public static void addClient(String clientName) {
        clients.add(clientName);
    }

    public static String getRandClientExceptThis(String clientName) {
        String randClient;
        do {
            randClient = clients.get(rand.nextInt(clients.size()));
        } while (clientName.equals(randClient));
        return randClient;
    }
}
