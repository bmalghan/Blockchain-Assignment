import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Random;

public class Block {
    private int index;
    private java.sql.Timestamp timestamp;

    private Transaction transaction;
    private String nonce;

    private String previousHash;

    private String hash;


    public Block(int index, Timestamp timestamp, Transaction transaction, String nonce, String previousHash, String hash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transaction = transaction;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.hash = hash;
    }

    public Block (int index, Transaction transaction, String previousHash) throws UnsupportedEncodingException {
        this.index = index;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.transaction = transaction;
        this.previousHash = previousHash;
        this.hash = findHash ();

    }

    public Block (int index, Timestamp timestamp, Transaction transaction, String nonce, String previousHash) throws UnsupportedEncodingException {
        this.index = index;
        this.transaction = transaction;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.hash = "";
    }
    /**
     * Finds Hash
     *
     * Generates a random nonce and then finds hash until the hash
     * starts with "00000". Then the nonce and hash is set for the instance.
     *
     * @throws UnsupportedEncodingException
     * @return Valid hash
     */

    public String findHash () throws UnsupportedEncodingException {
        Random random = new Random();

        StringBuilder tempNonce = new StringBuilder();
        nonce = "";
        hash = "123456";

        int num = 0;

        while (!hash.substring(0,5).equals("00000")){
            for (int i = 0; i < 24; i++){
                tempNonce.append((char)(33 + random.nextInt(126 - 33 +1)));
            }

            this.nonce = tempNonce.toString();
            tempNonce = new StringBuilder();
            this.hash = Sha1.hash(this.toString());
            num++;
        }
        System.out.println("Number of nonce iterations: "+num);
        return hash;
    }

    public int getIndex() {
        return index;
    }

    public Timestamp getTimestamp() {

        return timestamp;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getNonce() {

        return nonce;
    }

    public String getPreviousHash() {

        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public String toString ()
    {
        return timestamp.toString() + ":" + transaction.toString()+ "." +nonce+ previousHash;
    }
}
