package com.line.line_demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        List<List<String>> res = splitIntoBatches(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), 3);
        System.out.println(res);
    }

    public static List<List<String>> splitIntoBatches(List<String> messages, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < messages.size(); i += batchSize) {
            batches.add(messages.subList(i, Math.min(i + batchSize, messages.size())));
        }
        return batches;
    }

}
