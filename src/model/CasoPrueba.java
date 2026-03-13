package model;

public class CasoPrueba {

    private int id;
    private String nombre;
    private String descripcion;
    private Estado estado;
    private int escenarioId;
    private String criterioAceptacion;
    private String givenStep;
    private String whenStep;
    private String thenStep;
  

    public CasoPrueba(String nombre, String descripcion, Estado estado, int escenarioId, String criterioAceptacion, String givenStep, String whenStep, String thenStep) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.escenarioId = escenarioId;
        this.criterioAceptacion = criterioAceptacion;
        this.givenStep = givenStep;
        this.whenStep = whenStep;
        this.thenStep = thenStep;
    }

    public CasoPrueba(int id, String nombre, String descripcion, Estado estado, int escenarioId, String criterioAceptacion, String givenStep, String whenStep, String thenStep) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.escenarioId = escenarioId;
        this.criterioAceptacion = criterioAceptacion;
        this.givenStep = givenStep;
        this.whenStep = whenStep;
        this.thenStep = thenStep;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Estado getEstado() { return estado; }
    public int getEscenarioId() { return escenarioId; }
    public String getCriterioAceptacion() { return criterioAceptacion; }
    public String getGivenStep() { return givenStep; }
    public String getWhenStep() { return whenStep; }
    public String getThenStep() { return thenStep; }
    
    
    public String toGherkin() {
        return "Scenario: " + nombre + "\n"
             + "  Given " + givenStep + "\n"
             + "  When " + whenStep + "\n"
             + "  Then " + thenStep + "\n";
    }

	public void setNombre(String text) {
		
		this.nombre = text;
	}
    
	public void setDescripcion(String text) {
		this.descripcion = text;
		
	}
	
	public void setEstado(Estado selectedItem) {
		this.estado = selectedItem;
		
	}
	
	public void setGivenStep(String text) {
		this.givenStep = text;
		
	}
	
	public void setWhenStep(String text) {
		this.whenStep = text;
		
	}
	
	public void setThenStep(String text) {
		this.thenStep = text;
		
	}

	public void setCriterioAceptacion(String trim) {
		this.criterioAceptacion = trim;
		
	}
	
	
}
