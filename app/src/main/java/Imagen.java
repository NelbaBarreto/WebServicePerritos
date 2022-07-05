import android.graphics.Bitmap;

public class Imagen {
    private int tamanio;
    private String url;
    private Bitmap foto;

    public Imagen(int tamanio, String url, Bitmap foto) {
        this.tamanio = tamanio;
        this.url = url;
        this.foto = foto;
    }

    public Imagen() {
        this.tamanio = 0;
        this.url = "";
        this.foto = null;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }
}
