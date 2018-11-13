
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockChain {

    private static ArrayList <Block> blockChainArray;



    public BlockChain(ArrayList<Block> blockChain) {

        this.blockChainArray = blockChain;
    }

    /**
     * Creates the blockchain.
     *
     * Uses BufferReader to read through each line and create a blockchain.
     *
     * @param fileName Name of file being read.
     * @return BlockChain
     */

    public static BlockChain fromFile (String fileName) {

        ArrayList <String> listOfLines = new ArrayList<String>();
        ArrayList <Block> blockChain = new ArrayList<Block>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int num = 0;
            while ((line = br.readLine()) != null) {
                listOfLines.add(line);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        for (int i =0; i < listOfLines.size()/7 ; i++){
            int index = (Integer.parseInt(listOfLines.get(i*7)));
            Timestamp timestamp = new Timestamp(Long.parseLong(listOfLines.get(1+i*7)));
            String sender = listOfLines.get(2+i*7);
            String receiver = listOfLines.get(3+i*7);
            int amount = (Integer.parseInt(listOfLines.get(4+i*7)));
            String nonce = listOfLines.get(5+i*7);
            String hash = listOfLines.get(6+i*7);

            String previousHash = "";
            if (i == 0){
                previousHash = "00000";
            }
            else {
                previousHash = listOfLines.get(6+(i-1)*7);;
            }

            Block block = new Block(index, timestamp,new Transaction(sender, receiver, amount), nonce, previousHash, hash);
            blockChain.add(block);


        }

        return new BlockChain(blockChain);

    }

    /**
     * Writes Blockchain to text file
     *
     * Uses BufferWriter to write the blockchain into a text file.
     *
     * @param fileName Name of file being written to.
     * @throws IOException
     */

    public void toFile (String fileName) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName+".txt"), "utf-8"));


        for (int i = 0; i < getBlockChain().size(); i++){
            writer.write(getBlockChain().get(i).getIndex() + "\n");
            writer.write(Long.toString(getBlockChain().get(i).getTimestamp().getTime())+ "\n");
            writer.write(getBlockChain().get(i).getTransaction().getSender()+ "\n");
            writer.write(getBlockChain().get(i).getTransaction().getReceiver()+ "\n");
            writer.write(getBlockChain().get(i).getTransaction().getAmount()+ "\n");
            writer.write(getBlockChain().get(i).getNonce()+ "\n");
            writer.write(getBlockChain().get(i).getHash()+ "\n");
        }

        writer.close();
    }
    /**
     * Validates BlockChain
     *
     * First the blockchain is checked by seeing if it is in the right sequence
     * (looking at prevHash and if index matches. Then the method goes through
     * and checks if each user has only spent what the have and if the balances
     * matchup. And finally test if the hash produced is valid by using the
     * nonce and other values to findHash()
     *
     * @throws UnsupportedEncodingException
     * @return boolean (valid or not valid)
     */

    public boolean validateBlockChain () throws UnsupportedEncodingException {

        //hash of previous matches previousHash of current
        for (int i = 0; i < getBlockChain().size(); i++){
            if (getBlockChain().get(i).getIndex() == i){
                if (i == 0){
                    if (getBlockChain().get(i).getPreviousHash() != "00000") {
                        return false;
                    }
                }
                else {
                    if (getBlockChain().get(i).getPreviousHash() != getBlockChain().get(i-1).getHash()){
                        return false;
                    }
                }
            }
            else {
                return false;
            }
        }

        //verify the balance of each user
        ArrayList <String> listOfUsers = new ArrayList<>();
        ArrayList <Integer> balance = new ArrayList<>();
        for (Block block:getBlockChain()){
            String user1 = block.getTransaction().getSender();
            String user2 = block.getTransaction().getReceiver();
            if (!listOfUsers.contains(user1)){
                listOfUsers.add(user1);
                balance.add(0);
            }
            if (!listOfUsers.contains(user2)){
                listOfUsers.add(user2);
                balance.add(0);
            }
        }

        for (Block block:getBlockChain()){
            String sender = block.getTransaction().getSender();
            int i = listOfUsers.indexOf(sender);
            int balance1 = balance.get(i) - block.getTransaction().getAmount();
            String receiver = block.getTransaction().getReceiver();
            int j = listOfUsers.indexOf(receiver);
            int balance2 = balance.get(j) + block.getTransaction().getAmount();
            balance.set(i, balance1);
            balance.set(j, balance2);
        }

        for (int i =1; i < balance.size(); i++){
            if (balance.get(i) < 0){
                return false;
            }
        }

        //Check of hash generated is correct given all other values
        for (int i = 0; i < getBlockChain().size();i++){
            Block tempBlock = getBlockChain().get(i);
            Block testBlock = new Block (tempBlock.getIndex(),tempBlock.getTimestamp(),tempBlock.getTransaction(), tempBlock.getNonce(), tempBlock.getPreviousHash());
            if(!tempBlock.getHash().equals(Sha1.hash(testBlock.toString()))){
                return false;
            }
        }

        return true;
    }


    /**
     * Gets Balance of the user.
     *
     * Goes through the blockchain and moniters the transaction of the user
     * given and keeps track of the balance.
     *
     * @param username Name of user being used.
     * @return balance
     */

    public int getBalance (String username){

        int balance = 0;
        for (Block block: getBlockChain()){
            if (block.getTransaction().getSender().equals(username)){
                balance -= block.getTransaction().getAmount();
            }
            else if (block.getTransaction().getReceiver().equals(username)){
                balance += block.getTransaction().getAmount();
            }
        }
        return balance;
    }

    /**
     * Adds to BlockChain
     *
     * Adds the given block to the blockchain array.
     *
     * @param block Name of block being added.
     */
    public void add(Block block){
        getBlockChain().add(block);
    }
    /**
     * Get BlockChainArray
     *
     * returns the BlockChainArray
     *
     * @return blockcChainArray
     */

    public ArrayList<Block> getBlockChain() {
        return blockChainArray;
    }

    /**
     * Validates the blockchain
     *
     * Validates the given textfile by calling the fromFile
     * method and then isValid method to determine if the
     * file is a valid blockchain.
     *
     * @param fileName Name of blockchain being validated.
     * @throws UnsupportedEncodingException
     * @return boolean Valid or not valid.
     */

    public String validator (String fileName) throws UnsupportedEncodingException {
        BlockChain blockChain = fromFile(fileName+".txt");
        if(blockChain.validateBlockChain()){
            return ("This is a VALID blockchain!");
        }
        else{
            return ("This is not a VALID blockchain!");
        }
    }

    /**
     * Main method
     *
     * Asks for user inputs to add new transactions to the blockchain
     *
     * @throws IOException
     */
    public static void main (String args []) throws IOException {
        //ENTER FILENAME HERE TO FIND OUT IF VALID.
        System.out.println(new BlockChain(new ArrayList<>()).validator("bitcoinBank_bmalg082"));

        boolean newTrans = true;
        String fileName = "";
        Scanner in = new Scanner(System.in);

        //Get file name
        System.out.println("Enter name of blockchain file: ");
        fileName = in.nextLine();
        if (fromFile(fileName + ".txt").validateBlockChain()) {
            BlockChain blockChain = fromFile(fileName+".txt");
            while (newTrans) {

                //Get name of sender, receiver, and amount
                boolean bool = true;
                while (bool) {

                    System.out.println("Name of Sender: ");
                    String sender = in.nextLine();

                    System.out.println("Name of receiver ");
                    String receiver = in.nextLine();

                    System.out.println("Enter Amount: ");
                    int amount = Integer.parseInt(in.nextLine());
                    bool = blockChain.getBalance(sender) < amount;
                    if (bool) {
                        System.out.println("That is an invalid transaction, please try again.");
                    }
                    else{
                        //create block and add to blockchain
                        Block block = new Block (blockChain.getBlockChain().size(), new Transaction(sender, receiver, amount),blockChain.getBlockChain().get(blockChain.getBlockChain().size()-1).getHash());
                        blockChain.add(block);
                    }
                }

                System.out.println("Would you like to make another transaction?(Y/N)");
                if (in.nextLine().equals("Y")) {
                    newTrans = true;
                } else {
                    blockChain.toFile(fileName+"_bmalg082");
                    break;
                }
            }
        }
        else {
            System.out.println("Try Again");
            System.exit(1);
        }
    }
}
