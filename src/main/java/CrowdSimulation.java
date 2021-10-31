import java.util.*;

public class CrowdSimulation {

    List<Particle> outPersons = new LinkedList<>();
    private final Board board;
    private final double rc;
    private final double beta;
    private final double tau;

    public CrowdSimulation(final Board board, final double rc, final double beta, final double tau) {
        this.board = board;
        this.rc = rc;
        this.beta = beta;
        this.tau = tau;
    }

    public void simulate(final Board initialBoard) {
        List<Particle> currentState = initialBoard.getParticles();
        int lastLockedCount = currentState.size();
        int lastEscapedCount = 0;
        CellIndexMethod cim;

        final Set<Particle> alreadyEscaped = new HashSet<>(currentState.size());

        while (!currentState.isEmpty()) {
            final List<Particle> in = new ArrayList<>(lastLockedCount);
            final List<Particle> escaped = new ArrayList<>(lastEscapedCount);

            cim = new CellIndexMethod(board, rc, false);
            cim.calculateNeighbours();

            currentState = doStep(currentState, cim);
        }
    }

    private List<Particle> doStep(final List<Particle> currentState, final CellIndexMethod cim) {
        Set<Particle> alreadyInteract = new HashSet<>();

        for (Particle p : board.getParticles()) {
            Map<Integer, Set<Particle>> neighbours = cim.getNeighboursMap();
            board.advanceParticle(p, neighbours.get(p.getId()));
        }
    }
}
