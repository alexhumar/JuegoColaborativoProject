package com.juegocolaborativo.model;

public class Consulta {
	
	private int id;
	private String nombrePieza;
	private String descripcionPieza;
	private int cumple;
	private String justificacion;
	private int respondida;
	
	public Consulta(int id, String nombrePieza, String descripcionPieza,
			int cumple, String justificacion) {
		super();
		this.id = id;
		this.nombrePieza = nombrePieza;
		this.descripcionPieza = descripcionPieza;
		this.cumple = cumple;
		this.justificacion = justificacion;
		this.respondida = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombrePieza() {
		return nombrePieza;
	}

	public void setNombrePieza(String nombrePieza) {
		this.nombrePieza = nombrePieza;
	}

	public String getDescripcionPieza() {
		return descripcionPieza;
	}

	public void setDescripcionPieza(String descripcionPieza) {
		this.descripcionPieza = descripcionPieza;
	}

	public int getCumple() {
		return cumple;
	}

	public void setCumple(int cumple) {
		this.cumple = cumple;
	}

	public String getJustificacion() {
		return justificacion;
	}

	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
	}

	public int getRespondida() {
		return respondida;
	}

	public void setRespondida(int respondida) {
		this.respondida = respondida;
	}	
	
}