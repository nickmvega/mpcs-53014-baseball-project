package org.example;

import java.io.*;
import java.util.*;

public abstract class BaseProcessor<T> {

    protected Map<String,Integer> indexMap;

    protected int toInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    protected boolean toBool(String s) {
        return "1".equals(s) || "true".equalsIgnoreCase(s);
    }

    protected String get(String[] row, String col) {
        Integer index = indexMap.get(col);
        return (index == null || index >= row.length) ? "" : row[index];
    }

    protected boolean initializeIndexMap(String header) {
        if (header == null) return false;
        indexMap = new HashMap<>();
        String[] cols = header.split(",", -1);

        for (int i = 0; i < cols.length; i++) {
            indexMap.put(cols[i].trim(), i);
        }
        return true;
    }

    public void processFile(InputStream is, String sourceName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String header = br.readLine();
        if (!initializeIndexMap(header)) return;

        String line;
        while ((line = br.readLine()) != null) {
            T obj = parse(line);
            processRecord(obj, sourceName);
        }
    }

    protected abstract T parse(String line);
    protected abstract void processRecord(T obj, String sourceName) throws IOException;
}
