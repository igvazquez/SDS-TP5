public class Main {

    public static void main(String[] args) {
        double minR = 0.5;
        double maxR = 1;
        double maxV = 1;
        double maxMass = 1;
        double beta = 1;
        double tau = 1;

        Board board = Board.getRandomBoard(200, 20.0, Board.optM(10, maxR), minR, maxR, maxV, maxMass);

        CrowdSimulation cs = new CrowdSimulation(board, maxR, beta, tau);
    }
}
