# S-DES JAVA

This is the implementation of Simplified DES algorithm.
This Algorithm is for educational purposes only.


How to use it ? : 

```java
    String key = "1010000010";
    String plainText = "10101010";

    SdesArray sdesArray = new SdesArray(key);
    String encrypted = sdesArray.encrypt(plainText);
    String decrypted = sdesArray.decrypt(encrypted);

    System.out.println("Arr --");
    System.out.println("Plain   : " + plainText);
    System.out.println("Encrypt : " + encrypted);
    System.out.println("Decrypt : " + decrypted);
```