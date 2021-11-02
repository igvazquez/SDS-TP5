import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        double minR = 0.15;
        double maxR = 0.32;
        double maxV = 2;
        double maxMass = 1;
        double beta = 0.9;
        double tau = 0.5;
        double ve = maxV;
        double l = 20;

//        Board board = Board.getRandomBoard(200, 20.0, Board.optM(20, maxR), minR, maxR, maxV, tau, beta, ve, maxMass);
        List<Particle> particles = new LinkedList<>();
        double[] v = Board.calculateVelocityToTarget(maxV, l, 10, 5);
        particles.add(new Particle(0, 10, 5, v[0], v[1], 1, maxR));
        v = Board.calculateVelocityToTarget(maxV, l, 9, 5);
        particles.add(new Particle(1, 9, 5, v[0], v[1], 1, maxR));
        v = Board.calculateVelocityToTarget(maxV, l, 11, 5);
        particles.add(new Particle(2, 11, 5, v[0], v[1], 1, maxR));
        Board board = new Board(l, minR, maxR, maxV, tau, beta, ve, Board.optM(l, maxR), particles);
        CrowdSimulation cs = new CrowdSimulation(board, maxR, beta, tau);
        cs.simulate(100);
    }
}
