package blockchain;

public interface BlockchainModelInterface {

    void initialize(int numOfBlocks, int numOfZeros);

    void run();

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
