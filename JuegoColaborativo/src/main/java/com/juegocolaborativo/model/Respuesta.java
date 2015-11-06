package com.juegocolaborativo.model;

public class Respuesta {
	
	private Subgrupo subgrupo;
	private int cumple;
	private String justificacion;

	public Respuesta(Subgrupo subgrupo) {
		super();
		this.subgrupo = subgrupo;				
	}
	
	public Subgrupo getSubgrupo() {
		return subgrupo;
	}
	
	public void setSubgrupo(Subgrupo subgrupo) {
		this.subgrupo = subgrupo;
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
	
}