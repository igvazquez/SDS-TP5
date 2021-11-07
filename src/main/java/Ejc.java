import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Ejc {

    public static void main(String[] args) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/resources/config.yml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        if (data.isEmpty()) {
            throw new IllegalArgumentException("No se han detectado argumentos.");
        }

        double minR = (double) data.get("minR");
        double maxR = (double) data.get("maxR");
        double maxV = (double) data.get("maxV");
        double maxMass = (double) data.get("maxMass");
        double beta = (double) data.get("beta");
        double tau = (double) data.get("tau");
        double l = (double) data.get("l");

        int simulations = 30;
        List<Double> D = Arrays.asList(1.2, 1.8, 2.4, 3.0);
        List<Integer> N = Arrays.asList(200, 260, 320, 380);

        FileWriter pos = new FileWriter("ejC.csv", false);
        BufferedWriter buffer = new BufferedWriter(pos);
        buffer.write("d,simulation,t,n_t");
        buffer.newLine();

        for (int j = 0; j < D.size(); j++) {
            for (int i = 0; i < simulations; i++) {
                double d = D.get(j);
                int n = N.get(j);
                Board board = Board.getRandomBoard(n, d, l, Board.optM(20, maxR), minR, maxR, maxV, tau, beta, maxV, maxMass);
                CrowdSimulation cs = new CrowdSimulation(board, maxR, beta, tau);
                cs.simulate(100000, false);
                writeToCsv(buffer, cs.getOutputData(), d, i);
            }
        }
        buffer.flush();
        buffer.close();
        pos.close();
    }

    private static void writeToCsv(final BufferedWriter buffer, final OutputData outputData,final double d, final int i) throws IOException {
        for (Map.Entry<Double, Integer> entry : outputData.getEscapeData().entrySet()) {
            buffer.write(d + "," + i + "," + entry.getKey() + "," + entry.getValue());
            buffer.newLine();
        }
    }
}
