package blockchain;

public interface BlockchainModelInterface {

    void initialize(int numOfZeros);

    boolean receiveNextBlock(Block block, long blockTime, long minerId);

    int getNumOfZeros();

    String getHashOfPrev();

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
