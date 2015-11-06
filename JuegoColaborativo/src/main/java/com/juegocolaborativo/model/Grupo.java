package com.juegocolaborativo.model;

import java.util.ArrayList;

public class Grupo {
	
	private String nombre;
	private Consigna consigna;
	private Camino camino;
	private ArrayList<Subgrupo> subgrupos;
	
	public Grupo() {
		super();			
	}
	
	public Grupo(String nombre, Consigna consigna) {
		super();
		this.nombre = nombre;
		this.consigna = consigna;		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Consigna getConsigna() {
		return consigna;
	}

	public void setConsigna(Consigna consigna) {
		this.consigna = consigna;
	}
	
	public Camino getCamino() {
		return camino;
	}
	
	public void setCamino(Camino camino) {
		this.camino = camino;
	}
	
	public ArrayList<Subgrupo> getSubgrupos() {
		return subgrupos;
	}

	public void setSubgrupos(ArrayList<Subgrupo> subgrupos) {
		this.subgrupos = subgrupos;
	}
	
	public Posta getSiguientePosta(Posta posta) {
		return this.getCamino().getSiguientePosta(posta);
	}
	
}