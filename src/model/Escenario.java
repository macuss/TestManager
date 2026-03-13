package model;

public class Escenario {

    private int id;
    private String nombre;
    private String descripcion;
    private int proyectoId;

    public Escenario(String nombre, String descripcion, int proyectoId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.proyectoId = proyectoId;
    }

    public Escenario(int id, String nombre, String descripcion, int proyectoId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.proyectoId = proyectoId;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getProyectoId() { return proyectoId; }
}
