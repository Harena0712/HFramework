package service;

import java.util.*;

import definition.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.*;

public class Utilitaire {
    public record InvocationResult(Object value, boolean json) {
    }

    public List<String> getAllClassesWithAnnotationInPackage(String packageName,
            Class<? extends Annotation> annotationClass) throws Exception {
        List<String> classNames = new ArrayList<>();

        List<Class<?>> classes = getClassesParPackage(packageName);

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                classNames.add(clazz.getName());
            }
        }
        return classNames;
    }

    public static List<Class<?>> getClassesParPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        String chemin = packageName.replace(".", "/");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        var ressources = classLoader.getResources(chemin);

        while (ressources.hasMoreElements()) {
            File dossier = new File(ressources.nextElement().toURI());

            for (File fichier : dossier.listFiles()) {
                if (fichier.getName().endsWith(".class")) {
                    String nomClasse = fichier.getName().replace(".class", "");
                    classes.add(Class.forName(packageName + "." + nomClasse));
                }
            }
        }
        return classes;
    }

    public Object lireMethodeAndClass(String url, String httpMethode, String packageName,
            Map<UtilMethode, UrlMethode> urlMappings, Map<String, String> parametres) throws Exception {
        return lireMethodeAndClassResult(url, httpMethode, packageName, urlMappings, parametres).value();
    }

    public InvocationResult lireMethodeAndClassResult(String url, String httpMethode, String packageName,
            Map<UtilMethode, UrlMethode> urlMappings, Map<String, String> parametres) throws Exception {

        UtilMethode cle = new UtilMethode(url, httpMethode);
        if (urlMappings == null) {
            throw new Exception(
                    "Erreur : urlMappings est null. Assurez-vous que le ListenerDemarrage a été correctement initialisé.");
        }

        UrlMethode urlMethode = urlMappings.get(cle);
        if (urlMethode != null) {
            String nomMethode = urlMethode.getMethodeName();
            Class<?> classMethod = Class.forName(packageName + "." + urlMethode.getClassName());

            Method methode = null;
            for (Method m : classMethod.getMethods()) {
                if (m.getName().equals(nomMethode)) {
                    methode = m;
                    break;
                }
            }
            if (methode == null) {
                throw new NoSuchMethodException(nomMethode);
            }

            Object instance = classMethod.getDeclaredConstructor().newInstance();

            Parameter[] params = methode.getParameters();
            Object[] args = new Object[params.length];

            // for (int i = 0; i < params.length; i++) {
            //     Parameter p = params[i];
            //     Class<?> type = p.getType();

            //     String nomParam = p.getName();

            //     args[i] = convertir(parametres.get(nomParam), type);
            // }
            
            for (int i = 0; i < params.length; i++) {
                Parameter p = params[i];
                Class<?> type = p.getType();
                String nomParam = p.getName();

                if (estTypeSimple(type)) {
                    args[i] = convertir(parametres.get(nomParam), type);
                } else {
                    args[i] = construireObjet(type, nomParam, parametres);
                }
            }

            Object value;
            try {
                value = methode.invoke(instance, args);
            } catch (InvocationTargetException e) {
                throw (e.getCause() instanceof Exception) ? (Exception) e.getCause() : e;
            }
            return new InvocationResult(value, methode.isAnnotationPresent(JSON.class));
        }

        List<String> urlsDisponibles = new ArrayList<>();
        for (UtilMethode u : urlMappings.keySet()) {
            urlsDisponibles.add("[" + u.getUrl() + ", " + u.getMethode() + "]");
        }
        throw new Exception("Erreur : '" + url + "' [" + httpMethode + "] non trouvée. "
                + "URLs disponibles : " + urlsDisponibles);
    }

    public Map<String, UrlMethode> getAllUrlMappings(String packageName) throws Exception {
        Map<String, UrlMethode> urlMappings = new HashMap<>();

        List<Class<?>> classes = getClassesParPackage(packageName);

        for (Class<?> clazz : classes) {
            for (Method methode : clazz.getDeclaredMethods()) {
                if (methode.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping ann = methode.getAnnotation(UrlMapping.class);
                    urlMappings.put(ann.url(), new UrlMethode(clazz.getSimpleName(), methode.getName()));
                }
            }
        }

        return urlMappings;
    }

    public void getAllUrlMappingsWithUtilMethode(String packageName, Map<UtilMethode, UrlMethode> urlMappings)
            throws Exception {

        List<Class<?>> classes = getClassesParPackage(packageName);

        for (Class<?> clazz : classes) {
            for (Method methode : clazz.getDeclaredMethods()) {
                if (methode.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping ann = methode.getAnnotation(UrlMapping.class);
                    UtilMethode utilMethode = new UtilMethode(ann.url(), ann.methode());
                    UrlMethode urlMethode = new UrlMethode(clazz.getSimpleName(), methode.getName());

                    if (urlMappings != null) {
                        for (Map.Entry<UtilMethode, UrlMethode> entry : urlMappings.entrySet()) {
                            UtilMethode existingUtilMethode = entry.getKey();
                            if (utilMethode.equals(existingUtilMethode)) {
                                throw new Exception("Erruer :  l'url : '" + utilMethode.getUrl() + "' et la methode : '"
                                        + utilMethode.getMethode() + "' existe deja dans la class : '"
                                        + entry.getValue().getClassName() + "' et la methode : '"
                                        + entry.getValue().getMethodeName() + "'");
                            }
                        }
                    }

                    urlMappings.put(utilMethode, urlMethode);
                }
            }
        }
    }

    public void trouverChemin(ModelAndView mv,
            HttpServletRequest request,
            HttpServletResponse response, String prefix, String suffix)
            throws ServletException, IOException {

        Map<String, Object> model = mv.getModel();
        if (model != null) {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        String fullPath = prefix + mv.getViewName() + suffix;

        RequestDispatcher dispatcher = request.getRequestDispatcher(fullPath);
        dispatcher.forward(request, response);
    }

    private Object convertir(String valeur, Class<?> type) {
        if (type == String.class) return valeur;
        if (valeur == null || valeur.isEmpty()) {
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == double.class) return 0.0;
            if (type == boolean.class) return false;
            return null;
        }
        if (type == int.class || type == Integer.class) return Integer.parseInt(valeur);
        if (type == long.class || type == Long.class) return Long.parseLong(valeur);
        if (type == double.class || type == Double.class) return Double.parseDouble(valeur);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(valeur);
        return valeur;
    }

    private boolean estTypeSimple(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || type == Boolean.class
                || type == Character.class
                || Number.class.isAssignableFrom(type);
    }

    private Object construireObjet(Class<?> type, String prefixe, Map<String, String> parametres) throws Exception {
        Object obj = type.getDeclaredConstructor().newInstance();

        for (Field f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) continue;

            String valeur = parametres.get(prefixe + "." + f.getName());
            if (valeur == null) continue;

            f.setAccessible(true);
            f.set(obj, convertir(valeur, f.getType()));
        }
        return obj;
    }
}
