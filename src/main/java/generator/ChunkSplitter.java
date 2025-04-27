package generator;

import java.io.*;

public class ChunkSplitter {
    public static void main(String[] args) throws IOException {
        File inputFile = new File("src/main/resources/data.txt");
        long chunkSize = inputFile.length() / 10;

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        for (int i = 0; i < 10; i++) {
            FileWriter writer = new FileWriter("src/main/resources/chunks/chunk" + i + ".txt");
            long written = 0;
            String line;
            while ((line = reader.readLine()) != null && written < chunkSize) {
                writer.write(line + "\n");
                written += line.getBytes().length;
            }
            writer.close();
        }
        reader.close();
        System.out.println("Arquivo dividido em 10 chunks!");
    }
}