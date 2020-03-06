package blockchain;

import java.util.List;

public class Miner {
    private final BlockchainInterface blockchain;
    private final long numOfBlocks;
    private static long ids = 0;
    public final long id = ++ids;

    public Miner(BlockchainInterface blockchain, long numOfBlocks) {
        this.blockchain = blockchain;
        this.numOfBlocks = numOfBlocks;
        generateBlock(); // mineBlock
    }

    private void generateBlock() {
        long startTime;
        Block thisBlock;
        long endTime;
        Object[] data;

        for (int i = 0; i < numOfBlocks; i++) {
            data = blockchain.getDataForNewBlock();

            startTime = System.currentTimeMillis();
            thisBlock = createBlock((String) data[0], (List<BlockData>) data[1], (int) data[2]);
            endTime = System.currentTimeMillis();

            sendBlock(thisBlock, endTime - startTime);
        }
    }

    private static Block createBlock(String hashOfPrev, List<BlockData> data, int numOfZeros) {
        return new Block(hashOfPrev, data, numOfZeros);
    }

    private void sendBlock(Block block, long blockTime) {
        blockchain.receiveNextBlock(block, blockTime, id);
    }
}
