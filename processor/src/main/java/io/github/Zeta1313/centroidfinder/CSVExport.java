package io.github.Zeta1313.centroidfinder;

import java.io.FileWriter;
import java.io.PrintWriter;

public class CSVExport implements AutoCloseable {

    private PrintWriter writer;

    public CSVExport(String path) throws Exception {
        // autoFlush=true so each println is flushed to disk immediately,
        // letting the CSV be read live while the job is still running.
        writer = new PrintWriter(new FileWriter(path), true);

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
