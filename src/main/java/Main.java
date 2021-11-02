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

        Board board = Board.getRandomBoard(200, l, Board.optM(20, maxR), minR, maxR, maxV, tau, beta, maxV, maxMass);
        /*List<Particle> particles = new LinkedList<>();
        double[] v = Board.calculateVelocityToTarget(maxV, l, 10, 5);
        particles.add(new Particle(0, 10, 5, v[0], v[1], 1, maxR));
        v = Board.calculateVelocityToTarget(maxV, l, 9, 5);
        particles.add(new Particle(1, 9, 5, v[0], v[1], 1, maxR));
        v = Board.calculateVelocityToTarget(maxV, l, 11, 5);
        particles.add(new Particle(2, 11, 5, v[0], v[1], 1, maxR));
        Board board = new Board(l, minR, maxR, maxV, tau, beta, ve, Board.optM(l, maxR), particles); */
        CrowdSimulation cs = new CrowdSimulation(board, maxR, beta, tau);
        cs.simulate(1000);
    }
}
