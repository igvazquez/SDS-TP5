import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class Main {

    public static void main(String[] args) throws IOException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/resources/config.yml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        if(data.isEmpty()) {
            throw new IllegalArgumentException("No se han detectado argumentos.");
        }
        
        double minR = (double) data.get("minR");
        double maxR = (double) data.get("maxR");
        double maxV = (double) data.get("maxV");
        double maxMass = (double) data.get("maxMass");
        double beta = (double) data.get("beta");
        double tau =(double) data.get("tau");
        double l = (double) data.get("l");
        int n = (int) data.get("n");
        double d = (double) data.get("d");


        Board board = Board.getRandomBoard(n, d, l, Board.optM(20, maxR), minR, maxR, maxV, tau, beta, maxV, maxMass);
        CrowdSimulation cs = new CrowdSimulation(board, maxR, beta, tau);
        cs.simulate(1000, true);
    }
}
