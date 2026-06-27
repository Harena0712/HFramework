package service;

public class UrlMethode {
    private String className;
    private String methodeName;

    public UrlMethode(String className, String methodeName) {
        this.className = className;
        this.methodeName = methodeName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodeName() {
        return methodeName;
    }
}
