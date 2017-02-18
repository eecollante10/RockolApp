package com.monkey.entonado;

import java.io.Serializable;

/**
 * Clase que representa una cancion en móbil
 * @author EdgardEduardo
 *
 */
public class Cancion implements Serializable
{
	//-----------------------------------
	//Constantes
	//-----------------------------------

	private static final long serialVersionUID = -541387697211453528L;

	//--------------------------------------
	//Atributos
	//--------------------------------------
	
	

	/**
	 * Nombre de la cancion
	 */
	private String nombre;
	
	/**
	 * Artista de la cancion
	 */
	private String artista;
	
	/**
	 * Nombre del album de la cancion
	 */
	private String album;
	
	//-------------------------------------
	//Constructores
	//-------------------------------------
	
	/**
	 * Constructor de la clase Cancion
	 * @param pNombre
	 * @param pArtista
	 * @param pAlbum
	 * @param pArchivo
	 */
	public Cancion(String pNombre, String pArtista, String pAlbum)
	{
		nombre = pNombre;
		artista = pArtista;
		album = pAlbum;
	}
	
	
	//-------------------------------------------
	//Metodos
	//-------------------------------------------
	
	/**
	 * Devuelve el nombre de la cancion
	 * @return nombre
	 */
	public String darNombre()
	{
		return nombre;
	}
	
	/**
	 * Devuelve el artista de la cancion
	 * @return artista
	 */
	public String darArtista()
	{
		return artista;
	}
	
	/**
	 * Devuelve el album en el que esta la cancion
	 * @return album
	 */
	public String darAlbum()
	{
		return album;
	}
	
	/**
	 * Devuelve el nombre y el artista si tiene
	 * @return <nombre> : <artista>
	 */
	public String toString()
	{
		return nombre+" : "+artista+" : "+album;
	}
}
