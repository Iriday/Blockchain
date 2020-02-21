package blockchain;

import java.util.Random;

public class Block {
    private static final Random random = new Random();
    private static int ids = 0;
    public final long id = ++ids;
    public final long timeStamp;
    public final String hashOfPrev;
    public final String hashOfThis;
    public final int magicNumber;

    public Block(String hashOfPrev) {
        timeStamp = System.currentTimeMillis();
        this.hashOfPrev = hashOfPrev;
        magicNumber = random.nextInt();
        hashOfThis = createHash();
    }

    private String createHash() {
        var sb = new StringBuilder();
        sb.append(ids);
        sb.append(id);
        sb.append(timeStamp);
        sb.append(hashOfPrev);
        sb.append(magicNumber);
        return Utils.applySHA256(sb.toString());
    }
}
