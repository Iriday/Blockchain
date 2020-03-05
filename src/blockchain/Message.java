package blockchain;

import java.io.Serializable;

public class Message implements BlockData, Serializable {

    private final String message;
    private final long id;

    public Message(String message, long id) {
        this.message = message;
        this.id = id;
    }

    @Override
    public String getData() {
        return message;
    }


    @Override
    public long getId() {
        return id;
    }


    public static Message getEmptyData() {
        return new Message("no data", -1);
    }
}
