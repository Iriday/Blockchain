package blockchain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Model implements ModelInterface {
    private BlockchainInterface blockchain;

    @Override
    public boolean deserializeAndContinue() {
        // init blockchain
        try {
            blockchain = (BlockchainInterface) SerializationUtils.deserialize("src/blockchain/data.txt");

            initAndRun();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void startAnew(int input) {
        // init blockchain
        blockchain = new Blockchain();
        blockchain.initialize(input);

        initAndRun();
    }

    private void initAndRun() {
        // init/run miners
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 15; i++) {
            executor.submit(() -> new Miner(blockchain, 10));
        }
        executor.shutdown();

        // init/run clients
        Stream.of(new Client("Julia", blockchain), new Client("Sarah", blockchain), new Client("Kate", blockchain), new Client("Ivy", blockchain), new Client("Grace", blockchain))
                .forEach(client -> new Thread(client::setOnline).start());
    }

    @Override
    public BlockchainInterface getBlockchain() {
        return blockchain;
    }
}
