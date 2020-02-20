package blockchain;

public class Block {
    private static int ids = 0;
    public final long id = ++ids;
    public final long timeStamp;
    public final String hashOfPrev;
    public final String hashOfThis;

    public Block(String hashOfPrev) {
        timeStamp = System.currentTimeMillis();
        this.hashOfPrev = hashOfPrev;
        hashOfThis = createHash();
    }

    private String createHash() {
        var sb = new StringBuilder();
        sb.append(ids);
        sb.append(id);
        sb.append(timeStamp);
        sb.append(hashOfPrev);
        return Utils.applySHA256(sb.toString());
    }
}
