package main;

import exception.IntegrityException;

import java.util.ArrayList;

public class SdesArrayList {

    private ArrayList<Boolean> master_key;


    private ArrayList<Boolean> keyOne = new ArrayList<>();
    private ArrayList<Boolean> keyTwo = new ArrayList<>();
    private ArrayList<Boolean> leftShifted = new ArrayList<>();
    private ArrayList<Boolean> rightShifted = new ArrayList<>();

    public SdesArrayList(String secretKey)
    {
        integrity(secretKey, 10);

        // Call StringToArrayList
        this.master_key = stringToArrayList(secretKey);

        // Should generate Key here
        generateKey(this.master_key);

        System.out.println(arrayListToString(this.keyOne));
        System.out.println(arrayListToString(this.keyTwo));

    }

    private void generateKey(ArrayList<Boolean> masterKey)
    {
        // First P10

        masterKey = p10(masterKey);

        // Split
        // create two empty lists
        ArrayList<Boolean> left = new ArrayList<Boolean>();
        ArrayList<Boolean> right = new ArrayList<Boolean>();

        splitInHalf(masterKey, left, right);

        // GenerateKey

        this.keyOne = keyOne(left, right);
        this.keyTwo = keyTwo(this.leftShifted, this.rightShifted);

    }

    public String encrypt(String plainTextString)
    {
        integrity(plainTextString, 8);

        ArrayList<Boolean> plainText = stringToArrayList(plainTextString);

        plainText = ip(plainText);

        ArrayList<Boolean> resultFin = new ArrayList<>();

        ArrayList<Boolean> left = new ArrayList<>();
        ArrayList<Boolean> right = new ArrayList<>();
        ArrayList<Boolean> result = new ArrayList<>();


        fk(plainText, left, right, result, this.keyOne);
        result = swap(result);
        left.clear();
        right.clear();
        fk(result, left, right, resultFin, this.keyTwo);

        // ip table -1

        resultFin = ipmin(resultFin);

        return arrayListToString(resultFin);

    }

    public String decrypt(String plainTextString)
    {
        integrity(plainTextString, 8);


        ArrayList<Boolean> plainText = stringToArrayList(plainTextString);

        plainText = ip(plainText);

        ArrayList<Boolean> resultFin = new ArrayList<>();

        ArrayList<Boolean> left = new ArrayList<>();
        ArrayList<Boolean> right = new ArrayList<>();
        ArrayList<Boolean> result = new ArrayList<>();


        fk(plainText, left, right, result, this.keyTwo);
        result = swap(result);
        left.clear();
        right.clear();
        fk(result, left, right, resultFin, this.keyOne);

        // ip table -1
        resultFin = ipmin(resultFin);

        return arrayListToString(resultFin);
    }

    private ArrayList<Boolean> swap(ArrayList<Boolean> result) {
        ArrayList<Boolean> swipped = new ArrayList<>();
        ArrayList<Boolean> left = new ArrayList<>();
        ArrayList<Boolean> right = new ArrayList<>();

        splitInHalf(result, left, right);

        swipped.addAll(right);
        swipped.addAll(left);

        return swipped;
    }

    private void fk(ArrayList<Boolean> plainText, ArrayList<Boolean> left, ArrayList<Boolean> right, ArrayList<Boolean> result, ArrayList<Boolean> key) {
        ArrayList<Boolean> rightMem;
        splitInHalf(plainText, left, right);
        rightMem = right;

        right = ep(right);


        // XOR Right with K1
        right = xor(right, key);

        ArrayList<Boolean> rightLeft = new ArrayList<>();
        ArrayList<Boolean> rightRight = new ArrayList<>();

        splitInHalf(right, rightLeft, rightRight);

        // S-Box
        rightLeft = sbox0(rightLeft);
        rightRight = sbox1(rightRight);


        // Concat both
        ArrayList<Boolean> rigthBoxed = new ArrayList<>();
        rigthBoxed.addAll(rightLeft);
        rigthBoxed.addAll(rightRight);

        rigthBoxed = p4(rigthBoxed);

        // XOR rightBoxed with init left

        ArrayList<Boolean> tmp = xor(left, rigthBoxed);

        result.addAll(tmp);
        result.addAll(rightMem);
    }

    private ArrayList<Boolean> sbox0(ArrayList<Boolean> rightLeft) {

        ArrayList<Integer> left = new ArrayList<>();

        String[][] sbox = new String[][]{
                { "01", "00", "11", "10"},
                { "11", "10", "01", "00"},
                { "00", "10", "01", "11"},
                { "11", "01", "11", "10"}
        };

        ArrayList<Boolean> resultBool = getBooleansBox(rightLeft, left, sbox);

        return resultBool;
    }

    private ArrayList<Boolean> sbox1(ArrayList<Boolean> rightRight) {
        ArrayList<Integer> right = new ArrayList<>();

        String[][] sbox = new String[][]{
                { "00", "01", "10", "11"},
                { "10", "00", "01", "11"},
                { "11", "00", "01", "00"},
                { "10", "01", "00", "11"}
        };

        ArrayList<Boolean> resultBool = getBooleansBox(rightRight, right, sbox);

        return resultBool;
    }


    private ArrayList<Boolean> getBooleansBox(ArrayList<Boolean> rightLeft, ArrayList<Integer> left, String[][] sbox) {
        for (Boolean element: rightLeft) {

            if (element == Boolean.TRUE){
                left.add(1);
            }else {
                left.add(0);
            }

        }

        // Get Row
        String a = String.valueOf(left.get(0));
        String b = String.valueOf(left.get(3));

        String ab = a.concat(b);

        Integer row = Integer.parseInt(ab, 2);


        // Get Col
        String c = String.valueOf(left.get(1));
        String d = String.valueOf(left.get(2));

        String cd = c.concat(d);

        Integer col = Integer.valueOf(cd, 2);

        String resultString = sbox[row][col];

        ArrayList<Boolean> resultBool = new ArrayList<>();

        char first = resultString.charAt(0);
        if (first == '1'){
            resultBool.add(Boolean.TRUE);
        } else {
            resultBool.add(Boolean.FALSE);
        }
        char second = resultString.charAt(1);
        if (second == '1'){
            resultBool.add(Boolean.TRUE);
        } else {
            resultBool.add(Boolean.FALSE);
        }
        return resultBool;
    }

    private ArrayList<Boolean> xor(ArrayList<Boolean> right, ArrayList<Boolean> keyOne) {

        ArrayList<Boolean> xor = new ArrayList<>();

        int size = right.size();

        for (int i = 0; i < size; i++){

            if(right.get(i) == keyOne.get(i)) {
                xor.add(Boolean.FALSE);
            } else {
                xor.add(Boolean.TRUE);
            }

        }

        return xor;

    }

    private ArrayList<Boolean> keyTwo(ArrayList<Boolean> leftShifted, ArrayList<Boolean> rightShifted) {

        // Left shift 2
        leftShifted = leftShift(leftShifted, 2);
        rightShifted = leftShift(rightShifted, 2);

        // Concat

        ArrayList<Boolean> keyTwoTmp = new ArrayList<>();

        keyTwoTmp.addAll(leftShifted);
        keyTwoTmp.addAll(rightShifted);

        // P8
        ArrayList<Boolean> keyTwo = p8(keyTwoTmp);

        return keyTwo;

    }

    /**
     * Generate KeyOne
     * @param left
     * @param right
     * @return
     */
    private ArrayList<Boolean> keyOne(ArrayList<Boolean> left, ArrayList<Boolean> right)
    {
        // Left Shift for both
        ArrayList<Boolean> leftShifted = leftShift(left, 1);
        ArrayList<Boolean> rightShifted = leftShift(right, 1);

        this.leftShifted = leftShifted;
        this.rightShifted = rightShifted;

        // Concat both
        ArrayList<Boolean> keyOneTmp = new ArrayList<>();

        keyOneTmp.addAll(leftShifted);
        keyOneTmp.addAll(rightShifted);

        // P8 table
        ArrayList<Boolean> keyOne = new ArrayList<>();
        keyOne = p8(keyOneTmp);

        return keyOne;
    }


    /**
     * Left shift
     * @param aL
     * @param shift
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> leftShift(ArrayList<T> aL, int shift)
    {
        if (aL.size() == 0)
            return aL;

        T element = null;
        for(int i = 0; i < shift; i++)
        {
            // remove last element, add it to front of the ArrayList
            element = aL.remove( 0 );
            aL.add(aL.size(), element);
        }

        return aL;
    }


    /**
     * Split shifted key in two
     * @param shiftedKey
     * @param left
     * @param right
     */
    private void splitInHalf(ArrayList<Boolean> shiftedKey, ArrayList<Boolean> left, ArrayList<Boolean> right) {
        // get size of the list
        int size = shiftedKey.size();

        // First size)/2 element copy into list
        // first and rest second list
        for (int i = 0; i < size / 2; i++)
            left.add(shiftedKey.get(i));

        // Second size)/2 element copy into list
        // first and rest second list
        for (int i = size / 2; i < size; i++)
            right.add(shiftedKey.get(i));
    }

    /**
     * Shift the key with P10 table
     * @param key
     * @return
     */
    private ArrayList<Boolean> p10(ArrayList<Boolean> key)
    {
        ArrayList<Boolean> shiftedKey = new ArrayList<>();
        Integer[] p10 = new Integer[] {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};

        for (Integer i : p10) {
            shiftedKey.add(key.get(i-1));
        }

        return shiftedKey;
    }

    /**
     * Shift the key with P8 table
     * @param key
     * @return
     */
    private ArrayList<Boolean> p8(ArrayList<Boolean> key)
    {
        ArrayList<Boolean> shiftedKey = new ArrayList<>();
        Integer[] p8 = new Integer[] {6, 3, 7, 4, 8, 5, 10, 9};

        for (Integer i : p8) {
            shiftedKey.add(key.get(i-1));
        }

        return shiftedKey;
    }


    /**
     * Shift the key with P4 table
     * @param key
     * @return
     */
    private ArrayList<Boolean> p4(ArrayList<Boolean> key)
    {
        ArrayList<Boolean> shiftedKey = new ArrayList<>();
        Integer[] p4 = new Integer[] {2, 4, 3, 1};

        for (Integer i : p4) {
            shiftedKey.add(key.get(i-1));
        }

        return shiftedKey;
    }


    private ArrayList<Boolean> ep(ArrayList<Boolean> left) {

        ArrayList<Boolean> shiftedKey = new ArrayList<>();
        Integer[] ep = new Integer[] {4, 1, 2, 3, 2, 3, 4, 1};

        for (Integer i : ep) {
            shiftedKey.add(left.get(i-1));
        }

        return shiftedKey;

    }

    private ArrayList<Boolean> ip(ArrayList<Boolean> plainText) {

        ArrayList<Boolean> shiftedKey = new ArrayList<>();
        Integer[] ip8 = new Integer[] {2, 6, 3, 1, 4, 8, 5, 7};

        for (Integer i : ip8) {
            shiftedKey.add(plainText.get(i-1));
        }

        return shiftedKey;

    }

    private ArrayList<Boolean> ipmin(ArrayList<Boolean> plainText) {

        ArrayList<Boolean> shiftedKey = new ArrayList<>();
        Integer[] ip8 = new Integer[] {4, 1, 3, 5, 7, 2, 8, 6};

        for (Integer i : ip8) {
            shiftedKey.add(plainText.get(i-1));
        }

        return shiftedKey;

    }

    private void integrity(String secretKey, int i) {

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

    /**
     * Compare and change 1 and 0 with True and False
     * @param key
     * @return
     */
    private ArrayList<Boolean> stringToArrayList(String key)
    {

        ArrayList<Boolean> master_key = new ArrayList<>();

        char[] arr = key.toCharArray();

        for (char element : arr) {
            if (element == '1'){
                master_key.add(Boolean.TRUE);
            } else {
                master_key.add(Boolean.FALSE);
            }
        }

        return master_key;

    }

    public String arrayListToString(ArrayList<Boolean> arr)
    {
        String result = "";

        for (Boolean element: arr) {
            if (element == Boolean.TRUE){
                result = result.concat("1");
            } else {
                result = result.concat("0");
            }
        }

        return result;
    }

    public ArrayList<Boolean> getMaster_key() {
        return master_key;
    }

}
