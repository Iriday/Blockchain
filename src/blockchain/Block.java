package blockchain;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Block implements Serializable {
    private static final Random random = new Random();
    private long id = -101;
    private boolean idAssigned = false;
    public final long timeStamp;
    public final String hashOfPrev;
    public final String hashOfThis;
    public final List<BlockData> data;
    private int magicNumber;
    private final String createdBy;

    public Block(String hashOfPrev, List<BlockData> data, int numOfZeros, String createdBy) {
        timeStamp = System.currentTimeMillis();
        this.hashOfPrev = hashOfPrev;
        this.data = data;
        this.createdBy = createdBy;
        magicNumber = random.nextInt();
        hashOfThis = createHash(numOfZeros);
    }

    private String createHash(int numOfZeros) {
        if (numOfZeros < 0) throw new IllegalArgumentException();

        var sb = new StringBuilder();
        sb.append(timeStamp);
        sb.append(data);
        data.forEach(data -> {
            sb.append(data);
            sb.append(data.getId());
            sb.append(data.getData());
        });
        sb.append(idAssigned);
        sb.append(id);
        sb.append(createdBy);
        sb.append(hashOfPrev);

        String hash;
        do {
            magicNumber = random.nextInt();
            hash = Utils.applySHA256(sb.toString() + magicNumber);
        } while (!Utils.startsWithZeros(hash, numOfZeros));

        return hash;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public synchronized void setUnmodifiableId(long unmodifiableId) throws Exception {
        if (idAssigned) throw new Exception("Attempt to rewrite block id");

        idAssigned = true;
        this.id = unmodifiableId;
    }

    public long getId() {
        return id;
    }
}
