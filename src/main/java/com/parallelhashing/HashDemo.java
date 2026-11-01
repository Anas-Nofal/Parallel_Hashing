
package com.parallelhashing;

import com.parallelhashing.hashing.HashUtils;

public class HashDemo {

    public static void main(String[] args) {

        String word = "58ige";

        String hash = HashUtils.sha256(word);

        System.out.println("Original word: " + word);
        System.out.println("SHA-256 hash: " + hash);
    }
}
