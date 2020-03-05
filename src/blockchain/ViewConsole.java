package blockchain;

import java.util.List;
import java.util.Scanner;

public class ViewConsole implements ViewInterface, Observer {
    private ControllerInterface controller;
    private ModelInterface model;
    private BlockchainInterface blockchain;
    private long blockId = 0;

    @Override
    public void initialize(ControllerInterface controller, ModelInterface model) {
        this.controller = controller;
        this.model = model;
        this.blockchain = model.getBlockchain();
        blockchain.registerObserver(this);
    }

    @Override
    public int input() {
        var scn = new Scanner(System.in);
        int input;

        while (true) {
            System.out.print("Enter how many zeros the hash must start with or -1(autoregulation): ");
            input = scn.nextInt();
            if (input > 64) {
                System.out.println("Incorrect input, value should be <=64");
                continue;
            }
            break;
        }
        return input;
    }

    private void output(String data) {
        System.out.println(data);
        if (blockId % 10 != 0) return;

        System.out.println(blockchain.isBlockchainHacked() ? "\nWow, that's unbelievable! The blockchain is HACKED!" : "\nThe blockchain is not hacked.");
    }

    @Override
    public void update(long blockId, long timestamp, int magicNumber, String hashOfPrev, String hashOfThis, long blockTime, long minerId, int numOfZeros, int numOfZerosChange, List<BlockData> data) {
        var sb = new StringBuilder();

        sb.append("\nBlock:\nCreated by miner # ");
        sb.append(minerId);
        sb.append("\nId: ");
        sb.append(blockId);
        sb.append("\nTimestamp: ");
        sb.append(timestamp);
        sb.append("\nMagic number: ");
        sb.append(magicNumber);
        sb.append("\nHash of the previous block:\n");
        sb.append(hashOfPrev);
        sb.append("\nHash of the block:\n");
        sb.append(hashOfThis);
        sb.append("\nBlock data: ");
        data.forEach(message -> {
            sb.append(message.getData());
            if (message.getId() != -1) {
                sb.append(" /data id: ");
                sb.append(message.getId());
            }
        });
        sb.append("\nBlock was generating for ");
        sb.append(blockTime / 1000.0);
        sb.append(" seconds");

        if (numOfZerosChange != Integer.MAX_VALUE) {
            if (numOfZerosChange == 0) {
                sb.append("\nN stays the same");
            } else if (numOfZerosChange < 0) {
                sb.append("\nN was decreased to ");
                sb.append(numOfZeros);
            } else {
                sb.append("\nN was increased to ");
                sb.append(numOfZeros);
            }
        }

        this.blockId = blockId;
        output(sb.toString());
    }
}
