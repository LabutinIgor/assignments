package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class StringSetImpl implements StringSet, StreamSerializable {
    private class Node {
        int next[] = new int[128];
        int cnt_down = 0;
        boolean is_terminal = false;
    }
    private ArrayList<Node> a = new ArrayList<>();
    private int size = 0;

    StringSetImpl() {
        a.add(new Node());
    }

    public boolean add(String element) {
        if (contains(element))
            return false;

        int v = 0;
        a.get(0).cnt_down++;
        for (int i = 0; i < element.length(); i++) {
            int u = a.get(v).next[element.charAt(i) - 'A'];
            if (u == 0) {
                u = a.size();
                a.get(v).next[element.charAt(i) - 'A'] = u;
                a.add(new Node());
            }
            a.get(u).cnt_down++;
            v = u;
        }
        size++;
        a.get(v).is_terminal = true;
        return true;
    }

    public boolean contains(String element) {
        int v = 0;
        for (int i = 0; i < element.length(); i++) {
            int c = element.charAt(i) - 'A';
            v = a.get(v).next[c];
            if (v == 0 || a.get(v).cnt_down == 0) {
                return false;
            }
        }
        return a.get(v).is_terminal;
    }

    public boolean remove(String element) {
        if (!contains(element))
            return false;

        int v = 0;
        a.get(0).cnt_down--;
        for (int i = 0; i < element.length(); i++) {
            v = a.get(v).next[element.charAt(i) - 'A'];
            a.get(v).cnt_down--;
        }
        size--;
        a.get(v).is_terminal = false;
        return true;
    }

    public int size() {
        return size;
    }

    public int howManyStartsWithPrefix(String prefix) {
        int v = 0;
        for (int i = 0; i < prefix.length(); i++) {
            int c = prefix.charAt(i) - 'A';
            v = a.get(v).next[c];
            if (v == 0 || a.get(v).cnt_down == 0) {
                return 0;
            }
        }
        return a.get(v).cnt_down;
    }

    public void serialize(OutputStream out) throws SerializationException {
        try {
            out.write(size);
            out.write(a.size());
            for (Node el : a) {
                out.write(el.cnt_down);
                out.write(el.is_terminal ? 1 : 0);
                for (int j = 0; j < 128; j++) {
                    out.write(el.next[j]);
                }
            }
            out.close();
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    public void deserialize(InputStream in) throws SerializationException {
        try {
            size = in.read();
            int n = in.read();
            for (int i = 0; i < n; i++) {
                a.add(new Node());
                a.get(i).cnt_down = in.read();
                a.get(i).is_terminal = in.read() == 1;
                for (int j = 0; j < 128; j++) {
                    a.get(i).next[j] = in.read();
                }
            }
            in.close();
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

}
