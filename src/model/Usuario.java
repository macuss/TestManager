package model;

public class Usuario {

    private int id;
    private String alias;
    private String nombre;
    private String apellido;
    private String legajo;
    private String password;
    private String rol;

    public Usuario(String alias, String nombre, String apellido, String legajo, String password) {
        this.alias = alias;
        this.nombre = nombre;
        this.apellido = apellido;
        this.legajo = legajo;
        this.password = password;
    }

    public Usuario(int id, String alias, String nombre, String apellido, String legajo, String password, String rol) {
        this.id = id;
        this.alias = alias;
        this.nombre = nombre;
        this.apellido = apellido;
        this.legajo = legajo;
        this.password = password;
        this.rol = rol;
    }
    
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.rol);
    }

    public int getId() { return id; }
    public String getAlias() { return alias; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getLegajo() { return legajo; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
}
