package blockchain;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        Blockchain blockchain = new Blockchain();
        int input = input();

        blockchain.initialize(5, input);

        System.out.println(blockchain);
        System.out.println(blockchain.isBlockchainHacked() ? "Wow, that's unbelievable! The blockchain is HACKED!" : "The blockchain is not hacked.");
    }

    private int input() {
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
}
