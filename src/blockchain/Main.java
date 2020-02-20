package blockchain;

public class Main {
    public static void main(String[] args) {

        Blockchain blockchain = new Blockchain();
        blockchain.initialize(5);

        System.out.println(blockchain);
    }
}
