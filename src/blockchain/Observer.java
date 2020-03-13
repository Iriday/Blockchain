package blockchain;

import java.util.List;

public interface Observer {
    void update(long blockId, long timestamp, int magicNumber, String hashOfPrev, String hashOfThis, long blockTime, String minerName, long minerGetsVC, int numOfZeros, int numOfZerosChange, List<BlockData> data);
}
