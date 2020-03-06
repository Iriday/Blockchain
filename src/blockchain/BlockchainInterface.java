package blockchain;

import java.util.List;

public interface BlockchainInterface {

    void initialize(int numOfZeros);

    boolean receiveNextBlock(Block block, long blockTime, long minerId);

    void receiveNextData(BlockData data);

    List<BlockData> getData();

    long getNextBlockDataId();

    int getNumOfZeros();

    String getHashOfPrev();

    boolean isBlockchainHacked();

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
