import lombok.Data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Data
public class CrowdSimulation {

    private List<List<Particle>> states = new LinkedList<>();
    private final Board board;
    private final double rc;
    private final double beta;
    private final double tau;
    private double t;
    private final OutputData outputData;

    public CrowdSimulation(final Board board, final double rc, final double beta, final double tau) {
        this.board = board;
        this.rc = rc;
        this.beta = beta;
        this.tau = tau;
        this.t = 0;
        this.outputData = new OutputData();
    }

    public void simulate(final int maxIter, final boolean logToFile) throws IOException {
        List<Particle> currentState = board.getParticles();
        states.add(currentState);
        CellIndexMethod cim;

        int i = 0;
        while (!currentState.isEmpty() && i < maxIter) {
            if(i++ % 500 == 0){
                System.out.println("Iter: " + i);
            }
            cim = new CellIndexMethod(board, board.getMaxR(), false);
            cim.calculateNeighbours();
            currentState = doStep(currentState, cim);
            board.updateParticles(currentState);
            states.add(currentState);
        }
        if (logToFile){
            writeBoardToFile();
            outputData.writeTimesToFile();
        }
    }

    private List<Particle> doStep(final List<Particle> currentState, final CellIndexMethod cim) {
        this.t += board.getDt();
        List<Particle> nextState = new ArrayList<>(currentState.size());
        Map<Integer, Set<Particle>> neighbours = cim.getNeighboursMap();
        for (Particle p : currentState) {
            Particle newParticle = board.advanceParticle(p, neighbours.get(p.getId()));

            if(newParticle.getY() > 0) {
                nextState.add(newParticle);
            }
        }
        outputData.getEscapeData().put(this.t, 200 - nextState.size());
        return nextState;
    }

    private void writeBoardToFile() throws IOException {
        FileWriter pos = new FileWriter("testBoard.xyz", false);
        BufferedWriter buffer = new BufferedWriter(pos);
        for(List<Particle> particles : states) {
            buffer.write(String.valueOf(particles.size() + 12));
            buffer.newLine();
            buffer.newLine();
            writeDummyParticles(buffer);
            for(Particle p : particles) {
                buffer.write(p.getId() + " " + p.getX() + " " + p.getY() + " " + p.getVx() + " " + p.getVy() + " " + p.getRadius());
                buffer.newLine();
            }
        }
        buffer.flush();
        buffer.close();
        pos.close();
    }

    private void writeDummyParticles(final BufferedWriter buffer) throws IOException {
        buffer.write("201 0 0 0 0 0.0001");
        buffer.newLine();
        buffer.write("202 20 0 0 0 0.0001");
        buffer.newLine();
        buffer.write("203 0 20 0 0 0.0001");
        buffer.newLine();
        buffer.write("204 20 20 0 0 0.0001");
        buffer.newLine();
        buffer.write("214 5 18 0 0 0.0001");
        buffer.newLine();
        buffer.write("215 15 18 0 0 0.0001");
        buffer.newLine();
        buffer.write("216 5 2 0 0 0.0001");
        buffer.newLine();
        buffer.write("217 15 2 0 0 0.0001");
        buffer.newLine();
        buffer.write("205 "+(board.getL()/2 - this.board.getDoorWidth()/2)+" 0 0 0 0.1");
        buffer.newLine();
        buffer.write("206 "+(board.getL()/2 + this.board.getDoorWidth()/2)+" 0 0 0 0.1");
        buffer.newLine();
        buffer.write("225 "+(board.getL()/2 - this.board.getDoorWidth()/2)+" 2 0 0 0.1");
        buffer.newLine();
        buffer.write("226 "+(board.getL()/2 + this.board.getDoorWidth()/2)+" 2 0 0 0.1");
        buffer.newLine();
    }
}
