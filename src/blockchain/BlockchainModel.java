package blockchain;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockchainModel implements BlockchainModelInterface, Serializable {
    private List<Block> blocks;
    private List<String> hashes;
    private transient List<Observer> observers = new ArrayList<>();

    private long idCounter = 0;
    private int numOfZeros = 0;
    private boolean regulateNumOfZeros = false;
    private int numOfZerosChange = Integer.MAX_VALUE;
    private String hashOfPrev = "0";
    private Block thisBlock;
    private long blockTime;
    private long minerId;

    @Override
    public void initialize(int numOfZeros) {
        if (numOfZeros < 0) {
            regulateNumOfZeros = true;
        } else {
            this.numOfZeros = numOfZeros;
        }

        blocks = new ArrayList<>();
        hashes = new ArrayList<>();
    }

    @Override
    public synchronized boolean receiveNextBlock(Block block, long blockTime, long minerId) {
        if (!isBlockValid(block)) return false;

        try {
            block.setUnmodifiableId(++idCounter);
        } catch (Exception e) {
            --idCounter;
            return false;
        }

        this.thisBlock = block;
        this.blockTime = blockTime;
        this.minerId = minerId;

        blocks.add(thisBlock);
        hashes.add(thisBlock.hashOfThis);
        hashOfPrev = thisBlock.hashOfThis;

        if (regulateNumOfZeros) {
            int newNumOfZeros = adjustNumOfZeros(numOfZeros, blockTime);
            numOfZerosChange = newNumOfZeros - numOfZeros; // -1, 0, or 1
            numOfZeros = newNumOfZeros;
        }

        try {
            SerializationUtils.serialize(this, "src/blockchain/data.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        notifyObservers();
        return true;
    }

    private boolean isBlockValid(Block block) {
        return Utils.startsWithZeros(block.hashOfThis, numOfZeros) && hashOfPrev.equals(block.hashOfPrev);
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

    private int adjustNumOfZeros(int numOfZeros, long blockTimeMillis) {
        if (numOfZeros < 0 || blockTimeMillis < 0) throw new IllegalArgumentException();
        if (blockTimeMillis == 0) return ++numOfZeros;

        double seconds = blockTimeMillis / 1000.0;
        if (seconds < 2) return ++numOfZeros;
        if (seconds < 7) return numOfZeros;
        else return --numOfZeros; // >=7
    }

    public String toString() {
        var sb = new StringBuilder();
        for (Block block : blocks) {
            sb.append("Block:\n");
            sb.append("Id: ");
            sb.append(block.getId());
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
        observers.forEach(obr -> obr.update(
                thisBlock.getId(), thisBlock.timeStamp, thisBlock.getMagicNumber(), thisBlock.hashOfPrev, thisBlock.hashOfThis, blockTime, minerId, numOfZeros, numOfZerosChange));
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        observers = new ArrayList<>();
        if (isBlockchainHacked()) {
            throw new RuntimeException("Block hash does NOT match");
        }
    }

    @Override
    public int getNumOfZeros() {
        return numOfZeros;
    }

    @Override
    public String getHashOfPrev() {
        return hashOfPrev;
    }
}
