package blockchain;

public interface BlockchainModelInterface {

    void initialize(int numOfZeros);

    boolean receiveNextBlock(Block block, long blockTime, long minerId);

    void receiveNextData(String data);

    String getData();

    int getNumOfZeros();

    String getHashOfPrev();

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
