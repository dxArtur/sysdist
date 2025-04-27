package coordinator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import redis.clients.jedis.Jedis;

public class Shuffle {

    public static void shuffle(Jedis jedis, int numReducers) {
        for (int mapperId = 0; mapperId < 10; mapperId++) {
            List<String> output = jedis.lrange("mapper_" + mapperId + "_output", 0, -1);
            Map<String, List<Integer>> grouped = groupByKey(output);

            for (Map.Entry<String, List<Integer>> entry : grouped.entrySet()) {
                int reducerId = Math.abs(entry.getKey().hashCode()) % numReducers;
                writeToReducerFile(entry.getKey(), entry.getValue(), reducerId);
            }

            jedis.publish("shuffle_done", "shuffle_phase_completed");
        }
    }

    private static Map<String, List<Integer>> groupByKey(List<String> output) {
        Map<String, List<Integer>> grouped = new HashMap<String, List<Integer>>();
        for (String entry : output) {
            String[] parts = entry.split(",");
            String key = parts[0].trim();
            int value = Integer.parseInt(parts[1].trim());

            List<Integer> list = grouped.get(key);
            if (list == null) {
                list = new ArrayList<Integer>();
                grouped.put(key, list);
            }
            list.add(value);
        }
        return grouped;
    }


    private static void writeToReducerFile(String key, List<Integer> values, int reducerId) {
        String fileName = "reducer_" + reducerId + "_input.json";
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write("{\"" + key + "\": " + values + "}\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java coordinator.Shuffle <numReducers>");
            System.exit(1);
        }

        int numReducers = Integer.parseInt(args[0]);
        Jedis jedis = null;
        try {
            jedis = new Jedis("127.0.0.1", 6379);
            System.out.println("Iniciando fase de shuffle com " + numReducers + " reducers...");
            shuffle(jedis, numReducers);
            System.out.println("Shuffle finalizado.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
