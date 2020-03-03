package blockchain;

import java.io.Serializable;
import java.util.Random;

public class Block implements Serializable {
    private static final Random random = new Random();
    private long id = -101;
    private boolean idAssigned = false;
    public final long timeStamp;
    public final String hashOfPrev;
    public final String hashOfThis;
    public final String data;
    private int magicNumber;

    public Block(String hashOfPrev, String data, int numOfZeros) {
        timeStamp = System.currentTimeMillis();
        this.hashOfPrev = hashOfPrev;
        this.data = data;
        magicNumber = random.nextInt();
        hashOfThis = createHash(numOfZeros);
    }

    private String createHash(int numOfZeros) {
        if (numOfZeros < 0) throw new IllegalArgumentException();

        var sb = new StringBuilder();
        sb.append(timeStamp);
        sb.append(data);
        sb.append(idAssigned);
        sb.append(id);
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
