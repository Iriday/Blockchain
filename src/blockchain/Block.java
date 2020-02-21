package blockchain;

import java.util.Random;

public class Block {
    private static final Random random = new Random();
    private static int ids = 0;
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
}
