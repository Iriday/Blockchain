package blockchain;

public interface BlockchainModelInterface {

    void initialize(int numOfZeros);

    void receiveNextBlock(Block block, long creationTime);

    int getNumOfZeros();

    String getHashOfPrev();

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
