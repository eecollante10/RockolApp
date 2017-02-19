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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


/**
 * Clase que representa el servidor
 * @author EdgardEduardo
 *
 */
public class Servidor extends Thread
{

	//---------------------------------------
	//Atributos
	//---------------------------------------

	/**
	 * Coleccion de conexiones
	 */
	protected Collection conexiones;

	/**
	 * El numero puerto por donde va a recibir conexiones
	 */
	private int puerto;

	/**
	 * Socket receptor de las conexiones
	 */
	private ServerSocket receptor;

	/**
	 * La lista de reproduccion principal
	 */
	private ListaReproductor lista;

	/**
	 * Permite cerrar terminar la ejecucion del hili voliendo esta variable falsa
	 */
	private boolean abierto;



	//---------------------------------------
	//Constructor
	//---------------------------------------

	/**
	 * Constructor de la clase servidor
	 * @param p el puerto por donde va a recibir conexiones
	 * @param l el reproductor de las canciones
	 */
	public Servidor(int p, ListaReproductor l)
	{
		super.setName("Servidor De Canciones");
		puerto = p;
		lista = l;
		abierto = true;
		conexiones = new Vector();
	}

	//--------------------------------------
	//Metodos
	//--------------------------------------

	/**
	 * Devuelve las conexiones en proceso
	 * @return coleccion de conexiones en proceso
	 */
	public Collection darConexionesEnProceso()
	{
		Collection conexionesEnProceso = new Vector( );

        // Armar la lista actualizada con los encuentros que no han terminado
        Iterator iter = conexiones.iterator( );
        while( iter.hasNext( ) )
        {
            Conexion c = (Conexion)iter.next( );
            if( !c.conexionTerminada() )
                conexionesEnProceso.add( c );
        }

        return conexionesEnProceso;
	}

	/**
	 * Devuelve todas las conexiones desde que se inicio el programa
	 * @return coleccion de conexiones
	 */
	public Collection darConexiones()
	{
		return conexiones;
	}

	/**
	 * Termina de ejecutar el hilo
	 */
	public void parar()
	{
		abierto = false;

		Iterator iter = conexiones.iterator();
		int  i = 0;
		while(iter.hasNext())
		{
			i++;
			Conexion con = (Conexion)iter.next();
			System.out.println(con.toString()+" : "+i+" : "+con.darMensaje());
			con.parar();
		}
		try
		{
			receptor.close();
		}
		catch(SocketException e)
		{
			e.printStackTrace();;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Guarda la informacion de los usuarios
	 * @return true si guardo exitosamente
	 */
	public boolean guardarInfoUsuarios()
	{
		//@TODO
		return false;
	}

	/**
	 * Recibe las conexiones y las conecta
	 */
	public void recibirConexiones()
	{
		try
        {
            receptor = new ServerSocket( puerto );
            System.out.println("servidor sirviendo");

            while( abierto )
            {

                // Esperar una nueva conexi�n
                Socket socketNuevoCliente = receptor.accept( );


                // Intentar iniciar una conexion con el nuevo cliente
                crearConexion( socketNuevoCliente );
            }
        }
		catch(SocketException e)
		{
			System.out.println("|||---Se cerró el socket---|||");
		}
        catch( IOException e )
        {
            e.printStackTrace();
        }
		finally
	    {
	         try
            {
	              receptor.close( );
	        }
	        catch( IOException e )
	        {
	             e.printStackTrace( );
	        }
	    }
	}

	/**
	 * Metodo de la clase thread
	 */
	public void run()
	{
		recibirConexiones();
	}

	/**
	 * Inicia la conexion con el usuario
	 * @param s canal al nuevo usuario
	 */
	synchronized protected void crearConexion(Socket s)
	{
		try
        {
            Conexion nueva = new Conexion( s, lista );
            System.out.println(nueva.toString());
            System.out.println("InfoSocket: "+s);
            conexiones.add(nueva);
            nueva.start();

            synchronized (this)
            {
				try
				{
					this.wait(1000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
            }
            System.out.println("nueva="+nueva.toString());
         }
        catch( IOException e )
        {
            try
            {
                s.close( );
            }
            catch( IOException e1 )
            {
                e.printStackTrace( );
            }

            // Mostrar la excepci�n original
            e.printStackTrace( );
        }
	}
}
