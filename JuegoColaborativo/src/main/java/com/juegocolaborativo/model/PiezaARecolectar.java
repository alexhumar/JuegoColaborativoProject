package com.juegocolaborativo.model;

import java.util.ArrayList;

public class PiezaARecolectar {
	
	private int id;
	private String nombre;
	private String descripcion;
	private Poi poi;
	private ArrayList<Consigna> consignas;
	private boolean visitada;
	
	public PiezaARecolectar(int id, String nombre, String descripcion, Poi poi, ArrayList<Consigna> consignas) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.poi = poi;
		this.consignas = consignas;
		this.visitada = false;
		if (poi != null) poi.setPieza(this);
	}
	
	public synchronized int getId() {
		return id;
	}

	public synchronized void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Poi getPoi() {
		return poi;
	}

	public void setPoi(Poi poi) {
		this.poi.setPieza(null);
		this.poi = poi;
		if (poi != null) poi.setPieza(this);
	}

	public ArrayList<Consigna> getConsignas() {
		return consignas;
	}

	public void setConsignas(ArrayList<Consigna> consignas) {
		this.consignas = consignas;
	}
	
	public synchronized boolean isVisitada() {
		return visitada;
	}

	public synchronized void setVisitada(boolean visitada) {
		this.visitada = visitada;
	}

}