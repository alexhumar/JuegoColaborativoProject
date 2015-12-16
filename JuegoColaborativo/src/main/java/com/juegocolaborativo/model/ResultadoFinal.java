package com.juegocolaborativo.model;

public class ResultadoFinal implements Comparable<ResultadoFinal> {
	
	private String nombreGrupo;
	private int puntaje;
	
	public ResultadoFinal(String nombreGrupo, int puntaje) {
		super();
		this.nombreGrupo = nombreGrupo;
		this.puntaje = puntaje;
	}

	public synchronized String getNombreGrupo() {
		return nombreGrupo;
	}

	public synchronized void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public synchronized int getPuntaje() {
		return puntaje;
	}

	public synchronized void setPuntaje(int puntaje) {
		this.puntaje = puntaje;
	}

	@Override
	public int compareTo(ResultadoFinal compareResultadoFinal) {
		int compareResultado = ((ResultadoFinal) compareResultadoFinal).getPuntaje(); 
		 
		return (compareResultado == this.getPuntaje()) ? 1 : (compareResultado - this.getPuntaje());
	}

}