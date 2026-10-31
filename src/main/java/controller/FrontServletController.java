package controller;

import java.io.*;
import java.net.URI;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import service.ModelAndView;
import service.Utilitaire;
import service.AuthentificationException;
import definition.*;

import java.nio.charset.StandardCharsets;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 20 * 1024 * 1024
)

@Controller
public class FrontServletController extends HttpServlet {

    // private List<String> classNameController;
    private Utilitaire utilitaire = new Utilitaire();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    // dans FrontServletController

    @Override
    public void init() throws ServletException {
        getServletContext().setAttribute("controllerInstances", new ConcurrentHashMap<String, Object>());
    }

    public void proccessRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
        res.setContentType("text/plain;charset=UTF-8");

        String path = req.getRequestURI().toString();

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
        
        req.setCharacterEncoding("UTF-8");

        Map<String, Object> parametres = new HashMap<>();

        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            parametres.put(e.getKey(), e.getValue()[0]);
        }

        String ct = req.getContentType();
        if (ct != null && ct.toLowerCase().startsWith("multipart/")) {
            for (Part part : req.getParts()) {
                if (part.getSubmittedFileName() != null) {
                    parametres.put(part.getName(), part);
                } else if (!parametres.containsKey(part.getName())) {
                    try (InputStream in = part.getInputStream()) {
                        parametres.put(part.getName(),
                                new String(in.readAllBytes(), StandardCharsets.UTF_8));
                    }
                }
            }
        }

        ServletContext ctx = getServletContext();
        ConcurrentHashMap<String, Object> instances =
                (ConcurrentHashMap<String, Object>) ctx.getAttribute("controllerInstances");
        if (instances == null) {
            synchronized (ctx) {
                instances = (ConcurrentHashMap<String, Object>) ctx.getAttribute("controllerInstances");
                if (instances == null) {
                    instances = new ConcurrentHashMap<>();
                    ctx.setAttribute("controllerInstances", instances);
                }
            }
        }

        String authKey = this.getInitParameter("authSessionKey");
        String roleKey = this.getInitParameter("authRoleKey");

        parametres.put("__httpSession", req.getSession());
        parametres.put("__instances", instances);
        if (authKey != null) parametres.put("__authKey", authKey);
        if (roleKey != null) parametres.put("__roleKey", roleKey);

        System.out.println("Content-Type = " + ct);
        System.out.println("parametres = " + parametres);
        try {
            Utilitaire.InvocationResult invocationResult = utilitaire.lireMethodeAndClassResult(
                    chemin, req.getMethod(), packageName, urlMappings, parametres);
            Object result = invocationResult.value();

            if (invocationResult.json()) {
                res.setContentType("application/json;charset=UTF-8");
                PrintWriter out = res.getWriter();
                objectMapper.writeValue(out, result);
                return;
            }

            ModelAndView mv;
            if (result instanceof ModelAndView) {
                mv = (ModelAndView) result;
            } else if (result instanceof String) {
                mv = new ModelAndView((String) result);
            } else {
                throw new ServletException("Type de retour non supporté : " + result);
            }

            utilitaire.trouverChemin(mv, req, res, prefix, suffix);

        } catch (AuthentificationException e) {
            String loginUrl = this.getInitParameter("loginUrl");
            if (loginUrl != null) {
                res.sendRedirect(req.getContextPath() + "/" + loginUrl);
            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setContentType("text/plain;charset=UTF-8");
            res.getWriter().println("Erreur : " + e.getClass().getName() + " : " + e.getMessage());
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
