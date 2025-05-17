package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.jackson.dataformat.MessagePackMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MsgTest {
    // This is thread safe
    public static final ObjectMapper objectMapper = new MessagePackMapper();

    public static void main(String[] args) {
        try {
            ArrayList<Integer> l = new ArrayList<>();
            l.add(1); l.add(2); l.add(3);
            byte[] b = objectMapper.writeValueAsBytes(l);
            List<Integer> i = objectMapper.readValue(b, LinkedList.class);
            System.out.println(i);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
