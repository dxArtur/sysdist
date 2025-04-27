package workers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class ReducerWorker {

    public static int reduceFunction(List<Integer> values) {
        int sum = 0;
        for (int val : values) {
            sum += val;
        }
        return sum;
    }

    public static void reducer(int workerId, String inputFile) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, List<Integer>> data = new HashMap<String, List<Integer>>();
                String key = line.split(":")[0].replace("{", "").trim();
                String valuesStr = line.split(":")[1].replace("}", "").trim();
                
                String[] valueStrings = valuesStr.split(",");
                List<Integer> values = new ArrayList<Integer>();
                for (int i = 0; i < valueStrings.length; i++) {
                    values.add(Integer.parseInt(valueStrings[i].trim()));
                }

                data.put(key, values);

                for (Map.Entry<String, List<Integer>> entry : data.entrySet()) {
                    int result = reduceFunction(entry.getValue());
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter("reducer_" + workerId + "_output.txt", true));
                        writer.write(entry.getKey() + ": " + result + "\n");
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
            }

            jedis.publish("reducer_done", "reducer_" + workerId + "_finished");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            jedis.close();
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Uso: java workers.ReducerWorker <workerId> <inputFile>");
            System.exit(1);
        }
        int workerId = Integer.parseInt(args[0]);
        String inputFile = args[1];
        System.out.println("Reducer " + workerId + " iniciando com arquivo: " + inputFile);
        reducer(workerId, inputFile);
        System.out.println("Reducer " + workerId + " finalizado.");
    }
}
