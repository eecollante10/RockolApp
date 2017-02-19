// RockolApp/JukeboxApp -Add songs to the playlist queue of the player from the mobile app
//     Copyright (C) 2016  Edgard Collante
//
//     This program is free software: you can redistribute it and/or modify
//     it under the terms of the GNU Affero General Public License as published
//     by the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.
//
//     This program is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU Affero General Public License for more details.
//
//     You should have received a copy of the GNU Affero General Public License
//     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
