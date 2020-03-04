package blockchain;

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

        for (int i = 0; i < numOfBlocks; i++) {
            startTime = System.currentTimeMillis();
            thisBlock = createBlock(blockchain.getHashOfPrev(), blockchain.getData(), blockchain.getNumOfZeros());
            endTime = System.currentTimeMillis();

            sendBlock(thisBlock, endTime - startTime);
        }
    }

    private static Block createBlock(String hashOfPrev, String data, int numOfZeros) {
        return new Block(hashOfPrev, data, numOfZeros);
    }

    private void sendBlock(Block block, long blockTime) {
        blockchain.receiveNextBlock(block, blockTime, id);
    }
}
