import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OutputData {
    private final Map<Double, Integer> escapeData;

    public OutputData() {
        this.escapeData = new TreeMap<>();
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

    public void nombrePendiente(List<String> files) {
        Map<Integer, Double> result = new HashMap<>();
        try {
            for(String file : files) {
                Scanner st = new Scanner(new File(file));
                st.useDelimiter(";");
                while(st.hasNextLine()) {
                    double time = st.nextDouble();
                    int n = st.nextInt();
                    if(result.containsKey(n)) {
                        time = (time + result.get(n)) / 2;
                    }
                    result.put(n, time);
                }
            }
        } catch (Exception e) {

        }
    }
}
