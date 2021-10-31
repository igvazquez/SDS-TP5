import lombok.Data;

@Data
public class Particle {

    int id;
    double x;
    double y;
    double vx;
    double vy;
    double vd;
    double mass;
    double radius;

    public Particle(int id, double x, double y, double vx, double vy,
                    double mass, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.vd = Math.hypot(vx, vy);
        this.mass = mass;
        this.radius = radius;
    }

    private double distanceFromAxis(double ax1, double ax2, double L, boolean periodicOutline){
        double distance = Math.abs(ax1 - ax2);

        if (periodicOutline){
            if(distance > L/2){
                distance = L - distance;
            }
        }
        return distance;
    }

    public double calculateDistance(Particle p, double L, boolean periodicOutline) {
        double x = distanceFromAxis(this.x, p.getX(), L, periodicOutline);
        double y = distanceFromAxis(this.y, p.getY(), L, periodicOutline);

        return Math.sqrt(x*x + y*y) - this.radius - p.getRadius();
    }

    public double distanceTo(final Particle p) {
        return Math.hypot(x - p.getX(), y - p.getY()) - radius - p.radius;
    }

    public boolean collides(final Particle particle, final double L, final boolean periodicOutline) {
        return calculateDistance(particle, L, periodicOutline) <= 0;
    }

    public boolean collides(final Particle particle) {
        return distanceTo(particle) <= 0;
    }

    public double getVMod(){
        return Math.hypot(vx, vy);
    }
}