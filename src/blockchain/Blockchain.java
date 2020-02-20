package blockchain;

import java.util.ArrayList;

public class Blockchain {
    private ArrayList<Block> blocks;

    public void initialize(int numOfBlocks) { // createChain
        blocks = new ArrayList<>(numOfBlocks);

        Block prevBlock = null;
        Block thisBlock;

        for (int i = 0; i < numOfBlocks; i++) {

            if (i == 0) {
                thisBlock = createBlock("0");
            } else {
                thisBlock = createBlock(prevBlock.hashOfThis);
            }

            blocks.add(thisBlock);
            prevBlock = thisBlock;
        }
    }

    private static Block createBlock(String hashOfPrev) {
        return new Block(hashOfPrev);
    }

    public String toString() {
        var sb = new StringBuilder();
        for (Block block : blocks) {
            sb.append("Block:\n");
            sb.append("Id: ");
            sb.append(block.id);
            sb.append("\nTimestamp: ");
            sb.append(block.timeStamp);
            sb.append("\nHash of the previous block:\n");
            sb.append(block.hashOfPrev);
            sb.append("\nHash of the block:\n");
            sb.append(block.hashOfThis);
            sb.append("\n\n");
        }
        return sb.toString();
    }
}
