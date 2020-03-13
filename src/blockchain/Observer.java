package blockchain;

import java.util.List;

public interface Observer {
    void update(long blockId, long timestamp, int magicNumber, String hashOfPrev, String hashOfThis, long blockTime, long minerId, long minerGetsVC, int numOfZeros, int numOfZerosChange, List<BlockData> data);
}
