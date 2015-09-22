package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class StringSetImpl implements StringSet, StreamSerializable {
    private static class Node {
        public int next[] = new int[128];
        public int cntDown = 0;
        public boolean isTerminal = false;
    }

    private List<Node> a = new ArrayList<>();
    private int size = 0;

    public StringSetImpl() {
        a.add(new Node());
    }

    public boolean add(String element) {
        if (contains(element))
            return false;

        int v = 0;
        a.get(0).cntDown++;
        for (char c : element.toCharArray()) {
            int k = c - 'A';
            int u = a.get(v).next[k];
            if (u == 0) {
                u = a.size();
                a.get(v).next[k] = u;
                a.add(new Node());
            }
            a.get(u).cntDown++;
            v = u;
        }
        size++;
        a.get(v).isTerminal = true;
        return true;
    }

    private Node findNode(String element) {
        int v = 0;
        for (char c : element.toCharArray()) {
            int k = c - 'A';
            v = a.get(v).next[k];
            if (v == 0 || a.get(v).cntDown == 0) {
                return null;
            }
        }
        return a.get(v);
    }

    public boolean contains(String element) {
        Node el = findNode(element);
        return el != null && el.isTerminal;
    }

    public boolean remove(String element) {
        if (!contains(element))
            return false;

        int v = 0;
        a.get(0).cntDown--;
        for (char c : element.toCharArray()) {
            v = a.get(v).next[c - 'A'];
            a.get(v).cntDown--;
        }
        size--;
        a.get(v).isTerminal = false;
        return true;
    }

    public int size() {
        return size;
    }

    public int howManyStartsWithPrefix(String prefix) {
        Node el = findNode(prefix);
        return el == null ? 0 : el.cntDown;
    }

    private static void writeInt(OutputStream out, int n) throws IOException {
        out.write(ByteBuffer.allocate(4).putInt(n).array());
    }

    private static int readInt(InputStream in) throws IOException {
        byte[] b = new byte[4];
        int cnt = in.read(b, 0, 4);
        if (cnt != 4)
            throw new IOException();
        return ByteBuffer.wrap(b).getInt();
    }

    public void serialize(OutputStream out) throws SerializationException {
        try {
            writeInt(out, size);
            writeInt(out, a.size());
            for (Node el : a) {
                writeInt(out, el.cntDown);
                writeInt(out, el.isTerminal ? 1 : 0);
                for (int j = 0; j < 128; j++) {
                    writeInt(out, el.next[j]);
                }
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    public void deserialize(InputStream in) throws SerializationException {
        try {
            a.clear();
            size = readInt(in);
            int n = readInt(in);
            for (int i = 0; i < n; i++) {
                a.add(new Node());
                a.get(i).cntDown = readInt(in);
                a.get(i).isTerminal = readInt(in) == 1;
                for (int j = 0; j < 128; j++) {
                    a.get(i).next[j] = readInt(in);
                }
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

}
