package blockchain;

import java.util.Scanner;

public class ViewConsole implements Observer {
    private final Controller controller;
    private final BlockchainModelInterface model;
    private int blockCounter = 0;

    public ViewConsole(Controller controller, BlockchainModelInterface model) {
        this.controller = controller;
        this.model = model;
        model.registerObserver(this);
        controller.start(input());
    }

    int input() {
        var scn = new Scanner(System.in);
        int input;

        while (true) {
            System.out.print("Enter how many zeros the hash must start with: ");
            input = scn.nextInt();
            if (input < 0 || input > 64) {
                System.out.println("Incorrect input, number should be >=0 && <=64");
                continue;
            }
            break;
        }
        return input;
    }

    private void output(String data) {
        System.out.println(data);
        if (++blockCounter != 5) return;

        System.out.println(model.isBlockchainHacked() ? "\nWow, that's unbelievable! The blockchain is HACKED!" : "\nThe blockchain is not hacked.");
    }

    @Override
    public void update(long blockId, long timestamp, int magicNumber, String hashOfPrev, String hashOfThis, long blockTime) {
        var sb = new StringBuilder();

        sb.append("\nBlock:\nId: ");
        sb.append(blockId);
        sb.append("\nTimestamp: ");
        sb.append(timestamp);
        sb.append("\nMagic number: ");
        sb.append(magicNumber);
        sb.append("\nHash of the previous block:\n");
        sb.append(hashOfPrev);
        sb.append("\nHash of the block:\n");
        sb.append(hashOfThis);
        sb.append("\nBlock was generating for ");
        sb.append(blockTime / 1000.0);
        sb.append(" seconds");

        output(sb.toString());
    }
}
