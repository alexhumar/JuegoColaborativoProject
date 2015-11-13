package com.juegocolaborativo.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Subgrupo {
	
	private int id;
	private String nombre;
	private Grupo grupo;
	private ArrayList<Integer> idsConsultasQueMeHicieron;
	private int estado;
	private Consulta consultaRealizada;
	private HashMap<Integer,Respuesta> respuestas;
	private int cantidadRespuestas;
	private Resultado resultado;
	private Posta posta;
	private Consulta consultaActual;

	public static final int ESTADO_CREADO = 1;
	public static final int ESTADO_INICIAL = 2;
	public static final int ESTADO_JUGANDO = 3;
	public static final int ESTADO_FINAL = 4;
		
	public Subgrupo(int id) {
		super();
		this.id = id;
		this.cantidadRespuestas = 0;
	}
	
	public Subgrupo(int id, String nombre, Grupo grupo, ArrayList<Integer> idsConsultasQueMeHicieron, Consulta consultaRealizada, Posta posta) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.grupo = grupo;
		this.idsConsultasQueMeHicieron = idsConsultasQueMeHicieron;
		this.estado = Subgrupo.ESTADO_INICIAL;
		this.consultaRealizada = consultaRealizada;	
		this.cantidadRespuestas = 0;
		this.resultado = new Resultado();
		this.posta = posta;
		if (posta != null) posta.setSubgrupo(this);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}
	
	public boolean piezaVisitada() {
		return this.getPiezaARecolectar().isVisitada();
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public ArrayList<Integer> getIdsConsultasQueMeHicieron() {
		return idsConsultasQueMeHicieron;
	}

	public void setIdsConsultasQueMeHicieron(
			ArrayList<Integer> idsConsultasQueMeHicieron) {
		this.idsConsultasQueMeHicieron = idsConsultasQueMeHicieron;
	}
	
	public Consulta getConsultaRealizada() {
		return consultaRealizada;
	}

	public void setConsultaRealizada(Consulta consultaRealizada) {
		this.consultaRealizada = consultaRealizada;
	}

	public HashMap<Integer, Respuesta> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(HashMap<Integer, Respuesta> respuestas) {
		this.respuestas = respuestas;
	}

	public int getCantidadRespuestas() {
		return cantidadRespuestas;
	}

	public void setCantidadRespuestas(int cantidadRespuestas) {
		this.cantidadRespuestas = cantidadRespuestas;
	}	
	
	public synchronized Resultado getResultado() {
		return resultado;
	}

	public synchronized void setResultado(Resultado resultado) {
		this.resultado = resultado;
	}
	
	public Posta getPosta() {
		return posta;
	}
	
	public void setPosta(Posta posta) {
		if (this.posta != null) this.posta.setSubgrupo(null);
		this.posta = posta;
		if (posta != null) posta.setSubgrupo(this);
	}
	
	public Posta getSiguientePosta(Posta posta) {
		//Termina utilizando a la clase camino para obtener la Posta siguiente.
		return this.getGrupo().getSiguientePosta(posta);
	}
	
	public PiezaARecolectar getPiezaARecolectar() {
		return this.getPosta().getPiezaARecolectar();
	}
	
	public Poi getPoiAsignado() {
		return this.getPosta().getPoi();
	}
	
	public Poi getSiguientePoi() {
		return this.getSiguientePosta(this.getPosta()).getPoi();
	}
	
	public Consulta getConsultaActual() {
		return consultaActual;
	}

	public void setConsultaActual(Consulta consultaActual) {
		this.consultaActual = consultaActual;
	}
	
}