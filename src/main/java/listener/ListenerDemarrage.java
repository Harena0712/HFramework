package listener;

import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import service.Utilitaire;
import definition.*;

public class ListenerDemarrage implements ServletContextListener {

    private Utilitaire utilitaire = new Utilitaire();
    private List<String> classNameController;

    @Override
    public void contextInitialized(ServletContextEvent ServletContextEvent) {
        ServletContext context = ServletContextEvent.getServletContext();
        String packageName = context.getInitParameter("packageName");
        try {
            classNameController = utilitaire.getAllClassesWithAnnotationInPackage(packageName, Controller.class);

            context.setAttribute("classNameController", classNameController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent ServletContextEvent) {

    }

    public List<String> getClassNameController() {
        return classNameController;
    }
}