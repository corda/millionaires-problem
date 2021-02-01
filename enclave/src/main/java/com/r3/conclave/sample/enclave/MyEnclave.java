package com.r3.conclave.sample.enclave;

import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.mail.EnclaveMail;
import com.r3.conclave.mail.MutableMail;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.*;

/**
 * TODO
 */

public class MyEnclave extends Enclave {

    HashMap<String, Integer> map = new HashMap<>();

    @Override
    protected void receiveMail(long id, String routingHint, EnclaveMail mail) {

        System.out.println("The enclave received mail with topic " + mail.getTopic());

        if (mail.getTopic().equals("SUBMIT")) {

            // Parse the amount and name from the mail object
            ByteBuffer buffer = ByteBuffer.wrap(mail.getBodyAsBytes());
            int amount = buffer.getInt();
            String name = new String(Arrays.copyOfRange(buffer.array(), 4, buffer.capacity()), StandardCharsets.UTF_8);

            map.put(name, amount);

            if (map.size() == 2) {
                String[] nameArray = map.keySet().toArray(new String[0]);
                String name1 = nameArray[0];
                String name2 = nameArray[1];
                int amt1 = map.get(name2);
                int amt2 = map.get(name2);
                if (amt1 > amt2) System.out.println("Winner: " + name1);
                if (amt1 < amt2) System.out.println("Winner: " + name2);
                if (amt1 == amt2) System.out.println("It's a tie!");
            }
        }
    }

    private static HashMap<String, Integer> sortByValueReversed(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer> > list = new LinkedList<>(hm.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        Collections.reverse(list);
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) { temp.put(aa.getKey(), aa.getValue()); }
        return temp;
    }

}
