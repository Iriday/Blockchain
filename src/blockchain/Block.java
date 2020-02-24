package blockchain;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

public class Block implements Serializable {
    private static final Random random = new Random();
    private static long ids = 0;
    public final long id = ++ids;
    public final long timeStamp;
    public final String hashOfPrev;
    public final String hashOfThis;
    private int magicNumber;

    public Block(String hashOfPrev, int numOfZeros) {
        timeStamp = System.currentTimeMillis();
        this.hashOfPrev = hashOfPrev;
        magicNumber = random.nextInt();
        hashOfThis = createHash(numOfZeros);
    }

    private String createHash(int numOfZeros) {
        if (numOfZeros < 0) throw new IllegalArgumentException();

        var sb = new StringBuilder();
        sb.append(ids);
        sb.append(id);
        sb.append(timeStamp);
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

    private void writeObject(ObjectOutputStream oos) throws Exception {
        oos.defaultWriteObject();
        oos.writeObject(ids);
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        ids = (long) ois.readObject();
    }
}
