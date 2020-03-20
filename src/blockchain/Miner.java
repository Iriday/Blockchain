package blockchain;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Miner {
    private final BlockchainInterface blockchain;
    private final long numOfBlocks;
    private static long ids = 0;
    public final long id = ++ids;
    private final String name;
    private KeyPair keyPair;

    public Miner(BlockchainInterface blockchain, long numOfBlocks) {
        this.blockchain = blockchain;
        this.numOfBlocks = numOfBlocks < 0 ? -1 : numOfBlocks;
        name = "miner #" + id;
        Clients.addClient(name);
        try {
            keyPair = SignatureUtils.generateKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        generateBlock(); // mineBlock
    }

    private void generateBlock() {
        long startTime;
        Block thisBlock;
        long endTime;
        Object[] data;

        int i = numOfBlocks == -1 ? -2 : 0; // if numOfBlocks == -1 mine blocks infinitely
        while (i < numOfBlocks) {
            long coinsToSend = Clients.rand.nextInt(100) + 1;
            long hasCoins = blockchain.countUserCoins(name);
            if (hasCoins >= coinsToSend) {
                String randReceiver = Clients.getRandClientExceptThis(name);
                while (!blockchain.receiveNextData(new Transaction(blockchain.getNextBlockDataId(), name, coinsToSend, randReceiver, keyPair)))
                    ;
            }

            while ((data = blockchain.getDataForNewBlock()) == null) {
                Thread.yield();
            }

            startTime = System.currentTimeMillis();
            thisBlock = createBlock((String) data[0], (List<BlockData>) data[1], (int) data[2], name);
            endTime = System.currentTimeMillis();

             blockchain.receiveNextBlock(thisBlock, endTime - startTime);

            if (numOfBlocks != -1) i++;
        }
    }

    private static Block createBlock(String hashOfPrev, List<BlockData> data, int numOfZeros, String createdBy) {
        return new Block(hashOfPrev, data, numOfZeros, createdBy);
    }
}
