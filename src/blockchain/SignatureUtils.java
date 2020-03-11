package blockchain;

import java.security.*;

public class SignatureUtils {

    public static KeyPair generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(1024);

        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] createSignature(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA1withDSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());

        return signature.sign();
    }

    public static boolean verifyData(String data, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withDSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());

        return sig.verify(signature);
    }
}
