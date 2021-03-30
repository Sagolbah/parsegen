package parsegen.model;

import java.util.List;

public class SyntRule implements Rule {
    private final List<String> dependencies;
    private List<String> actions;
    private final String name;

    public List<String> getActions() {
        return actions;
    }

    public SyntRule(String name, List<String> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public SyntRule(String name, List<String> dependencies, List<String> actions){
        this.name = name;
        this.dependencies = dependencies;
        this.actions = actions;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public String getName() {
        return name;
    }

}
