import java.util.*;

    // Represents a node in the parse tree, Each node has a label and are optionally child nodes
public class ParseTreeNode {
    private final String label;
    private final List<ParseTreeNode> children;

    public ParseTreeNode(String label) {
        this.label = label;
        this.children = new ArrayList<>();
    }


    // Adds a child node to the node
    public void addChild(ParseTreeNode child) {
        children.add(child);
    }

    // Recursively prints the parse tree
    public void printTree(String indent) {
        System.out.println(indent + label);
        for (ParseTreeNode child : children) {
            child.printTree(indent + "    ");
        }
    }
}
