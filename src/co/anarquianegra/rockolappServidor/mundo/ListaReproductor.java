package co.anarquianegra.rockolappServidor.mundo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Stack;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.audio.AudioParser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.sun.media.jfxmediaimpl.platform.Platform;

import ListaOrdenada.IListaOrdenada;
import ListaOrdenada.ListaOrdenada;
import TablaHashing.ITablaHashing;
import TablaHashing.TablaHashing;
import co.anarquianegra.rockolappServidor.Main;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 * Clase que representa el reproductor de la lista de canciones
 * @author EdgardEduardo
 *
 */
public class ListaReproductor
{
	//---------------------------------------
	//Constantes
	//---------------------------------------
	
	public final static String client_secret = "bc408523039a6c6f8ebfefb650692620";
	
	public final static String client_id = "0a679292c57922b30c2bd1d475f8063a";

	//---------------------------------------
	//Atributos
	//---------------------------------------
	
	/**
	 * Devuelve un numreo con la cantidad de conexiones 
	 * que pudieron agregar canciones a la lista
	 */
	private int cantidadConexionesPositivas;
	
	/**
	 * Tabla hashing de canciones que tienen como llave el nombre
	 */
	private ITablaHashing<String, Cancion> cancionesXnombre;
	
	/**
	 * Tabla hashing de canciones que tienen como llave el artista
	 */
	private ITablaHashing<String, Cancion> cancionesXartista;
	
	/**
	 * Tabla hashing de canciones que tienen como llave el id
	 */
	private ITablaHashing<String, Cancion> cancionesXid;
	
	/**
	 * Lista de las canciones en la lista de reproduccion
	 */
	private SimpleListProperty<Cancion> listaReproduccion;
	
	/**
	 * Capacidad preferible de las tablas de hashing
	 */
	private int capacidad;
	
	/**
	 * Dice la cantidad de canciones
	 */
	private int cant;
	
	/**
	 * Archivo de propiedades con las rutas de las carpetas
	 * y canciones a cargar
	 */
	private Properties rutasCanciones;
	
	/**
	 * Reproductor de audio
	 */
	private MediaPlayer mediaPlayer;
	
	/**
	 * Sonidos a ser reproducidos por el mediaPlayer
	 */
	private Media media;
	
	/**
	 * Si prendido indica que las canciones se deben reproducir
	 */
	private boolean playing;
	
	//---------------------------------------------
	//Constructor
	//---------------------------------------------
	
	/**
	 * Constructor de la clase ListaReproductor
	 */
	public ListaReproductor(Main interfaz)
	{
		playing = false;
		ObservableList<Cancion> ol = FXCollections.observableList(new LinkedList<Cancion>());
		listaReproduccion = new SimpleListProperty<Cancion>(ol);
		listaReproduccion.addListener(interfaz);
		rutasCanciones = new Properties();
		cant = 0;
	}
	
	//-------------------------------------------
	//Metodos
	//-------------------------------------------
	
	/**
	 * Agrega una cancion a la carpeta de canciones de la aplicacion
	 * @param arch el archivo de la cancion
	 * @throws IOException
	 * @throws SAXException 
	 * @throws TikaException 
	 */
	public void agregarCancion(File arch) throws IOException, SAXException, TikaException
	{
		try
		{
			extraerCanciones(arch);	
			rutasCanciones.setProperty(arch.getName(), arch.getCanonicalPath());
		}
		catch (FileNotFoundException e) 
		{
			throw new FileNotFoundException("No se agregó la cancion: (FileNotFound) \n"+e.getMessage());
		}
		catch(IOException e)
		{
			throw new IOException("No se agregó la cancion: (IO) \n"+e.getMessage());
		}
		catch (SAXException e)
		{
			throw new SAXException("No se agregó la cancion: (SAX) \n"+e.getMessage());
		} 
		catch (TikaException e)
		{
			throw new TikaException("No se agregó la cancion: (Tika) \n"+e.getMessage());
		} 
		
	}

	/**
	 * Elimina una cancion que entra por parametro
	 * @param c
	 * @throws IOException
	 */
	public void eliminarCancion(Cancion c) throws IOException
	{
		
		File arch = new File("./data/canciones/"+c.darArchivo());
		try
		{
			Files.delete(arch.toPath());
			cant--;
		}
		catch(IOException e)
		{
			throw new IOException("No se eliminó la canción: \n"+e.getMessage());
		}
		
		Comparator<Cancion> comparador = new ComparadorCanciones();
		cancionesXnombre.eliminar(c.darNombre(), c, comparador);
		cancionesXartista.eliminar(c.darArtista(), c, comparador);
		cancionesXid.eliminar(c.toString(), c, comparador);
	}
	
	/**
	 * Devuelve la lista de reproduccion
	 * @return Cancion[] canciones en la lista
	 */
	public SimpleListProperty<Cancion> darLista()
	{
		return listaReproduccion;
	}

	/**
	 * Devuelve un arreglo de canciones que tienen el nombre
	 * que entra por parametro
	 * @param pNombre el nombre de la cancion
	 * @return canciones[] arreglo de canciones
	 */
	public Cancion[] darCancionesPorNombre(String pNombre)
	{
		ComparadorBusquedas comparador = new ComparadorBusquedas();
		IListaOrdenada lista = cancionesXnombre.buscarLista(pNombre, comparador);
		
		if(lista != null)
		{
			Cancion[] canciones = new Cancion[lista.darLongitud()];
			
			for(int i =0; i<canciones.length; i++)
			{
				Cancion c = (Cancion)lista.dar(i);
				canciones[i] = c;
			}
			
			return canciones;
		}
		return null;
	}

	/**
	 * Devuelve una lista ordenada canciones por nombre
	 * @param pNombre
	 * @return IListaOrdenada
	 */
	synchronized public IListaOrdenada buscarCancionesPorNombre(String pNombre)
	{
		ComparadorBusquedas comparador = new ComparadorBusquedas();
		IListaOrdenada resultado = cancionesXnombre.buscarLista(pNombre, comparador);
		if(resultado == null)
			resultado = new ListaOrdenada();
		buscarCancionesEnApi(resultado, pNombre, -1);
		return resultado;
	}
	
	/**
	 * Devuelve una lista ordenada de canciones por artista
	 * @param pArtista el artista de la cancion
	 * @return IListaOrdenada
	 */
	synchronized public IListaOrdenada buscarCancionesPorArtista(String pArtista)
	{
		ComparadorBusquedas comparador = new ComparadorBusquedas();
		return cancionesXartista.buscarLista(pArtista, comparador);
	}
	
	/**
	 * Devuelve una cancion por id
	 * @param pId la identificacion de la cancion
	 * @return Cancion con el id que entra por parametro
	 */
	synchronized public Cancion buscarCancionesPorId(String pId)
	{
		ComparadorId comparador = new ComparadorId();
		Cancion c = cancionesXid.buscar(pId, comparador);
		if(c == null)
		{
			String n = pId.split(":")[3];
			System.out.println("n es: "+n);
			c = buscarCancionesEnApi(null, "", Long.parseLong(n));
		}
		return c;
	}

	/**
	 * Agrega canciones de un usuario a la lista de reproduccion grande
	 * notifica a la interfaz para mostrar un mensaje de que han 
	 * sido añadidas las canciones
	 * @param canciones
	 * @param infoUsuario informacion del usuario
	 */
	synchronized public void agregarLista(Stack<Cancion> canciones, String infoUsuario)
	{
		for(int i = 0; i<canciones.size();i++)
		{
			Cancion c = (Cancion)canciones.get(i);
		}
		while(!canciones.empty())
		{
			try
			{
				listaReproduccion.add(canciones.pop());
			}
			catch(UnsupportedOperationException e)
			{
				System.out.println("Erro agregar cancion: "+e.toString());
			}
		}
		if(mediaPlayer == null && playing)
			reproducir();
		else if(mediaPlayer != null && mediaPlayer.getStatus().equals(Status.DISPOSED) && playing)
			reproducir();
		cantidadConexionesPositivas++;
	}

	/**
	 * Devuelve la cantidad de veces que un usuario ha
	 * agregado exitosamente canciones a la lista de reproduccion
	 * @return int
	 */
	public int darConexionesPositivas()
	{
		return cantidadConexionesPositivas;
	}
	
	/**
	 * Da la cantidad de canciones;
	 */
	public int darCantidadCanciones()
	{
		return cant;
	}
	
	public void pausar()
	{
		playing = false;
		if(mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING)
			mediaPlayer.pause();
	}

	public void siguiente(boolean salir)
	{
		if(listaReproduccion.size()>0)
			listaReproduccion.remove(0);
		if(mediaPlayer != null)
		{
			Status status = mediaPlayer.getStatus();
			mediaPlayer.stop();
			mediaPlayer.dispose();
			if(status.equals(Status.PLAYING) && !salir)
			{
				reproducir();
			}
		}
	}
	
	/**
	 * Reproduece la siguiente cancion en la cola
	 */
	public synchronized void reproducir()
	{
		playing = true;
		if(mediaPlayer != null && mediaPlayer.getStatus().equals(Status.PAUSED))
		{
			mediaPlayer.play();
		}
		else
		{
			if(listaReproduccion.size() > 0)
			{
				String path = sacarPathCancion();
				System.out.println("Path = "+path);
				media = new Media(path);
				mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setOnEndOfMedia(new Runnable() {
					@Override
					public void run() {
						setOnMedia();
					}
				});
				mediaPlayer.play();
			}
		}
	}
	
	public void guardar() throws FileNotFoundException,  IOException, URISyntaxException
	{
		//URL url = Main.class.getResourceAsStream("res/canciones.properties");
		//rutasCanciones.store(new FileOutputStream(new File(url.toURI())), null);
		System.out.println("da al01: "+Main.class.getResource("res/canciones.properties").getFile());
		//System.out.println("jar:file:");

		File f = new File(Main.class.getResource("res/canciones.properties").getFile());
		System.out.println("guardando: "+f.exists()+" - "+f.getAbsolutePath());
		//rutasCanciones.store(new FileOutputStream(Main.class.getResource("res/canciones.properties").getFile()), null);
	}

	/**
	 * Carga todas las canciones desde la carpeta ./data/canciones
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public void cargar() throws IOException, SAXException, TikaException
	{
		try
		{
			rutasCanciones.load(Main.class.getResourceAsStream("res/canciones.properties"));
			capacidad = (int)(rutasCanciones.size()*.8);
			capacidad = (capacidad >5) ? capacidad:5;
			cancionesXartista = new TablaHashing<String, Cancion>((int)(capacidad*.5));
			cancionesXnombre = new TablaHashing<String, Cancion>(capacidad);
			cancionesXid = new TablaHashing<String, Cancion>(capacidad);
			
			Iterator iter = rutasCanciones.keySet().iterator();
			while(iter.hasNext())
			{
				String key = (String)iter.next();
				
				File archivo = new File(rutasCanciones.getProperty(key));
				
				extraerCanciones(archivo);
			}
		}
		catch (FileNotFoundException e) 
		{
			throw new FileNotFoundException("No se cargaron correctamente las canciones: \n"+e.getMessage());
		}
		catch(IOException e)
		{
			throw new IOException("No se cargaron correctamente las canciones: \n"+e.getMessage());
		}
		catch (SAXException e)
		{
			throw new SAXException("No se cargaron correctamente las canciones: \n"+e.getMessage());
		} 
		catch (TikaException e)
		{
			throw new TikaException("No se cargaron correctamente las canciones: \n"+e.getMessage());
		} 
	}
	
	//--------------------------------
	//	Metodos Privados
	//--------------------------------
	
	private String sacarPathCancion()
	{
		String path = "file://";
		Cancion c = listaReproduccion.get(0);
		System.out.println("Reproduciendo: "+c);
		String encoded = "";

		if(!c.darArchivo().startsWith("http"))
		{
			String[] sec = c.darArchivo().split("/");

			path += sec[0];
			for(int i = 1; i<sec.length; i++)
			{
				try {
					encoded = URLEncoder.encode(sec[i], "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				path += "/"+encoded;
			}
			path = path.replace("+", "%20");
		}
		else
		{
			path = c.darArchivo();
		}
		return path;
	}
	
	private void extraerCanciones(File archivo) throws IOException, SAXException, TikaException
	{
		if(archivo.isDirectory())
		{	
			File[] canciones = archivo.listFiles();
			
			for(int i = 0; i<canciones.length; i++)
			{
				String extension = "";
				int j = canciones[i].getPath().lastIndexOf('.');
				if (j > 0) 
				{
				    extension = canciones[i].getPath().substring(j+1);
				}
				
				if (canciones[i].isFile() && (extension.equals("mp3") || extension.equals("wav"))) 
				{	
					cargarCanciones(canciones[i], extension);
				}
			}
		}
		else
		{
			String extension = "";
			int j = archivo.getPath().lastIndexOf('.');
			if (j > 0) 
			{
			    extension = archivo.getPath().substring(j+1);
			}
			
			if (archivo.isFile() && (extension.equals("mp3") || extension.equals("wav"))) 
			{	
				cargarCanciones(archivo, extension);
			}
		}
	}

	private Cancion buscarCancionesEnApi(IListaOrdenada resultado, String pNombre, long id)
	{
		ApiWrapper wrapper = new ApiWrapper(client_id, client_secret, null, null);
		try {
			Request r;
			if(id > -1)
				r= new Request("/tracks/"+id);
			else
				r= new Request("/tracks?q="+pNombre);
			System.out.println("Buscando en sOUNDclOUD");
			HttpResponse resp = wrapper.get(r);
			if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				String s = Http.formatJSON(Http.getString(resp));
				try {
					if(id > -1 )
					{
						JSONObject json = (JSONObject)new JSONParser().parse(s);
						System.out.println(json);
						Cancion c = new Cancion((String)json.get("title"), (String)json.get("stream_url"));
						c.setId((long)json.get("id"));
						return c;
					}
					else
					{
						JSONArray canciones = (JSONArray)new JSONParser().parse(s);
						for(int i = 0; i<canciones.size(); i++)
						{
							JSONObject json = (JSONObject) canciones.get(i);
							System.out.println(json);
							Cancion c = new Cancion((String)json.get("title"), (String)json.get("stream_url"));
							c.setId((long)json.get("id"));
							resultado.agregar(c);
						}
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void setOnMedia()
	{
		System.out.println("------------EndOfMedia----------");
		mediaPlayer.dispose();
		System.out.println("Dispose Status: "+mediaPlayer.getStatus());
		if(listaReproduccion.size() > 0)
		{
			listaReproduccion.remove(0);
		}
		if(listaReproduccion.size() > 0)
		{
			String path = sacarPathCancion();
			media = new Media(path);
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setOnEndOfMedia(new Runnable() {
				@Override
				public void run() {
					setOnMedia();
				}
			});
			mediaPlayer.play();
		}
	}
	
	private void cargarCanciones(File archivo, String extension) throws IOException, SAXException, TikaException
	{
		cant++;

		Parser parser = null;
		if(extension.equals("wav"))
			parser = new AudioParser();
		else if(extension.equals("mp3"))
			parser = new Mp3Parser();
		InputStream input = new FileInputStream(new File(archivo.toString()));
		ContentHandler handler = new DefaultHandler();
		Metadata metadata = new Metadata();
		ParseContext parseCtx = new ParseContext();
		parser.parse(input, handler, metadata, parseCtx);
		input.close();

		System.out.println("------------------------------------------");
		System.out.println(archivo.getCanonicalPath());

		System.out.println("Title: " + metadata.get("title"));
		System.out.println("Artists: " + metadata.get("xmpDM:artist"));
		System.out.println("Album: " + metadata.get("xmpDM:album"));
		if(metadata.get("title") != null  && !metadata.get("title").equals("") && metadata.get("xmpDM:artist") != null && !metadata.get("xmpDM:artist").equals("") && metadata.get("xmpDM:album") != null && !metadata.get("xmpDM:album").equals("") )
		{
			Cancion c = new Cancion(metadata.get("title"), metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), archivo.getCanonicalPath());
			cancionesXnombre.agregar(c.darNombre(), c);
			cancionesXartista.agregar(c.darArtista(), c);
			cancionesXid.agregar(c.toString(), c);
		}
		else if(metadata.get("title") != null  && !metadata.get("title").equals("") && metadata.get("xmpDM:artist") != null && !metadata.get("xmpDM:artist").equals(""))
		{
			Cancion c = new Cancion(metadata.get("title"), metadata.get("xmpDM:artist"), archivo.getCanonicalPath());
			cancionesXnombre.agregar(c.darNombre(), c);
			cancionesXartista.agregar(c.darArtista(), c);
			cancionesXid.agregar(c.toString(), c);

		}
		else if(metadata.get("title") != null && !metadata.get("title").equals(""))
		{
			Cancion c = new Cancion(metadata.get("title"), archivo.getCanonicalPath());
			cancionesXnombre.agregar(c.darNombre(), c);
			cancionesXartista.agregar(c.darArtista(), c);
			cancionesXid.agregar(c.toString(), c);
		}
		else
		{
			String[] sp = archivo.getName().split("\\.");

			if(sp.length==2)
			{
				Cancion c = new Cancion(sp[0], archivo.getCanonicalPath());
				cancionesXnombre.agregar(c.darNombre(), c);
				cancionesXartista.agregar(c.darArtista(), c);
				cancionesXid.agregar(c.toString(), c);
				System.out.println("Nombre: "+c.darNombre());
			}
			
		}
	}
}
