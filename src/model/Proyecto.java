package model;

public class Proyecto {

    private int id;
    private String nombre;
    private String descripcion;
    private int usuarioId;

    public Proyecto(String nombre, String descripcion, int usuarioId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
    }

    public Proyecto(int id, String nombre, String descripcion, int usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getUsuarioId() { return usuarioId; }
}
