import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class OutputData {
    private List<Double> escapeTimes;

    public OutputData() {
        this.escapeTimes = new LinkedList<>();
    }

    public List<Double> getEscapeTimes() {
        return escapeTimes;
    }

    public void writeTimesToFile() throws IOException {
        FileWriter pos = new FileWriter("times.csv", false);
        BufferedWriter buffer = new BufferedWriter(pos);
        for(double time : escapeTimes) {
            buffer.write(String.valueOf(time));
            buffer.newLine();
        }
        buffer.flush();
        buffer.close();
        pos.close();
    }
}
