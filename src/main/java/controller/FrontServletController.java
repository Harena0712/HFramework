package controller;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontServletController extends HttpServlet {
    
    public void proccessRequest(HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException { 
        res.setContentType("text/plain;charset=UTF-8");

        String path = req.getRequestURI().toString();

        PrintWriter out = res.getWriter();
        out.println("Path : " + path);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	proccessRequest(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	proccessRequest(req, res);
    }
}
