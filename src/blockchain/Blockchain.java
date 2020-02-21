package blockchain;

import java.util.ArrayList;

public class Blockchain {
    private ArrayList<Block> blocks;
    private ArrayList<String> hashes;

    public void initialize(int numOfBlocks, int numOfZeros) { // createChain
        blocks = new ArrayList<>(numOfBlocks);
        hashes = new ArrayList<>(numOfBlocks);

        Block prevBlock = null;
        Block thisBlock;

        for (int i = 0; i < numOfBlocks; i++) {

            if (i == 0) {
                thisBlock = createBlock("0", numOfZeros);
            } else {
                thisBlock = createBlock(prevBlock.hashOfThis, numOfZeros);
            }

            blocks.add(thisBlock);
            hashes.add(thisBlock.hashOfThis);
            prevBlock = thisBlock;
        }
    }

    public boolean isBlockchainHacked() {
        return !isBlockchainValid(blocks, hashes);
    }

    private static boolean isBlockchainValid(ArrayList<Block> blocks, ArrayList<String> hashes) {
        for (int i = 0; i < hashes.size(); i++) {
            if (!hashes.get(i).equals(blocks.get(i).hashOfThis))
                return false;
        }
        return true;
    }

    private static Block createBlock(String hashOfPrev, int numOfZeros) {
        return new Block(hashOfPrev, numOfZeros);
    }

    public String toString() {
        var sb = new StringBuilder();
        for (Block block : blocks) {
            sb.append("Block:\n");
            sb.append("Id: ");
            sb.append(block.id);
            sb.append("\nTimestamp: ");
            sb.append(block.timeStamp);
            sb.append("\nMagic number: ");
            sb.append(block.getMagicNumber());
            sb.append("\nHash of the previous block:\n");
            sb.append(block.hashOfPrev);
            sb.append("\nHash of the block:\n");
            sb.append(block.hashOfThis);
            sb.append("\n\n");
        }
        return sb.toString();
    }
}
