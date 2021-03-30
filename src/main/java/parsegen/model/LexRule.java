package parsegen.model;

public class LexRule implements Rule {
    private String name;
    private String rule;
    private boolean isRegex;

    public LexRule(String name, String rule, boolean isRegex) {
        this.name = name;
        this.rule = rule;
        this.isRegex = isRegex;
    }

    public String getName() {
        return name;
    }

    public String getRule() {
        return rule;
    }

    public boolean isRegex() {
        return isRegex;
    }

}
