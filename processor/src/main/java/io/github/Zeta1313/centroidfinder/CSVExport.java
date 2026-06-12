package io.github.Zeta1313.centroidfinder;

import java.io.PrintWriter;
import java.util.List;

public class CSVExport implements AutoCloseable {

    private PrintWriter writer;

    public CSVExport(String path) throws Exception {
        writer = new PrintWriter(path);

        writer.println("frame,size,x,y");
        // writer.flush();
    }

    public void writeFrame(int timestamp, Group group) {
        writer.println(timestamp + "," + group.toCsvRow());
        // writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
