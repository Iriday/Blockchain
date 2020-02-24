package blockchain;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockchainModel implements BlockchainModelInterface, Serializable {
    private List<Block> blocks;
    private List<String> hashes;
    private transient List<Observer> observers = new ArrayList<>();
    private int numOfBlocks, numOfZeros;

    private Block prevBlock;
    private Block thisBlock;
    private long blockTime;

    @Override
    public void initialize(int numOfBlocks, int numOfZeros) {
        this.numOfBlocks = numOfBlocks;
        this.numOfZeros = numOfZeros;
        blocks = new ArrayList<>(numOfBlocks);
        hashes = new ArrayList<>(numOfBlocks);
    }

    @Override
    public void run() { // createChain
        long startTime;

        for (int i = 0; i < numOfBlocks; i++) {
            if (prevBlock == null) { // if first block hashOfPrev=0
                startTime = System.currentTimeMillis();
                thisBlock = createBlock("0", numOfZeros);
                blockTime = System.currentTimeMillis() - startTime;
            } else {
                startTime = System.currentTimeMillis();
                thisBlock = createBlock(prevBlock.hashOfThis, numOfZeros);
                blockTime = System.currentTimeMillis() - startTime;
            }

            blocks.add(thisBlock);
            hashes.add(thisBlock.hashOfThis);
            prevBlock = thisBlock;
            try {
                SerializationUtils.serialize(this, "src/blockchain/data.txt");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            notifyObservers();
        }
    }

    @Override
    public boolean isBlockchainHacked() {
        return !isBlockchainValid(blocks, hashes);
    }

    private static boolean isBlockchainValid(List<Block> blocks, List<String> hashes) {
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

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(obr -> obr.update(thisBlock.id, thisBlock.timeStamp, thisBlock.getMagicNumber(), thisBlock.hashOfPrev, thisBlock.hashOfThis, blockTime));
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        observers = new ArrayList<>();
    }
}
