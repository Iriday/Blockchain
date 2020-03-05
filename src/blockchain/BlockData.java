package blockchain;

import java.io.Serializable;

public interface BlockData extends Serializable {
    long getId();

    String getData();
}
