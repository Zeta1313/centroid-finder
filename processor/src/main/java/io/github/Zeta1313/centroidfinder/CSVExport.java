package io.github.Zeta1313.centroidfinder;

import java.io.PrintWriter;
import java.util.List;

public class CSVExport implements AutoCloseable {

    private PrintWriter writer;

    public CSVExport(String path) throws Exception {
        writer = new PrintWriter(path);

        writer.println("frame,size,x,y");
    }

    public void writeFrame(int timestamp, Group group) {
        writer.println(timestamp + "," + group.toCsvRow());
    }

    @Override
    public void close() {
        writer.close();
    }
}
