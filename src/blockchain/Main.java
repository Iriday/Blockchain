package blockchain;

public class Main {
    public static void main(String[] args) {
        var model = new BlockchainModel();
        var controller = new Controller(model);
    }
}
