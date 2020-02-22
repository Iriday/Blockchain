package blockchain;

public interface BlockchainModelInterface {
    void run(int numOfBlocks, int numOfZeros);

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
