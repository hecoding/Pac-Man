package jeco.core.algorithm.neat;

import java.util.ArrayList;

public class CPPN {
    ArrayList<Node> nodes;
    ArrayList<Link> links;

    class Node {
        NodeType type;
    }

    class Link {
        Node in;
        Node out;
        double weight;
        boolean enabled;
        int innov;
    }

    public enum NodeType {NEURON, INPUT}
}
