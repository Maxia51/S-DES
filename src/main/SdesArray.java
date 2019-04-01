package main;

import exception.IntegrityException;

import java.util.Arrays;

public class SdesArray {

    private boolean[] masterKey;
    private boolean[] keyOne;
    private boolean[] keyTwo;

    public SdesArray(String key)
    {
        integrity(key, 10);

        this.masterKey = convertStringToBool(key);

        generateKeys();

        System.out.println(convertBoolToString(this.keyOne));
        System.out.println(convertBoolToString(this.keyTwo));
    }

    public String encrypt(String plainText)
    {
        integrity(plainText, 8);

        return convertBoolToString(rip(fk(this.keyTwo, swap(fk(this.keyOne, ip(convertStringToBool(plainText)))))));
    }

    public String decrypt(String plainText)
    {
        integrity(plainText, 8);

        return convertBoolToString(rip(fk(this.keyOne, swap(fk(this.keyTwo, ip(convertStringToBool(plainText)))))));
    }


    private boolean[] swap(boolean[] fk) {

        return combine(Arrays.copyOfRange(fk, 4, 8), Arrays.copyOfRange(fk, 0, 4));

    }

    private boolean[] fk(boolean[] key, boolean[] arr) {

        // Split
        boolean[] left = Arrays.copyOfRange(arr, 0, 4);
        boolean[] right = Arrays.copyOfRange(arr, 4, 8);

        //Xor
        boolean[] xor = xor(ep(right), key);

        return combine(xor(p4(combine(sbox(Arrays.copyOfRange(xor, 0, 4)), sbox1(Arrays.copyOfRange(xor, 4, 8)))), left), right);

    }


    private boolean[] sbox(boolean[] arr) {

        boolean[][][] sbox = new boolean[][][]{
                { {false, true}, {false, false}, {true, true}, {true, false}},
                { {true, true}, {true, false}, {false, true}, {false, false}},
                { {false, false}, {true, false}, {false, true}, {true, true}},
                { {true, true}, {false, true}, {true, true}, {true, false}}
        };

        return getBooleans(sbox, arr);
    }


    private boolean[] sbox1(boolean[] arr) {

        boolean[][][] sbox = new boolean[][][]{
                { {false, false}, {false, true}, {true, false}, {true, true}},
                { {true, false}, {false, false}, {false, true}, {true, true}},
                { {true, true}, {false, false}, {false, true}, {false, false}},
                { {true, false}, {false, true}, {false, false}, {true, true}}
        };

        return getBooleans(sbox, arr);
    }

    private boolean[] getBooleans(boolean[][][] sbox, boolean[] arr) {
        int a = arr[0] ? 1 : 0;
        int b = arr[1] ? 1 : 0;
        int c = arr[2] ? 1 : 0;
        int d = arr[3] ? 1 : 0;

        int row = Integer.valueOf(String.valueOf(a).concat(String.valueOf(d)),2);
        int col = Integer.valueOf(String.valueOf(b).concat(String.valueOf(c)),2);

        return sbox[row][col];
    }

    private void generateKeys()
    {
        boolean[] globalKey = new boolean[10];

        // P10
        globalKey = p10(this.masterKey);

        this.keyOne = p8(combine(shift(Arrays.copyOfRange(globalKey, 0, 5)), shift(Arrays.copyOfRange(globalKey, 5, 10))));
        this.keyTwo = p8(combine(shift(shift(shift(Arrays.copyOfRange(globalKey, 0, 5)))), shift(shift(shift(Arrays.copyOfRange(globalKey, 5, 10))))));
    }

    /**
     * Combine left and right array
     * @param left
     * @param right
     * @return
     */
    private boolean[] combine(boolean[] left, boolean[] right){
        boolean[] result = new boolean[left.length + right.length];

        for (int i = 0; i < left.length; i++)
            result[i] = left[i];

        int i = left.length;

        for (int j = 0; j < right.length; j++) {
            result[i] = right[j];
            i++;
        }

        return result;
    }

    /**
     * left shift of only one bit
     * @param arr
     * @return
     */
    private boolean[] shift(boolean[] arr){
        boolean firstElement = arr[0]; // Store first index
        for(int i = 1; i< arr.length;i++){
            arr[i-1]=arr[i];
        }
        arr[arr.length-1] = firstElement; //set first index to the last index

        return arr;
    }

    /**
     * Xor table
     * @param a
     * @param b
     * @return
     */
    private boolean[] xor(boolean[] a, boolean[] b)
    {
        boolean[] res = new boolean[a.length];
        for (int i = 0; i < a.length; i++){
            if (a[i] == b[i]){
                res[i] = false;
            } else {
                res[i] = true;
            }
        }

        return res;
    }

    private boolean[] ip(boolean[] arr)
    {
        return new boolean[]{ arr[1], arr[5], arr[2], arr[0], arr[3], arr[7], arr[4], arr[6]};
    }

    private boolean[] rip(boolean[] arr)
    {
        return new boolean[]{ arr[3], arr[0], arr[2], arr[4], arr[6], arr[1], arr[7], arr[5]};
    }

    private boolean[] ep(boolean[] arr) {
        return new boolean[]{ arr[3], arr[0], arr[1], arr[2], arr[1], arr[2], arr[3], arr[0]};
    }

    /**
     * P10 table
     * @param arr
     * @return
     */
    private boolean[] p10(boolean[] arr)
    {
        return new boolean[]{ arr[2], arr[4], arr[1], arr[6], arr[3], arr[9], arr[0], arr[8], arr[7], arr[5]};
    }

    /**
     * P8 table
     * @param arr
     * @return
     */
    private boolean[] p8(boolean[] arr)
    {
        return new boolean[]{ arr[5], arr[2], arr[6], arr[3], arr[7], arr[4], arr[9], arr[8]};
    }

    private boolean[] p4(boolean[] arr)
    {
        return new boolean[]{ arr[1], arr[3], arr[2], arr[0]};
    }

    /**
     * Convert a String made with 0 and 1 to an array of bool
     * @param text
     * @return
     */
    private boolean[] convertStringToBool(String text)
    {

        boolean[] res = new boolean[text.length()];

        for (int i = 0; i < text.length(); i++) {
            res[i]=text.charAt(i) == '1';
        }

        return res;
    }

    /**
     * Convert a array of bool to string
     * @param arr
     * @return
     */
    private String convertBoolToString(boolean[] arr)
    {
        String res = "";
        for (int i = 0; i < arr.length; i++){
            if (arr[i]){
                res = res.concat("1");
            }else {
                res = res.concat("0");
            }
        }
        return res;
    }

    /**
     * Check the integrity of String, accept only 1 and 0, and the length given
     * @param secretKey text
     * @param i length
     */
    private void integrity(String secretKey, int i)
    {

        char[] arr = secretKey.toCharArray();

        if (arr.length > i || arr.length < i) {
            throw new IntegrityException("Secret key should be "+ i +" bit long");
        }

        for (char element : arr) {
            if (element != '0' && element != '1') {
                throw new IntegrityException("Secret key should be only composed by 0 and 1");
            }
        }
    }

}
