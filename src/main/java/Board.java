import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Board {

    public static final double TARGET_DISTANCE_FROM_DOOR = -10;
    public static final double TARGET_LENGTH = 3;
    public static final double TARGET_TRIM = 0.2;
    public static final double X_PADDING = 5.0;
    public static final double Y_PADDING = 2.0;

    private final double L;
    private final double minR;
    private final double maxR;
    private final double maxV;
    private final double Ve;
    private final double tau;
    private final double beta;
    private final double dt;
    private final double doorWidth;
    private final int M;
    private final Map<Integer, List<Particle>> cells;
    private List<Particle> particles;

    public Board(double l, double d, double minR, double maxR, double maxV, double tau,
                 double beta, double Ve, int m, List<Particle> particles) {
        L = l;
        this.minR = minR;
        this.maxR = maxR;
        this.maxV = maxV;
        this.Ve = Ve;
        this.doorWidth = d;
        this.tau = tau;
        this.beta = beta;
        this.dt = minR / (2 * Math.max(maxV, Ve));
        M = m;
        this.cells = new HashMap<>();
        sortBoard(particles);
    }

    public double getDt() {
        return dt;
    }

    public double getMaxR() {
        return maxR;
    }

    public double getDoorWidth() {
        return doorWidth;
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
                throw new IllegalArgumentException("Partícula fuera de los límites." + p);
            }
            cells.get(calculateCellIndexOnBoard(p.getX(), p.getY())).add(p);
        }
    }

    public Integer calculateCellIndexOnBoard(double x, double y) {
        int i = (int) (x / (L / M));
        int j = (int) (y / (L / M));
        return i + M * j;
    }

    private static boolean overlap(double x, double y, double r, List<Particle> particles) {
        if (particles.size() == 0) {
            return false;
        }
        for (Particle p : particles) {
            if (Math.hypot(x - p.getX(), y - p.getY()) - r - p.getRadius() <= 0) {
                return true;
            }
        }
        return false;
    }

    public static Board getRandomBoard(int n, double d, double l, int m, double minR,
                                       double maxR, double maxV, double tau,
                                       double beta, double ve,double maxMass) {

        List<Particle> particles = new ArrayList<>();

        double x, y, mass, radius;
        double[] vel;

        int i;
        for (i = 0; i < n; i++) {
            do {
                x = X_PADDING + Math.random() * (l-2*X_PADDING);
                y = Y_PADDING + Math.random() * (l-2*Y_PADDING);
                radius = ThreadLocalRandom.current().nextDouble(minR, maxR);
            } while (overlap(x, y, radius, particles));

            vel = calculateVelocityToTarget(maxV, l, d, x, y);
            mass = Math.random() * maxMass;

            particles.add(new Particle(i, x, y, vel[0], vel[1], mass, radius));
        }

        return new Board(l, d, minR, maxR, maxV, tau, beta, ve, m, particles);
    }

    public static double[] calculateVelocityToTarget(final double maxV, final double l, final double doorWidth, final double x, final double y) {
        double v = Math.random() * maxV;
        final double leftLimit  = l/2 - doorWidth/2 + TARGET_TRIM*doorWidth;
        final double rightLimit = l/2 + doorWidth/2 - TARGET_TRIM * doorWidth;
        double dx = x < leftLimit || x > rightLimit
                ? leftLimit + Math.random() * (rightLimit - leftLimit) - x : 0;
        final double dy = -y;
        final double distance = Math.hypot(dx, dy);

        double vx = v * (dx / distance);
        double vy = v * (dy / distance);

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

    public Particle advanceParticle(final Particle p, final Set<Particle> neighbours) {
        double dx = 0;
        double dy = 0;

        if (p.getX() - p.getRadius() <= 0 + X_PADDING) {
            dx += 1;
        } else if (p.getX() + p.getRadius() >= L - X_PADDING) {
            dx -= 1;
        }
        if (p.getY() + p.getRadius() >= L - Y_PADDING) {
            dy -= 1;
        } else if ((p.getY() <= Y_PADDING && p.getY() + p.getRadius() >= Y_PADDING) || (p.getY() > Y_PADDING && p.getY() - p.getRadius() <= Y_PADDING)) {
            if (p.getX() <= L/2 - doorWidth/2 || p.getX() >= L/2 + doorWidth/2) {
                dy += 1;
            } else if (p.getX() - p.getRadius() <= L/2 - doorWidth/2) {
                final double diffX = p.getX() - L/2 + doorWidth/2;
                final double distance = Math.hypot(diffX, p.getY());

                dx += diffX / distance;
                dy += p.getY() / distance;
            } else if (p.getX() + p.getRadius() >= L/2 + doorWidth/2) {
                final double diffX = p.getX() -  L/2 - doorWidth/2;
                final double distance = Math.hypot(diffX, p.getY());

                dx += diffX / distance;
                dy += p.getY() / distance;
            }
        }

        for(final Particle other : neighbours) {
            if(p.getId() != other.getId() && p.collides(other)){
                double diffX = p.getX() - other.getX();
                double diffY = p.getY() - other.getY();
                double distance = Math.hypot(diffX, diffY);

                dx += diffX / distance;
                dy += diffY / distance;
            }
        }

        final double newVx;
        final double newVy;
        final double newR;
        if(dx == 0 && dy == 0) {
            newR = Math.min(maxR, p.getRadius() + maxR/(tau/dt));

            final double newVMod = maxV * Math.pow((newR - minR) / (maxR - minR), beta);
            boolean escaped = p.getY() <= Y_PADDING;
            final double targetDirX = nextTargetX(p.getX(), escaped);
            final double targetDirY = nextTargetY(p.getY(), escaped);
            final double targetDirMod = Math.hypot(targetDirX, targetDirY);

            newVx = newVMod * (targetDirX / targetDirMod);
            newVy = newVMod * (targetDirY / targetDirMod);
        } else {
            final double escapeMod = Math.hypot(dx, dy);
            newVx   = Ve * (dx / escapeMod);
            newVy   = Ve * (dy / escapeMod);
            newR    = minR;
        }

        return new Particle(p.getId(), p.getX() + newVx*dt, p.getY() + newVy*dt, newVx, newVy, p.getMass(), newR);
    }

    private double nextTargetX(final double x, final boolean escaped) {
        final double leftLimit  = escaped ?
                (L/2 - TARGET_LENGTH/2) + TARGET_TRIM * TARGET_LENGTH
                : L/2 - doorWidth/2 + TARGET_TRIM*doorWidth;
        final double rightLimit = escaped ?
                (L/2 + TARGET_LENGTH/2) - TARGET_TRIM * TARGET_LENGTH
                : L/2 + doorWidth/2 - TARGET_TRIM * doorWidth;

        return x < leftLimit || x > rightLimit
                ? leftLimit + Math.random() * (rightLimit - leftLimit) - x : 0;
    }

    private double nextTargetY(final double y, final boolean escaped) {
        return escaped ? TARGET_DISTANCE_FROM_DOOR - y : -y;
    }

    public void updateParticles(final List<Particle> currentState) {
        this.particles = currentState;
        sortBoard(particles);
    }
}