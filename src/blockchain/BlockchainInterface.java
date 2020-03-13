package blockchain;

public interface BlockchainInterface {

    void initialize(int numOfZeros);

    long receiveNextBlock(Block block, long blockTime, long minerId);

    void receiveNextData(BlockData data);

    Object[] getDataForNewBlock();

    long getNextBlockDataId();

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
