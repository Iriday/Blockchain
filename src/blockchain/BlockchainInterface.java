package blockchain;

public interface BlockchainInterface {

    void initialize(int numOfZeros);

    void receiveNextBlock(Block block, long blockTime);

    boolean receiveNextData(BlockData data);

    Object[] getDataForNewBlock();

    long getNextBlockDataId();

    long countUserCoins(String userName);

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
