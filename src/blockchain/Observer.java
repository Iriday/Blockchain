package blockchain;

public interface Observer {
    void update(long blockId, long timestamp, int magicNumber, String hashOfPrev, String hashOfThis, long blockTime, long minerId, int numOfZeros, int numOfZerosChange);
}
