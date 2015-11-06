package com.juegocolaborativo.model;

public class Camino {

	private String descripcion;
	private Posta primerPosta;
	private Posta postaActual;
	
	public Camino(String descripcion, Posta primerPosta) {
		super();
		this.descripcion = descripcion;
		this.primerPosta = primerPosta;
		this.postaActual = primerPosta;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Posta getPrimerPosta() {
		return primerPosta;
	}
	
	public void setPrimerPosta(Posta primerPosta) {
		this.primerPosta = primerPosta;
	}
	
	public Posta getPostaActual() {
		return postaActual;
	}
	
	public void setPostaActual(Posta postaActual) {
		this.postaActual = postaActual;
	}
	
	public Posta getSiguientePosta(Posta posta) {
		return posta.getSiguientePosta();
	}
	
	public Posta getSiguientePosta() {
		return this.getPostaActual().getSiguientePosta();
	}
	
	public void avanzar() {
		this.setPostaActual(this.getSiguientePosta());
	}
	
}