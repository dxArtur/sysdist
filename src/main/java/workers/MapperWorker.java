package workers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class MapperWorker {

	//processa cada linha de texto
	public static List<Map.Entry<String, Integer>> mapFunction(String line) {
        List<Map.Entry<String, Integer>> result = new ArrayList<Map.Entry<String, Integer>>();
        String[] words = line.split("\\s+");
        for (String word : words) {
            result.add(new AbstractMap.SimpleEntry<String, Integer>(word, 1));
        }
        return result;
    }
	
	 public static void mapper(int workerId, String chunkFile) {
	        Jedis jedis = new Jedis("127.0.0.1", 6379); // Conectar ao Redis
	        try {
	            BufferedReader reader = new BufferedReader(new FileReader(chunkFile));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                List<Map.Entry<String, Integer>> pairs = mapFunction(line);
	                for (Map.Entry<String, Integer> pair : pairs) {
	                    // Armazenar os pares chave-valor no Redis (em uma lista)
	                    jedis.rpush("mapper_" + workerId + "_output", pair.getKey() + "," + pair.getValue());
	                }
	            }
	            // Avisar que o trabalho do Mapper foi concluído
	            jedis.publish("mapper_done", "mapper_" + workerId + "_finished");
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            jedis.close();
	        }
	    }
	 
	 public static void main(String[] args) {
	        if (args.length != 2) {
	            System.out.println("Uso: java MapperWorker <workerId> <chunkFile>");
	            System.exit(1);
	        }

	        int workerId = Integer.parseInt(args[0]);
	        String chunkFile = args[1];

	        // Chama o método mapper com os parâmetros passados
	        mapper(workerId, chunkFile);

	        System.out.println("Mapper " + workerId + " finalizado para o arquivo: " + chunkFile);
	    }
}
