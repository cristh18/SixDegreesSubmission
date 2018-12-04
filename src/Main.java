import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

class Main {

    private static int peopleNumber;
    private static int relationsNumber;
    private static ArrayList<PeopleRelation> relations;
    private static HashMap<String, Node> nodes;
    private static HashSet<String> nodeValues;
    private static HashSet<Integer> degrees;
    private static ArrayList<String> unCheckedValues;
    private static int degree = 0;

    private static void calculateDegree() {
        try {
            manualInitialize();
            evaluateGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void evaluateGraph() {
        if (isGraphConnected()) {
            System.out.println("Network: " + getMaximumDegree());
        } else {
            System.out.println("Network: DISCONNECTED");
        }
        System.out.println("");
    }

    private static void manualInitialize() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        System.out.println("Enter people number: ");

        peopleNumber = Integer.parseInt(br.readLine());
        System.out.println("Persons: " + peopleNumber);

        System.out.println("Enter relations number: ");

        relationsNumber = Integer.parseInt(br.readLine());
        System.out.println("Relations number: " + relationsNumber);

        getManualRelations(br);
    }

    private static void getManualRelations(BufferedReader br) throws IOException {
        if (relationsNumber > 0) {
            int i = 0;
            nodeValues = new HashSet<>(peopleNumber);
            relations = new ArrayList<>(relationsNumber);
            nodes = new HashMap<>(peopleNumber);
            degrees = new HashSet<>();

            while (i < relationsNumber) {
                System.out.println("Enter the ".concat(String.valueOf(i)).concat(" relation:"));
                System.out.println("Origin: ");
                String origin = br.readLine();
                System.out.println("Destiny: ");
                String destiny = br.readLine();

                nodeValues.add(origin);
                nodeValues.add(destiny);

                PeopleRelation peopleRelation = new PeopleRelation(origin, destiny);
                relations.add(peopleRelation);
                i++;
            }
            validateRelations();
        }
    }

    private static void validateRelations() {
        for (PeopleRelation peopleRelation : relations) {
            buildNode(peopleRelation, true);
            buildNode(peopleRelation, false);
        }
    }

    private static void buildNode(PeopleRelation peopleRelation, boolean isOrigin) {
        String nodeValue;
        if (isOrigin) {
            nodeValue = peopleRelation.origin;
        } else {
            nodeValue = peopleRelation.destiny;
            peopleRelation = new PeopleRelation(peopleRelation.destiny, peopleRelation.origin);
        }

        if (nodes.containsKey(nodeValue)) {
            Node node = nodes.get(nodeValue);
            node.addRelation(peopleRelation);
            node.addChild(peopleRelation.destiny);
        } else {
            Node node = new Node(nodeValue);
            node.addRelation(peopleRelation);
            node.addChild(peopleRelation.destiny);
            nodes.put(nodeValue, node);
        }
    }

    private static boolean isGraphConnected() {
        int nodesConnected = 0;
        ArrayList<String> nodes = new ArrayList<>(nodeValues);
        for (int i = 0; i < nodes.size(); i++) {
            boolean nodeConnected;
            int connections = 0;

            for (int j = 0; j < nodes.size(); j++) {
                unCheckedValues = new ArrayList<>(nodeValues);
                nodeConnected = isNodeConnectedToDestiny(nodes.get(i), nodes.get(j));
                if (nodeConnected) {
                    connections++;
                }
            }

            if (connections == nodeValues.size()) {
                nodesConnected++;
            }
        }

        return (nodesConnected == nodeValues.size());
    }

    private static boolean isNodeConnectedToDestiny(String referenceNode, String destiny) {
        boolean isConnected;
        degree = 0;
        Node reference = nodes.get(referenceNode);
        if (reference.children.contains(destiny)) {
            degree++;
            degrees.add(degree);
            isConnected = true;
        } else {
            degree = 1;
            updateUncheckedValues(reference.value);
            isConnected = iterateNetwork(new ArrayList<>(reference.children), destiny);
        }

        return isConnected;
    }

    private static void updateUncheckedValues(String value) {
        if (!unCheckedValues.isEmpty()) {
            unCheckedValues.remove(value);
        }
    }

    private static int getMaximumDegree() {
        int maxValue = 0;
        for (Integer degree : degrees) {
            if (degree > maxValue) {
                maxValue = degree;
            }
        }
        return maxValue;
    }

    private static boolean iterateNetwork(List<String> children, String destiny) {
        boolean found = false;
        degree++;
        HashSet<String> minorChildren = new HashSet<>();
        for (String child : children) {
            Node newReference = nodes.get(child);
            if (newReference.children.contains(destiny)) {
                degrees.add(degree);
                found = true;
                break;
            } else {
                updateUncheckedValues(child);
                for (String n : newReference.children) {
                    if (unCheckedValues.contains(n)) {
                        minorChildren.add(n);
                    }
                }
            }
        }

        if (!found && !minorChildren.isEmpty()) {
            return iterateNetwork(new ArrayList<>(minorChildren), destiny);
        }

        return found;
    }


    static class Node {
        String value;
        HashSet<PeopleRelation> relations;
        HashSet<String> children;

        Node(String value) {
            this.value = value;
            relations = new HashSet<>();
            children = new HashSet<>();
        }

        void addRelation(PeopleRelation peopleRelation) {
            if (relations != null && peopleRelation != null) {
                relations.add(peopleRelation);
            }
        }

        void addChild(String child) {
            if (children != null && child != null && !child.equalsIgnoreCase("")) {
                children.add(child);
            }
        }
    }

    static class PeopleRelation {
        String origin;
        String destiny;

        PeopleRelation(String origin, String destiny) {
            this.origin = origin;
            this.destiny = destiny;
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.calculateDegree();
    }
}