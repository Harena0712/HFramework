package service;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private Map<String, Object> model;
    private String viewName;

    public ModelAndView() {
        this.model = new HashMap<>();
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
        this.model = new HashMap<>();
    }

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = (model != null) ? model : new HashMap<>();
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public String getViewName() {
        return viewName;
    }

    public void addObject(String key, Object value) {
        model.put(key, value);
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}