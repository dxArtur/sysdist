package coordinator;

import java.io.*;

public class Merger {
    public static void mergeOutputs() throws IOException {
        // 1) Debug: mostra de onde o programa está sendo executado
        System.out.println("Working directory: " + new File(".").getAbsolutePath());

        // 2) Diretório de entrada (resultados dos reducers)
        File inputDir = new File("src/main/resources/reducer_outputs");
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IOException("Input directory not found: " + inputDir.getAbsolutePath());
        }

        // 3) Garante que a pasta resources/final exista
        File outputDir = new File("src/main/resources/final");
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                System.out.println("Created output directory: " + outputDir.getAbsolutePath());
            } else {
                throw new IOException("Failed to create output directory: " + outputDir.getAbsolutePath());
            }
        }

        // 4) Arquivo final
        File outputFile = new File(outputDir, "final_result.txt");

        // 5) Merge dos arquivos
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);

            File[] files = inputDir.listFiles();
            for (File file : files) {
                if (!file.isFile()) continue;
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        bw.write(line);
                        bw.newLine();
                    }
                } finally {
                    if (br != null) br.close();
                }
            }
            bw.flush();
        } finally {
            if (bw != null) bw.close();
            if (fw != null) fw.close();
        }

        System.out.println("Merge completed! Final file at: " + outputFile.getAbsolutePath());
    }
}
