package co.anarquianegra.rockolappServidor.mundo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Stack;


import ListaOrdenada.IListaOrdenada;


/**
 * Clase que representa  una conexion
 * @author EdgardEduardo
 *
 */
public class Conexion extends Thread
{
	//----------------------------------------
	//Constantes
	//----------------------------------------
	
	public final static String INFO_USUARIO = "informacion usuario";
	
	public final static String BUSCAR_POR_NOMBRE = "buscar por nombre";
	
	public final static String BUSCAR_POR_ARTISTA = "buscar por artista";
	
	public final static String AGREGAR_CANCION = "agregar cancion";
	
	public final static String AGREGAR_LISTA = "agregar lista";

	//----------------------------------------
	//Atributos
	//----------------------------------------
	
	/**
	 * Socket del usuario
	 */
	private Socket canal;
	
	/**
	 * Indica si la lista de reproduccion escogida por el usuario
	 * ha sido agregada a la lista de reproduccion grande
	 */
	private boolean listaAgregada;
	
	/**
	 * Informacion del usuario
	 */
	private String infoUsuario;
	
	/**
	 * Arreglo de canciones a donde el usuario puede agregar canciones
	 */
	private Stack canciones;
	
	/**
	 * La lista de reproduccion principal
	 */
	private ListaReproductor lista;
	
	/**
	 * Escribe los datos para mandar
	 */
	private PrintWriter out;
	
	/**
	 * Lee los datos que entran
	 */
	private BufferedReader in;
	
	/**
	 * Indica si se debe terminar la conexion
	 */
	private boolean conexionTerminada;
	
	/**
	 * Mensaje recibido
	 */
	private String mens;
	
	/**
	 * Tiempo en que se agrego la ultima lista
	 */
	private long tiempo;
	
	//----------------------------------------
	//Constructores
	//----------------------------------------
	
	/**
	 * Constructor de la clase Conexion
	 * @param c
	 */
	public Conexion(Socket c, ListaReproductor l) throws IOException
	{
		canal = c;
		lista = l;
		listaAgregada = false;
		conexionTerminada = false;
		infoUsuario = "";
		canciones = new Stack();
		
		tiempo = -1;
		
		out = new PrintWriter( canal.getOutputStream( ), true );
        in = new BufferedReader( new InputStreamReader( canal.getInputStream( ) ) );
        
		mens = "Ningun mensaje";
	}
	
	//----------------------------------------
	//Metodos
	//----------------------------------------
	
	/**
	 * Dice si el usuario ya envio la informacion de las canciones
	 * @return listaAgregada
	 */
	public boolean conexionTerminada()
	{
		return conexionTerminada;
	}
	
	/**
	 * Devuelve la informacion del usuario
	 * @return infoUsuario
	 */
	public String toString()
	{
		return infoUsuario+canal.getInetAddress();
	}
	
	/**
	 * Devuelve el ultimo mensaje recibido
	 */
	public String darMensaje()
	{
		return mens;
	}
	
	/**
	 * Devuelve el tiempo de ultima agregada
	 * @return tiempo
	 */
	public long darTiempo()
	{
		return tiempo;
	}
	
	/**
	 * Para la ejecucion de este hilo
	 */
	public void parar()
	{
		conexionTerminada = true;
		try
		 {
			 canal.close();
		 }
		 catch(IOException e)
		 {
			 e.printStackTrace();
		 }
	}
	
	/**
	 * Metodo proveniente de la clase thread
	 * dentro de este se inicia la conexion
	 * mientras se este ejecutando el usuario todavia 
	 * podra mandar su lista de canciones
	 */
	public void run()
	{
		 try
         {
             iniciarConexion( );

             while( !conexionTerminada )
             {
                 procesarMensaje( );
             }
         }
         catch( Exception e )
         {
        	 conexionTerminada = true;
        	 e.printStackTrace();
        	 System.out.println("Conexion terminada: "+canal.getInetAddress()+" : "+ e.getMessage());
        	 
        	 try
             {
                 in.close( );
                 out.close( );
                 canal.close( );
             }
             catch( IOException e2 )
             {
                 e2.printStackTrace( );
             }
         }
		 finally
		 {
			 if(canal != null)
			 {
				 try
				 {
					 in.close();
					 out.close();
					 canal.close();
				 }
				 catch(IOException e)
				 {
					 e.printStackTrace();
				 }
			 }
		 }
	}
	
	/**
	 * Inicia la conexion con el usuario
	 * le manda la informacon necesaria
	 * para recibir la informacion del ususario
	 */
	protected void iniciarConexion() throws IOException
	{
		if(!listaAgregada)
		{
			String linea = in.readLine();
			String[] info = linea.split(";");
			
			
			if(info[0].equals(INFO_USUARIO))
			{
				System.out.println(linea);
				infoUsuario = info[1];
				out.println("InfoUsuario Recibida");
			}
			System.out.println(linea);
		}
	}
	
	/**
	 * Agrega la lista de canciones que el usuario envio
	 * a la lista de reproduccion grande
	 */
	protected void agregarLista()
	{
		lista.agregarLista(canciones, infoUsuario);
		listaAgregada = true;
	}
	
	//---------------------------------------
	//Metodos privados
	//---------------------------------------
	
	/**
	 * Procesa cualquier mensaje que provenga del usuario
	 */
	private void procesarMensaje() 
	{
			String linea = null;
			String[] info = null;
			try
			{
				linea = in.readLine();
			}
			catch(IOException e)
			{
				System.out.println("Error al leer linea: "+e.getMessage());
				conexionTerminada = true;
			}	
			
			if(linea != null)
			{
				if(linea.equals("null"))
					parar();
				info = linea.split(";");
				System.out.println("Mensaje : "+linea);
				mens = linea;
			}
			//--------indica en cuanto tiempo se puede volver a enviar
			if(tiempo != -1 && System.currentTimeMillis() - tiempo >= 600000)
				listaAgregada = false;
			if(info != null && info[0].equals(INFO_USUARIO))
			{
				infoUsuario = info[1];
			}
			else if(info != null && info[0].equals(BUSCAR_POR_NOMBRE))
			{
				IListaOrdenada resultado = buscarCancionesPorNombre(info[1]);
				if(resultado != null)
					System.out.println("resultados : "+resultado.darLongitud());
				for(int i = 0;resultado != null && i<resultado.darLongitud(); i++)
				{
					Cancion c = (Cancion)resultado.dar(i);
					System.out.println(c.toString());
					out.println(AGREGAR_CANCION+";"+c.toString());
				}
				out.println("TERMINO");
			}
			else if(info != null && info[0].equals(BUSCAR_POR_ARTISTA))
			{
				IListaOrdenada resultado = buscarCancionesPorArtista(info[1]);
				if(resultado != null)
				System.out.println("resultados : "+resultado.darLongitud());
					
				for(int i = 0; resultado != null && i<resultado.darLongitud(); i++)
				{
					Cancion c = (Cancion)resultado.dar(i);
					System.out.println(c.toString());
					out.println(AGREGAR_CANCION+";"+c.toString());
				}
				out.println("TERMINO");
			}
			else if(!listaAgregada && info != null && info[0].equals(AGREGAR_CANCION))
			{
				Cancion c = lista.buscarCancionesPorId(info[1]);
				System.out.println("pId: "+info[1]);
				System.out.println("cancion a agregar _ "+c);
				canciones.push(c);
			}
			else if(!listaAgregada && info != null && info[0].equals(AGREGAR_LISTA))
			{
				agregarLista();	
				tiempo = System.currentTimeMillis();
			}
			else if(listaAgregada && info != null && info[0].equals(AGREGAR_LISTA))
			{
				System.out.println("debes esperar 10");
				out.println("MENSAJE;Debes esperar un tiempo de 10 minutos despues de tu ultima agregada para volver a agregar canciones a la lista");
			}
	}
	
	/**
	 * Busca canciones por nombre y las devuelve al metodo
	 * procesar mensaje que es el que lo invoca
	 * @param pNombre el nombre de la cancion a buscar
	 * @return lista ordenada con las canciones que se encontraron con ese nombre
	 */
	private IListaOrdenada buscarCancionesPorNombre(String pNombre)
	{
		return lista.buscarCancionesPorNombre(pNombre);
	}
	
	/**
	 * Busca canciones por artista y las devuelve al metodo
	 * procesar mensaje que es el que lo invoca
	 * @param pArtista el nombre de la cancion a buscar
	 * @return lista ordenada con las canciones que se encontraron con ese nombre
	 */
	private IListaOrdenada buscarCancionesPorArtista(String pArtista)
	{
		return lista.buscarCancionesPorArtista(pArtista);
	}

}
