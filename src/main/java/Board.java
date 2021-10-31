import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Board {

    private static final double DOOR_WIDTH = 1.2;
    private static final double TARGET_DISTANCE_FROM_DOOR = -10;
    private static final double TARGET_LENGTH = 3;
    private static final double TARGET_TRIM = 0.2;

    private final double L;
    private final double minR;
    private final double maxR;
    private final double maxV;
    private final double Ve;
    private final double tau;
    private final double beta;
    private final int M;
    private final Map<Integer, List<Particle>> cells;
    private List<Particle> particles;

    public Board(double l, double minR, double maxR, double maxV, double tau,
                 double beta, double Ve, int m, List<Particle> particles) {
        L = l;
        this.minR = minR;
        this.maxR = maxR;
        this.maxV = maxV;
        this.Ve = Ve;
        this.tau = tau;
        this.beta = beta;
        M = m;
        this.cells = new HashMap<>();
        sortBoard(particles);
    }

    public void sortBoard(List<Particle> newParticles) {
        for (int i = 0; i < M * M; i++) {
            cells.put(i, new ArrayList<>());
        }
        this.particles = newParticles;
        divideParticles();
    }

    public void divideParticles() {
        for (Particle p : particles) {
            if (p.getX() < 0 || p.getX() > L || p.getY() < 0 || p.getY() > L) {
                throw new IllegalArgumentException("Partícula fuera de los límites.");
            }
            cells.get(calculateCellIndexOnBoard(p.getX(), p.getY())).add(p);
        }
    }

    public Integer calculateCellIndexOnBoard(double x, double y) {
        int i = (int) (x / (L / M));
        int j = (int) (y / (L / M));
        return i + M * j;
    }

    public static Board getRandomBoard(int n, double l, int m, double minR,
                                       double maxR, double maxV, double tau,
                                       double beta, double ve,double maxMass) {

        List<Particle> particles = new ArrayList<>();

        double x, y, mass, radius;
        double[] vel;

        int i;
        for (i = 0; i < n; i++) {
            x = Math.random() * l;
            y = Math.random() * l;
            vel = calculateVelocityToTarget(maxV, l, x, y);
            mass = Math.random() * maxMass;
            radius = Math.random() * maxR;

            particles.add(new Particle(i, x, y, vel[0], vel[1], mass, radius));
        }

        return new Board(l, minR, maxR, maxV, tau, beta, ve, m, particles);
    }

    private static double[] calculateVelocityToTarget(final double maxV, final double l, final double x, final double y) {
        double v = Math.random() * maxV;
        double vx = v * (l / 2 - x) / Math.abs(l / 2 - x);
        double vy = v * ((l + TARGET_DISTANCE_FROM_DOOR) - y) / Math.abs((l + TARGET_DISTANCE_FROM_DOOR) - y);

        double[] ret = new double[2];
        ret[0] = vx;
        ret[1] = vy;

        return ret;
    }

    public static int optM(final double l, final double rc) {
        return (int) Math.floor(l / rc);
    }

    public double getL() {
        return L;
    }

    public int getM() {
        return M;
    }

    public int getN() {
        return particles.size();
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public List<Particle> getCell(int idx) {
        return cells.get(idx);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(M * M);
        b.append("Board:\n");
        for (int i = 0; i < M * M; i++) {
            b.append(cells.get(i).size()).append(" ");
            if (i % M == M - 1) {
                b.append("\n");
            }
        }
        return b.toString();
    }

    public boolean isParticleInside(final Particle p) {
        return !(p.getX() < 0) && !(p.getX() > L) && !(p.getY() < 0) && !(p.getY() > L);
    }

    public Particle removeParticle(final Particle p) {
        particles.remove(p);
        cells.get(calculateCellIndexOnBoard(p.getX(), p.getY())).remove(p);
        return p;
    }

    public void advanceParticle(final Particle p, final Set<Particle> neighbours) {
        double escapeX = 0;
        double escapeY = 0;

        if (p.getX() - p.getRadius() <= 0) {
            escapeX += 1;
        } else if (p.getX() + p.getRadius() >= L) {
            escapeX -= 1;
        }
        if (p.getY() + p.getRadius() >= L) {
            escapeY -= 1;
        } else if ((p.getY() <= 0 && p.getY() + p.getRadius() >= 0) || (p.getY() > 0 && p.getY() - p.getRadius() <= 0)) {
            if (p.getX() <= L/2 - DOOR_WIDTH/2 || p.getX() >= L/2 + DOOR_WIDTH/2) {
                escapeY += 1;
            }
            if (p.getX() - p.getRadius() <= L/2 - DOOR_WIDTH/2) {
                final double diffX = p.getX() - L/2 - DOOR_WIDTH/2;
                final double distance = Math.hypot(diffX, p.getY());

                escapeX += diffX / distance;
                escapeY += p.getY() / distance;
            } else if (p.getX() + p.getRadius() >= L/2 + DOOR_WIDTH/2) {
                final double diffX = p.getY() -  L/2 + DOOR_WIDTH/2;
                final double distance = Math.hypot(diffX, p.getY());

                escapeX += diffX / distance;
                escapeY += p.getY() / distance;
            }
        }

        for(final Particle other : neighbours) {
            final double diffX = p.getX() - other.getX();
            final double diffY = p.getY() - other.getY();
            final double distance = Math.hypot(diffX, diffY);

            escapeX += diffX / distance;
            escapeY += diffY / distance;
        }

        final double newVx;
        final double newVy;
        final double newR;
        if(escapeX == 0 && escapeY == 0) {
            double dt = minR / 2 * Math.max(maxV, Ve);
            newR = Math.min(maxR, p.getRadius() + maxR/(tau/dt));

            final double newVMod = maxV * Math.pow((newR - minR) / (maxR - minR), beta);
            boolean escaped = p.getY() <= 0;
            final double targetDirX = targetX(p.getX(), escaped);
            final double targetDirY = targetY(p.getY(), escaped);
            final double targetDirMod = Math.hypot(targetDirX, targetDirY);

            newVx = newVMod * (targetDirX / targetDirMod);
            newVy = newVMod * (targetDirY / targetDirMod);
        } else {
            final double escapeMod = Math.hypot(escapeX, escapeY);
            newVx   = Ve * (escapeX / escapeMod);
            newVy   = Ve * (escapeY / escapeMod);
            newR    = minR;
        }

    }

    private double targetX(final double x, final boolean escaped) {
        final double leftLimit  = escaped ?
                (L/2 - TARGET_LENGTH/2) + TARGET_TRIM * TARGET_LENGTH
                : L/2 - DOOR_WIDTH/2 + TARGET_TRIM*DOOR_WIDTH;
        final double rightLimit = escaped ?
                (L/2 + TARGET_LENGTH/2) - TARGET_TRIM * TARGET_LENGTH
                : L/2 + DOOR_WIDTH/2 - TARGET_TRIM * DOOR_WIDTH;

        return x < leftLimit || x > rightLimit
                ? leftLimit + Math.random() * (rightLimit - leftLimit) - x : 0;
    }

    private double targetY(final double y, final boolean escaped) {
        return escaped ? TARGET_DISTANCE_FROM_DOOR - y : -y;
    }
}