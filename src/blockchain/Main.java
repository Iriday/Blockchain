package blockchain;

public class Main {
    public static void main(String[] args) {
        ModelInterface model = new Model();
        ViewInterface view = new ViewConsole();
        ControllerInterface controller = new Controller(model, view);
    }
}
