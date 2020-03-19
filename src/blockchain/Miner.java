package blockchain;

import java.util.List;

public class Miner {
    private final BlockchainInterface blockchain;
    private final long numOfBlocks;
    private static long ids = 0;
    public final long id = ++ids;
    private final String minerName;
    private long coins = 0;

    public Miner(BlockchainInterface blockchain, long numOfBlocks) {
        this.blockchain = blockchain;
        this.numOfBlocks = numOfBlocks < 0 ? -1 : numOfBlocks;
        minerName = "miner #" + id;
        generateBlock(); // mineBlock
    }

    private void generateBlock() {
        long startTime;
        Block thisBlock;
        long endTime;
        Object[] data;

        int i = numOfBlocks == -1 ? -2 : 0; // if numOfBlocks == -1 mine blocks infinitely
        while (i < numOfBlocks) {
            while ((data = blockchain.getDataForNewBlock()) == null) {
                Thread.yield();
            }

            startTime = System.currentTimeMillis();
            thisBlock = createBlock((String) data[0], (List<BlockData>) data[1], (int) data[2], minerName);
            endTime = System.currentTimeMillis();

            coins += sendBlock(thisBlock, endTime - startTime, id);

            if (numOfBlocks != -1) i++;
        }
    }

    private static Block createBlock(String hashOfPrev, List<BlockData> data, int numOfZeros, String createdBy) {
        return new Block(hashOfPrev, data, numOfZeros, createdBy);
    }

    private long sendBlock(Block block, long blockTime, long id) {
        return blockchain.receiveNextBlock(block, blockTime, id);
    }
}
