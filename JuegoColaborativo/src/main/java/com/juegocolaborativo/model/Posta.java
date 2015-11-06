package com.juegocolaborativo.model;

public class Posta {
	
	private Posta siguientePosta;
	private Poi poi;
	private Subgrupo subgrupo;
	
	public Posta () {
		super();
	}
	
	public Posta(Posta siguientePosta, Poi poi) {
		super();
		this.siguientePosta = siguientePosta;
		this.poi = poi;
	}
	
	public Posta getSiguientePosta() {
		return siguientePosta;
	}
	
	public void setSiguientePosta(Posta siguientePosta) {
		this.siguientePosta = siguientePosta;
	}
	
	public Poi getPoi() {
		return poi;
	}
	
	public void setPoi(Poi poi) {
		this.poi = poi;
	}
	
	public Subgrupo getSubgrupo() {
		return subgrupo;
	}
	
	public void setSubgrupo(Subgrupo subgrupo) {
		//IMPORTANTE: este metodo deberia invocarse unicamente a traves del setPosta de Subgrupo para asegurar la consistencia.
		this.subgrupo = subgrupo;
	}
	
	public PiezaARecolectar getPiezaARecolectar() {
		return this.getPoi().getPieza();
	}
	
	public boolean isVisitada() {
		return this.getPiezaARecolectar().isVisitada();
	}
	
}