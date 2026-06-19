package controller;

import java.io.*;
import java.util.List;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import service.Utilitaire;
import definition.*;

@Controller
public class FrontServletController extends HttpServlet {

    private List<String> classNameController;
    private Utilitaire utilitaire = new Utilitaire();

    // public void init() throws ServletException {
    //     String packageName = this.getInitParameter("packageName");

    //     try {
    //         classNameController = utilitaire.getAllClassesWithAnnotationInPackage(packageName, Controller.class);
    //     } catch (Exception e) {
    //         throw new ServletException(e);
    //     }
    // }

    public void proccessRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain;charset=UTF-8");

        String path = req.getRequestURI().toString();

        PrintWriter out = res.getWriter();
        out.println("Path : " + path);

        // for (String className : classNameController) {
        //     out.println("Class : " + className);
        // }

        List<String> classNameControllerFromListener = (List<String>) getServletContext().getAttribute("classNameController");

        if (classNameControllerFromListener == null) {
            out.println("Class from listener is null");
        } else {
            for (String className : classNameControllerFromListener) {
                out.println("Class from listener : " + className);
            }
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        proccessRequest(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        proccessRequest(req, res);
    }
}
