package model;

public class Adjunto {
    private int id;
    private int casoId;
    private String nombre;
    private byte[] contenido;

    public Adjunto(int id, int casoId, String nombre, byte[] contenido) {
        this.id = id;
        this.casoId = casoId;
        this.nombre = nombre;
        this.contenido = contenido;
    }
    // Getters
    public int getId() { return id; }
    public int getCasoId() { return casoId; }
    public String getNombre() { return nombre; }
    public byte[] getContenido() { return contenido; }
    
    //Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setContenido(byte[] contenido) { this.contenido = contenido; }
    
    
}