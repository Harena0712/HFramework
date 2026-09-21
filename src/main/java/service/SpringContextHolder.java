package service;

import org.springframework.context.ApplicationContext;

public class SpringContextHolder {

    private static ApplicationContext context;

    private SpringContextHolder() {
    }

    public static void setContext(ApplicationContext ctx) {
        context = ctx;
    }

    public static ApplicationContext getContext() {
        if (context == null) {
            throw new IllegalStateException(
                    "Le contexte Spring n'est pas initialisé. Vérifiez qu'un listener "
                            + "(ex: SpringBootstrapListener) appelle bien SpringContextHolder.setContext(...) "
                            + "au démarrage de l'application, avant qu'un controller ne l'utilise.");
        }
        return context;
    }

    public static <T> T getBean(Class<T> type) {
        return getContext().getBean(type);
    }

    public static Object getBean(String name) {
        return getContext().getBean(name);
    }
}
