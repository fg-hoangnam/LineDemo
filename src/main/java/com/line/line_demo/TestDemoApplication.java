package com.line.line_demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TestDemoApplication {

    public static void main(String[] args) {

        List<List<Integer>> result = new ArrayList<>();
        List<Integer> original = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        int totalSize = original.size();
        int limit = 8;
        if (original.size() > limit) {
            for (int i = 0; i < totalSize; i = i + (i + limit > totalSize ? totalSize - i : limit)) {
                result.add(new ArrayList<>(original.subList(i, i + (i + limit > totalSize ? totalSize - i : limit))));
            }
        }
        System.out.println(result);
    }

}
