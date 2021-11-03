import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OutputData {
    private final Map<Double, Integer> escapeData;

    public OutputData() {
        this.escapeData = new HashMap<>();
    }

    public Map<Double, Integer> getEscapeData() {
        return escapeData;
    }

    public void writeTimesToFile() throws IOException {
        FileWriter pos = new FileWriter("times_5.csv", false);
        BufferedWriter buffer = new BufferedWriter(pos);
        buffer.write("time;escaped_particles");
        buffer.newLine();
        for(Map.Entry<Double, Integer> time : escapeData.entrySet()) {
            buffer.write(time.getKey().toString() + ";" + time.getValue().toString());
            buffer.newLine();
        }
        buffer.flush();
        buffer.close();
        pos.close();
    }
}
