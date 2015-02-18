package org.sphindroid.sample.command.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by mondhs on 15.2.16.
 */
public class ResponseCatalog {
    List<String> responses;
    public ResponseCatalog(String... responseArr){
        this.responses = new ArrayList<>(Arrays.asList(responseArr));
    }

    public ResponseCatalog(){
        this.responses = new ArrayList<>();
    }

    public String anyItem(){
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(responses.size());
        return responses.get(index);
    }
}
