package controller;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Map;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import service.ModelAndView;
import service.Utilitaire;
import definition.*;

@Controller
public class FrontServletController extends HttpServlet {

    // private List<String> classNameController;
    private Utilitaire utilitaire = new Utilitaire();

    // public void init() throws ServletException {
    // String packageName = this.getInitParameter("packageName");

    // try {
    // classNameController =
    // utilitaire.getAllClassesWithAnnotationInPackage(packageName,
    // Controller.class);
    // } catch (Exception e) {
    // throw new ServletException(e);
    // }
    // }

    public void proccessRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
        res.setContentType("text/plain;charset=UTF-8");

        String path = req.getRequestURI().toString();
        PrintWriter out = res.getWriter();

        String contextPath = req.getContextPath();
        String chemin = path.substring(contextPath.length() + 1);

        String packageName = this.getInitParameter("packageName");

        Map<service.UtilMethode, service.UrlMethode> urlMappings = (Map<service.UtilMethode, service.UrlMethode>) getServletContext()
                .getAttribute("urlMappings");
        if (urlMappings == null) {
            throw new Exception("Erreur : urlMappings est null...");
        }
        String prefix = (String) getServletContext().getAttribute("prefix");
        String suffix = (String) getServletContext().getAttribute("suffix");

        try {
            Object result = utilitaire.lireMethodeAndClass(chemin, req.getMethod(), packageName, urlMappings);

            ModelAndView mv;
            if (result instanceof ModelAndView) {
                mv = (ModelAndView) result;
            } else if (result instanceof String) {
                mv = new ModelAndView((String) result);
            } else {
                throw new ServletException("Type de retour non supporté : " + result);
            }

            utilitaire.trouverChemin(mv, req, res, prefix, suffix);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Resultat de l'url : " + e.getMessage());
        }

        // for (String className : classNameController) {
        // out.println("Class : " + className);
        // }

        // List<String> classNameControllerFromListener = (List<String>)
        // getServletContext()
        // .getAttribute("classNameController");

        // if (classNameControllerFromListener == null) {
        // out.println("Class from listener is null");
        // } else {
        // for (String className : classNameControllerFromListener) {
        // out.println("Class from listener : " + className);
        // }
        // }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            proccessRequest(req, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            proccessRequest(req, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
