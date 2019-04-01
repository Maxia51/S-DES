package main;

public class Main {

    public static void main(String[] args) {

        String key = "1010000010";
        String plainText = "10101010";

        SdesArrayList sdesArrayList = new SdesArrayList(key);
        String encrypted = sdesArrayList.encrypt(plainText);
        String decrypted = sdesArrayList.decrypt(encrypted);

        System.out.println("ArrList --");
        System.out.println("Plain   : " + plainText);
        System.out.println("Encrypt : " + encrypted);
        System.out.println("Decrypt : " + decrypted);

    }
}