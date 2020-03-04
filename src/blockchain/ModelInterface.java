package blockchain;

public interface ModelInterface {
    boolean deserializeAndContinue();

    void startAnew(int input);

    BlockchainInterface getBlockchain();
}
