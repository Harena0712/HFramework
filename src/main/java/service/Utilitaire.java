package service;

import java.util.*;

import definition.UrlMapping;

import java.io.File;
import java.lang.annotation.*;
import java.lang.reflect.Method;

public class Utilitaire {
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

    public String lireMethodeAndClass(String valeurRecherchee, String packageName) throws Exception {
        String resultat = "";

        List<String> urlsDisponibles = new ArrayList<>();

        List<Class<?>> classes = getClassesParPackage(packageName);

        for (Class<?> clazz : classes) {
            for (Method methode : clazz.getDeclaredMethods()) {
                if (methode.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping ann = methode.getAnnotation(UrlMapping.class);
                    urlsDisponibles.add(ann.url());

                    if (ann.url().equals(valeurRecherchee)) {
                        resultat = "Methode appellé " + methode.getName() + " dans la class " + clazz.getSimpleName();
                        return resultat;
                    }
                }
            }
        }

        return "URL '" + valeurRecherchee + "' non trouvée, les URLs disponibles sont : " + String.join(", ", urlsDisponibles);
    }
}
