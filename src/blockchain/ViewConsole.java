package blockchain;

import java.util.Scanner;

public class ViewConsole implements Observer {
    private final Controller controller;
    private final BlockchainModelInterface model;

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
            System.out.println();
            break;
        }
        return input;
    }

    private void output() {
        System.out.println(model);
        System.out.println(model.isBlockchainHacked() ? "Wow, that's unbelievable! The blockchain is HACKED!" : "The blockchain is not hacked.");
    }

    @Override
    public void update() {
        output();
    }
}
