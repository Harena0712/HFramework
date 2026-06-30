package service;

import java.util.Objects;

public class UtilMethode {
    private String url;
    private String methode;

    public UtilMethode(String url, String methode) {
        this.url = url;
        this.methode = methode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UtilMethode that = (UtilMethode) obj;
        return this.url.equals(that.url) && this.methode.equals(that.methode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, methode);
    }
}
