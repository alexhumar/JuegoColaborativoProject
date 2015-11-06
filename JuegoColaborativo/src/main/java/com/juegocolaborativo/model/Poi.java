package com.juegocolaborativo.model;

public class Poi {
	
	private Coordenada coordenadas;
	private PiezaARecolectar pieza;

	public Poi(Coordenada coordenadas) {
		super();
		this.coordenadas = coordenadas;
	}
	
	public Coordenada getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(Coordenada coordenadas) {
		this.coordenadas = coordenadas;
	}
	
	public PiezaARecolectar getPieza() {
		return pieza;
	}
	
	public void setPieza(PiezaARecolectar pieza) {
		//IMPORTANTE: este metodo no deberia llamarse sino a traves de setPoi de PiezaARecolectar, para asegurar la consistencia.
		this.pieza = pieza;
	}

}