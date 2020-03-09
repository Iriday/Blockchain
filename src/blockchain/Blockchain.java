package blockchain;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class Blockchain implements BlockchainInterface, Serializable {
    private List<Block> blocks;
    private List<String> hashes;
    private transient List<Observer> observers = new ArrayList<>();
    private final Lock lockData = new Lock();
    private final Lock lockBlockDataId = new Lock();
    private volatile List<BlockData> newData;
    private volatile List<BlockData> oldData;
    private Object[] data;

    private long idCounter = 0;
    private volatile int numOfZeros = 0;
    private volatile boolean regulateNumOfZeros = false;
    private int numOfZerosChange = Integer.MAX_VALUE;
    private volatile String hashOfPrev = "0";
    private Block thisBlock;
    private long blockTime;
    private long minerId;
    private final BlockData NO_DATA = Message.getEmptyData();
    private long currentBlockDataId = 0;
    private volatile boolean switcher = false;
    private long maxPrevBlockDataId = -1;

    @Override
    public void initialize(int numOfZeros) {
        if (numOfZeros < 0) regulateNumOfZeros = true;
        else this.numOfZeros = numOfZeros;

        blocks = new ArrayList<>();
        hashes = new ArrayList<>();
        newData = new ArrayList<>();
        oldData = new ArrayList<>();
        oldData.add(NO_DATA);
        createDataForNewBlock();
    }

    @Override
    public synchronized boolean receiveNextBlock(Block block, long blockTime, long minerId) {
        if (!isBlockValid(block, maxPrevBlockDataId, numOfZeros, hashOfPrev)) return false;

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

        maxPrevBlockDataId = getMaxBlockDataId(thisBlock.data);

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
        switcher = true;
        return true;
    }

    @Override
    public void receiveNextData(BlockData blockData) {
        synchronized (lockData) {
            this.newData.add(blockData);
            if (switcher) {
                swapData();
                createDataForNewBlock();
                switcher = false;
            }
        }
    }

    private void createDataForNewBlock() {
        data = new Object[]{hashOfPrev, oldData, numOfZeros};
    }

    @Override
    public Object[] getDataForNewBlock() {
        if (switcher) return null;
        return data;
    }

    private void swapData() {
        synchronized (lockData) {
            oldData = newData;
            newData = new ArrayList<>();
        }
    }

    @Override
    public long getNextBlockDataId() {
        synchronized (lockBlockDataId) {
            return ++currentBlockDataId;
        }
    }

    private static long getMaxBlockDataId(List<BlockData> blockData) {
        return blockData.stream().mapToLong(BlockData::getId).max().orElseThrow();
    }

    private static long[] getSortedIds(List<BlockData> blockData) {
        return blockData.stream().mapToLong(BlockData::getId).sorted().toArray();
    }

    private static boolean isBlockValid(Block block, long maxPrevBlockDataId, int numOfZeros, String hashOfPrev) {
        if (block == null || block.data == null || block.data.isEmpty()) return false;

        long[] ids = getSortedIds(block.data);

        if ((maxPrevBlockDataId + 1) != ids[0]) return false;
        for (int i = 1; i < ids.length; i++) {
            if ((ids[i - 1] + 1) != ids[i]) return false;
        }
        return Utils.startsWithZeros(block.hashOfThis, numOfZeros) && hashOfPrev.equals(block.hashOfPrev);
    }

    @Override
    public boolean isBlockchainHacked() {
        return !isBlockchainValid(blocks, hashes);
    }

    private static boolean isBlockchainValid(List<Block> blocks, List<String> hashes) {
        if (blocks == null || hashes == null || hashes.isEmpty() || blocks.size() != hashes.size())
            return false;
        for (int i = 0; i < hashes.size(); i++) {
            Block block = blocks.get(i);
            if (block == null || block.data == null || block.data.isEmpty()) return false;
            if (!hashes.get(i).equals(block.hashOfThis)) return false;
        }
        // check BlockData id
        long[] ids = blocks.stream()
                .map(block -> Blockchain.getSortedIds(block.data))
                .flatMapToLong(LongStream::of)
                .toArray();

        if (ids[0] != 0) return false;
        for (int i = 1; i < ids.length; i++) {
            if ((ids[i - 1] + 1) != ids[i]) return false;
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
                thisBlock.getId(), thisBlock.timeStamp, thisBlock.getMagicNumber(), thisBlock.hashOfPrev, thisBlock.hashOfThis, blockTime, minerId, numOfZeros, numOfZerosChange, thisBlock.data));
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        if (isBlockchainHacked()) {
            throw new RuntimeException("Block hash does NOT match");
        }
        observers = new ArrayList<>();
        switcher = true;
    }

    private static class Lock implements Serializable {
    }
}
