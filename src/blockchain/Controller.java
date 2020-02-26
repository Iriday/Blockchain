package blockchain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {
    private BlockchainModelInterface model;
    private ViewConsole view;

    public Controller(BlockchainModelInterface model) {

        try {
            this.model = (BlockchainModelInterface) SerializationUtils.deserialize("src/blockchain/data.txt");
            view = new ViewConsole(this, this.model);
        } catch (Exception e) {
            this.model = model;
            view = new ViewConsole(this, model);
            model.initialize(view.input());
        }
        start();
    }

    private void start() {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 15; i++) {
            executor.submit(() -> new Miner(model, 10));
        }
        executor.shutdown();
    }
}
