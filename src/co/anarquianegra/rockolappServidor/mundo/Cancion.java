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
package co.anarquianegra.rockolappServidor.mundo;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;

/**
 * Clase que representa una cancion en servidor
 * @author EdgardEduardo
 *
 */
public class Cancion
{

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
	 * Nombre del archivo de la cancion
	 */
	private String archivo;

	/**
	 * Nombre del album de la cancion
	 */
	private String album;

	/**
	 * La extension del archivo
	 */
	private String extension;

	/**
	 * La id si la cancion es de soundcloud
	 */
	private long id;

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
	public Cancion(String pNombre, String pArtista, String pAlbum, String pArchivo)
	{
		nombre = pNombre;
		artista = pArtista;
		archivo = pArchivo;
		album = pAlbum;
		id = -1;

		int j = archivo.lastIndexOf('.');
		if (j > 0)
		{
		    extension = archivo.substring(j+1);
		}
	}

	/**
	 * Constructor de la clase Cancion
	 * @param pNombre el nombre de la cancion
	 * @param pArtista el artista de la cancion
	 * @param pArchivo el nombre del archivo de la cancion
	 */
	public Cancion(String pNombre, String pArtista, String pArchivo)
	{
		nombre = pNombre;
		artista = pArtista;
		archivo = pArchivo;
		album = "Album Desconocido";
		int j = archivo.lastIndexOf('.');
		if (j > 0)
		{
		    extension = archivo.substring(j+1);
		}
	}

	/**
	 * Constructor de la clase Cancion
	 * @param pNombre el nombre de la cancion
	 * @param pArchivo nombre del archivo de la cancion
	 */
	public Cancion(String pNombre, String pArchivo)
	{
		nombre = pNombre;
		archivo = pArchivo;
		artista = "Artista Desconocido";
		album = "Album Desconocido";
		int j = archivo.lastIndexOf('.');
		if (j > 0)
		{
		    extension = archivo.substring(j+1);
		}
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
	 * Devuelve el nombre del archivo de la cancion
	 * @return archivo
	 */
	public String darArchivo()
	{
		if(archivo.startsWith("http"))
		{
			ApiWrapper wrapper = new ApiWrapper(ListaReproductor.client_id, ListaReproductor.client_secret, null, null);
			try {
				HttpResponse resp = wrapper.get(new Request(archivo+"?allow_redirects=false"));
				String s = Http.formatJSON(Http.getString(resp));
				try {
					JSONObject stream = (JSONObject)new JSONParser().parse(s);
					if(((String)stream.get("status")).equals("302 - Found"))
						return (String)stream.get("location");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return archivo;
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
	 * Devuelve la extension del archivo
	 * @return extension
	 */
	public String darExtension()
	{
		return extension;
	}

	/**
	 * Pne la id si la cancion es de soundcloud
	 * @param id
	 */
	public void setId(long pId)
	{
		id = pId;
	}

	/**
	 * Devuelve el nombre y el artista si tiene
	 * @return <nombre> : <artista>
	 */
	public String toString()
	{
		if(id > -1)
			return nombre+" : "+artista+" : "+album+" :"+id;
		else
			return nombre+" : "+artista+" : "+album;
	}
}
