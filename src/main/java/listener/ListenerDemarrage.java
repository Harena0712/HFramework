package listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import service.UrlMethode;
import service.UtilMethode;
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

        Map<UtilMethode, UrlMethode> urlMappings = new HashMap<>();
        try {
            utilitaire.getAllUrlMappingsWithUtilMethode(packageName, urlMappings);
            context.setAttribute("urlMappings", urlMappings);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Erreur d'initialisation : urlMappings non initialisé. Cause : " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent ServletContextEvent) {

    }
}