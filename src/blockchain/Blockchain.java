package blockchain;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Blockchain implements BlockchainInterface, Serializable {
    private List<Block> blocks;
    private List<String> hashes;
    private long coins = 999_999_999;
    private transient List<Observer> observers = new CopyOnWriteArrayList<>();
    private final Lock lockData = new Lock();
    private final Lock lockBlockDataId = new Lock();
    private final Lock lockReceiveNextBlock = new Lock();
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
    private final long minerGetsVC = 100;
    private long lastAcceptedBlockDataId = 1;

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
    public long receiveNextBlock(Block block, long blockTime, long minerId) {
        synchronized (lockReceiveNextBlock) {
            if (!isBlockValid(block, maxPrevBlockDataId, numOfZeros, hashOfPrev)) return 0;

            try {
                block.setUnmodifiableId(++idCounter);
            } catch (Exception e) {
                --idCounter;
                return 0;
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
                synchronized (lockData) {
                    synchronized (lockBlockDataId) {
                        SerializationUtils.serialize(this, "src/blockchain/data.txt");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            notifyObservers();
            switcher = true;
            coins -= 100;
            return minerGetsVC;
        }
    }

    @Override
    public boolean receiveNextData(BlockData blockData) {
        synchronized (lockData) {
            if (lastAcceptedBlockDataId >= blockData.getId()) return false;
            lastAcceptedBlockDataId = blockData.getId();
            this.newData.add(blockData);
            if (switcher) {
                swapData();
                createDataForNewBlock();
                switcher = false;
            }
            return true;
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
        //check id(s)
        long[] ids = getSortedIds(block.data);
        if (maxPrevBlockDataId >= ids[0]) return false;
        for (int i = 1; i < ids.length; i++) {
            if (ids[i - 1] >= ids[i]) return false;
        }
        //check numOfZeros, hash, signature(s)
        return Utils.startsWithZeros(block.hashOfThis, numOfZeros) && hashOfPrev.equals(block.hashOfPrev) && checkSignatures(block.data);
    }

    private static boolean checkSignatures(List<BlockData> blockData) {
        for (BlockData d : blockData) {
            try {
                if (!SignatureUtils.verifyData(d.getData() + d.getId(), d.getSignature(), d.getPublicKey())) {
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean isBlockchainHacked() {
        synchronized (lockReceiveNextBlock) {
            return !isBlockchainValid(blocks, hashes);
        }
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
            if (ids[i - 1] >= ids[i]) return false;
        }
        //check signature(s)
        for (Block block : blocks) {
            if (!checkSignatures(block.data)) return false;
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

    @Override
    public long countUserCoins(String userName) {
        synchronized (lockReceiveNextBlock) {
            synchronized (lockData) {
                synchronized (lockBlockDataId) {
                    return countUserCoins(blocks, userName, oldData, newData, minerGetsVC);
                }
            }
        }
    }

    private long countUserCoins(List<Block> blocks, String userName, List<BlockData> oldData, List<BlockData> newData, long blockPrice) {
        if (userName == null || userName.isEmpty() || blocks == null || blockPrice <= 0)
            throw new IllegalArgumentException();
        if (blocks.isEmpty()) return 0;

        Set<BlockData> combinedBlockData = new HashSet<>(); // Set handles situation if swapData was not called write after block was added
        combinedBlockData.addAll(oldData);
        combinedBlockData.addAll(newData);
        combinedBlockData.addAll(blocks.stream().flatMap(block -> block.data.stream()).collect(Collectors.toList()));

        long receivedCoins = combinedBlockData.stream()
                .filter(d -> d.getData().endsWith("\n" + userName))
                .mapToLong(d -> Long.parseLong(d.getData().substring(d.getData().indexOf("\n") + 1, d.getData().lastIndexOf("\n"))))
                .reduce(Long::sum)
                .orElse(0);
        // if miner +coins for blocks
        receivedCoins += blocks.stream().filter(d -> d.createdBy.equals(userName)).count() * blockPrice;

        long sentCoins = combinedBlockData.stream()
                .filter(d -> d.getData().startsWith(userName + "\n"))
                .mapToLong(d -> Long.parseLong(d.getData().substring(d.getData().indexOf("\n") + 1, d.getData().lastIndexOf("\n"))))
                .reduce(Long::sum)
                .orElse(0);

        if (receivedCoins < 0 || sentCoins < 0)
            throw new RuntimeException("receivedCoins || sentCoins < 0, blockchain is not correct");
        if (sentCoins > receivedCoins)
            throw new RuntimeException("sentCoins > receivedCoins, blockchain is not correct");

        return receivedCoins - sentCoins; // hasCoins
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
                thisBlock.getId(), thisBlock.timeStamp, thisBlock.getMagicNumber(), thisBlock.hashOfPrev, thisBlock.hashOfThis, blockTime, thisBlock.createdBy, minerGetsVC, numOfZeros, numOfZerosChange, thisBlock.data));
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        if (isBlockchainHacked()) {
            throw new RuntimeException("Block hash does NOT match");
        }
        observers = new CopyOnWriteArrayList<>();
        switcher = true;
    }

    private static class Lock implements Serializable {
    }
}
