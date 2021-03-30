package parsegen.model;

import parsegen.mock.Node;

import java.util.List;

public class Token extends Node {
    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Token(int type, String text) {
        this.type = type;
        this.name = text;
    }

    @Override
    public List<Node> getChildren() {
        return List.of();
    }

}
