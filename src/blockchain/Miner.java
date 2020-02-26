package blockchain;

public class Miner {
    private final BlockchainModelInterface blockchain;
    private final long numOfBlocks;
    private static long ids = 0;
    public final long id = ++ids;

    public Miner(BlockchainModelInterface blockchain, long numOfBlocks) {
        this.blockchain = blockchain;
        this.numOfBlocks = numOfBlocks;
        generateBlock(); // mineBlock
    }

    private void generateBlock() {
        long startTime;
        Block thisBlock;
        long endTime;
        int numOfZeros = blockchain.getNumOfZeros();
        String hashOfPrev;

        for (int i = 0; i < numOfBlocks; i++) {
            hashOfPrev = blockchain.getHashOfPrev();

            startTime = System.currentTimeMillis();
            thisBlock = createBlock(hashOfPrev, numOfZeros);
            endTime = System.currentTimeMillis();

            sendBlock(thisBlock, endTime - startTime);
        }
    }

    private static Block createBlock(String hashOfPrev, int numOfZeros) {
        return new Block(hashOfPrev, numOfZeros);
    }

    private void sendBlock(Block block, long blockTime) {
        blockchain.receiveNextBlock(block, blockTime, id);
    }
}
