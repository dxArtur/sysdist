package coordinator;
import java.io.IOException;

import redis.clients.jedis.Jedis;



public class Coordinator {
	

	public static void main(String[] args)  {
		 Jedis jedis = new Jedis("127.0.0.1", 6379);
		 
		 for (int i = 0; i < 10; i++) {
			 jedis.lpush("mapper_queue", "chunk", i+".txt");
		 }
		 
		 Shuffle.shuffle(jedis, 5);
		 
		 for (int i =  0; i<4; i++) {
			 jedis.lpush("reducer_queue", "reducer_input_", i+".json");
			 
		 }
		 try {
			Merger.mergeOutputs();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
}
