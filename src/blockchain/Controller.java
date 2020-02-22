package blockchain;

public class Controller {
    private final BlockchainModelInterface model;
    private final ViewConsole view;

    public Controller(BlockchainModelInterface model) {
        this.model = model;
        view = new ViewConsole(this, model);
    }

    public void start(int input) {
        model.run(5, input);
    }
}
