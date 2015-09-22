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

    private List<Node> nodeList = new ArrayList<>();
    private int size = 0;

    public StringSetImpl() {
        nodeList.add(new Node());
    }

    public boolean add(String element) {
        if (contains(element))
            return false;

        int curNode = 0;
        nodeList.get(0).cntDown++;
        for (char curChar : element.toCharArray()) {
            int nextNodeIndex = curChar - 'a';
            int nextNode = nodeList.get(curNode).next[nextNodeIndex];
            if (nextNode == 0) {
                nextNode = nodeList.size();
                nodeList.get(curNode).next[nextNodeIndex] = nextNode;
                nodeList.add(new Node());
            }
            nodeList.get(nextNode).cntDown++;
            curNode = nextNode;
        }
        size++;
        nodeList.get(curNode).isTerminal = true;
        return true;
    }

    private Node findNode(String element) {
        int curNode = 0;
        for (char curChar : element.toCharArray()) {
            curNode = nodeList.get(curNode).next[curChar - 'a'];
            if (curNode == 0 || nodeList.get(curNode).cntDown == 0) {
                return null;
            }
        }
        return nodeList.get(curNode);
    }

    public boolean contains(String element) {
        Node el = findNode(element);
        return el != null && el.isTerminal;
    }

    public boolean remove(String element) {
        if (!contains(element))
            return false;

        int curNode = 0;
        nodeList.get(0).cntDown--;
        for (char curChar : element.toCharArray()) {
            curNode = nodeList.get(curNode).next[curChar - 'a'];
            nodeList.get(curNode).cntDown--;
        }
        size--;
        nodeList.get(curNode).isTerminal = false;
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
        byte[] bytes = new byte[4];
        int cnt = in.read(bytes, 0, 4);
        if (cnt != 4)
            throw new IOException();
        return ByteBuffer.wrap(bytes).getInt();
    }

    public void serialize(OutputStream out) throws SerializationException {
        try {
            writeInt(out, size);
            writeInt(out, nodeList.size());
            for (Node el : nodeList) {
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
            nodeList.clear();
            size = readInt(in);
            int cntNodes = readInt(in);
            for (int i = 0; i < cntNodes; i++) {
                nodeList.add(new Node());
                nodeList.get(i).cntDown = readInt(in);
                nodeList.get(i).isTerminal = readInt(in) == 1;
                for (int j = 0; j < 128; j++) {
                    nodeList.get(i).next[j] = readInt(in);
                }
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

}
