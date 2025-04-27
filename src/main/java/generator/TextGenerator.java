package generator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TextGenerator {
    public static void main(String[] args) throws IOException {
    	File outputFile = new File("src/main/resources/data.txt");
        FileWriter writer = new FileWriter(outputFile);
        String[] words = {"funciona", "na", "minha", "maquina", "sistemas", "distribuidos", "map", "reduce", "system", "distributed"};
        Random rand = new Random();

        long totalBytes = 1024L * 1024L * 1024L; // ~1GB
        long writtenBytes = 0;

        while (writtenBytes < totalBytes) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < 50; i++) {
                line.append(words[rand.nextInt(words.length)]).append(" ");
            }
            line.append("\n");
            String str = line.toString();
            writer.write(str);
            writtenBytes += str.getBytes().length;
        }

        writer.close();
        System.out.println("Arquivo de ~1GB criado com sucesso em resources/data.txt");
        System.out.println(outputFile.getAbsolutePath());
    }
}
