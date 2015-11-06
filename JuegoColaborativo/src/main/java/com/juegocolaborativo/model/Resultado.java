package com.juegocolaborativo.model;

public class Resultado {
	
	private String pieza;
	private boolean cumple;
	private boolean correcta;
	
	public Resultado() {
		super();
	}
	
	public Resultado(String pieza, boolean cumple, boolean correcta) {
		super();
		this.pieza = pieza;
		this.cumple = cumple;
		this.correcta = correcta;
	}
	
	public String getPieza() {
		return pieza;
	}

	public void setPieza(String pieza) {
		this.pieza = pieza;
	}

	public boolean getCumple() {
		return cumple;
	}

	public void setCumple(boolean cumple) {
		this.cumple = cumple;
	}

	public boolean getCorrecta() {
		return correcta;
	}

	public void setCorrecta(boolean correcta) {
		this.correcta = correcta;
	}

}