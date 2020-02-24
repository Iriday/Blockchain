package blockchain;

public class Controller {
    private BlockchainModelInterface model;
    private ViewConsole view;

    public Controller(BlockchainModelInterface model) {

        try {
            this.model = (BlockchainModelInterface) SerializationUtils.deserialize("src/blockchain/data.txt");
            view = new ViewConsole(this, this.model);
            this.model.run();
        } catch (Exception e) {
            this.model = model;
            view = new ViewConsole(this, model);
            model.initialize(5, view.input());
            start();
        }
    }

    private void start() {
        model.run();
    }
}
