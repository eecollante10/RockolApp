package com.monkey.entonado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener, OnMenuItemClickListener
{
	//---------------------------
	//Constantes
	//---------------------------
	
	public final static String INFO_USUARIO = "informacion usuario";
	
	public final static String BUSCAR_POR_NOMBRE = "buscar por nombre";
	
	public final static String BUSCAR_POR_ARTISTA = "buscar por artista";
	
	public final static String AGREGAR_CANCION = "agregar cancion";
	
	public final static String AGREGAR_LISTA = "agregar lista";
	
	public final static String EXTRA_CANCIONES = "com.example.entonado.MESSAGE";
	
	public final static String EXTRA_LISTA = "com.example.entonado.LISTA";
	
	public final static int RESULT_BOTON =1994;
	
	public final static String TAG = "Entonado";
	
	
	//---------------------------
	//Atributos
	//---------------------------
	/**
	 * Boton buscar
	 */
	private TextView btnBuscar;
	
	/**
	 * Campo busqueda
	 */
	private EditText campoBusqueda;
	
	/**
	 * Radio boton artista
	 */
	private Button radioArtista;
	
	/**
	 * Radio boton titulo
	 */
	private Button radioTitulo;
	
	/**
	 * Texto Que indica el metodo de busaueda
	 */
	private TextView busqueda;
	
	/**
	 * Socket de este cliente
	 */
	private Socket canal;
	
	/**
	 * Canal para escribir hacia el servidor
	 */
	private PrintWriter out;
	
	/**
	 * Canal para recibir del servidor
	 */
	private BufferedReader in;
	
	/**
	 * Arreglo de canciones resultantes de una busqueda
	 */
	private ArrayList canciones;
	
	
	/**
	 * Wifi p2p manager
	 */
	private WifiP2pManager mManager;
	
	/**
	 * Canal para conexion a hardware wifip2p
	 */
	private Channel mChannel;
	
	/**
	 * Broadcast reciever, recibe informacion de el estado del wifi
	 */
	private WiFiDirectBroadCastReceiver mReceiver;
	
	/**
	 * Filtro de mensajes del broadcast receiver
	 */
	private IntentFilter mIntentFilter;
	
	/**
	 * Mensaje de exito de coneccion
	 */
	private String mensajeConexion;
	
	/**
	 * Indica si se ha intentado conectar
	 */
	private boolean intentoConectar;
	
	/**
	 * Indica si se ha intentado enviar la lista
	 */
	private boolean intentoEnviarLista;
	
	/**
	 * Direccion ip del servidor
	 */
	private String ip;
	
	/**
	 * Canciones añadidas a la lista de reproduccion
	 */
	private ArrayList milista;
	
	
	//---------------------------------
	//Metodos De la Actividad
	//---------------------------------
	/**
	 * Inicializa la actividad
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	    mReceiver = new WiFiDirectBroadCastReceiver(mManager, mChannel, this);
	    
	    mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
		btnBuscar = (TextView)findViewById(R.id.btnBuscar);
		btnBuscar.setOnClickListener(this);
		
		radioTitulo = (Button)findViewById(R.id.btnTitulo);
		radioArtista = (Button)findViewById(R.id.btnArtista);
		
		campoBusqueda = (EditText)findViewById(R.id.campoBusqueda);
		
		busqueda = (TextView)findViewById(R.id.txtBusqueda);
				
			out = null;
			in = null;
			
			canciones = new ArrayList();
			milista = new ArrayList();
			
			mensajeConexion = "No se estableció la conexion";
			intentoConectar = false;
			ip = "";

	}
	
	/**
	 * OnResume
	 * register the broadcast receiver with the intent values to be matched 
	 */
	@Override
	protected void onResume()
	{
	    super.onResume();
	    registerReceiver(mReceiver, mIntentFilter);
	    CountDownTimer t = new CountDownTimer(1000, 500) {

	        public void onTick(long millisUntilFinished)
	        {

	        }

	        public void onFinish() {
	        	if(intentoConectar)
	    	    {
	    	    	mostrarMensaje(mensajeConexion);
	    	    	intentoConectar = false;
	    	    }
	        	else if(intentoEnviarLista)
	        	{
	        		String mensaje = "";
	        		boolean entro = false;
	        		if(mensajeConexion == null)
	        		{
	        			mensaje = "Estas desconectado";
	        				
	        		}
	        		else if(mensajeConexion.equals("desconectado"))
	        		{
	        			mensaje = "Estas desconectado";
	        				
	        		}
	        		else if(mensajeConexion.equals("¡Estas conectado!"))
	        		{
	        			mensaje = "La lista ha sido enviada";
	        			Intent in = new Intent(MainActivity.this, AlarmReciever.class );
	        			PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, in, PendingIntent.FLAG_ONE_SHOT);
	        			entro = true;
	        			System.out.println("puso alarma");
	        			
	        			AlarmManager am = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
	        			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000*60, pi);
	        		}
	        		else
	        		{
	        			mensaje = mensajeConexion;
	        		}
	        		if(!entro)
	        			mostrarMensaje(mensaje);
	        		intentoEnviarLista = false;
	        	}
	        }
	     }.start();
	}
	
	/**
	 *OnPause
	 * unregister the broadcast receiver 
	 */
	@Override
	protected void onPause() 
	{
	    super.onPause();
	    unregisterReceiver(mReceiver);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
				
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setOnMenuItemClickListener(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			Intent intent = new Intent(this, MiLista.class);
			intent.putParcelableArrayListExtra(EXTRA_LISTA, milista);
			startActivityForResult(intent, 999);
			intentoEnviarLista = false;
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Recibe el resultado de intentar escanear un codigo QR
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{           
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 0) 
	    {
	        if (resultCode == RESULT_OK) 
	        {
	            ip = data.getStringExtra("SCAN_RESULT");
	            
	            Conectar conectar = new Conectar(this);
	            conectar.start();
	            
	          
	        }
	        else if(resultCode == RESULT_CANCELED)
	        {
	            
	           if( canal == null )
	           {
	        	   mensajeConexion = "No te conectaste";
	        	   intentoConectar = true;
	           }
	        }
	    }
	    else if(requestCode == 999)
	    {
	    	if (resultCode == RESULT_OK) 
	        {
	            milista = data.getParcelableArrayListExtra("LISTA_ACTUALIZADA");   
	        }
	    	else if(resultCode == RESULT_BOTON)
	    	{
	    		milista = data.getParcelableArrayListExtra("LISTA_ACTUALIZADA");
	    		
	    		if(canal != null && canal.isConnected() && mensajeConexion != null && !mensajeConexion.equals("desconectado"))
	    		{
	    			ThreadEnviarLista enviar = new ThreadEnviarLista(this);
	    			enviar.start();
	    			
	    			while(enviar.isAlive())
	    			{
	    				
	    			}
	    			mostrarMensaje(mensajeConexion);
	    		}
	    		
	    		intentoEnviarLista = true;
	    		
	    	}
	    	else if(resultCode == RESULT_CANCELED)
	        {
	        	System.out.println("Lista actualizada : cancel");
	        }
	    }
	}

	/**
	 * Maneja los clicks a los botones
	 * escanear docdigo QR
	 * y Buscar
	 */
	@Override
	public void onClick(View v) 
	{
		canciones = new ArrayList();
		
		if(v.getId() == R.id.btnBuscar)
		{
			String mensaje = campoBusqueda.getText().toString();
			
			if(mensaje == null || mensaje.equals(""))
			{
				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	            builder1.setMessage("Debe introducir algo para buscar...");
	            builder1.setCancelable(false);
	            builder1.setPositiveButton("OK",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	            AlertDialog alert11 = builder1.create();
	            alert11.show();
	            
			}
			else if(radioTitulo.getText().equals("por Titulo") || radioArtista.getText().equals("por Artista"))
			{
				if(canal != null && canal.isConnected() && mensajeConexion != null && !mensajeConexion.equals("desconectado"))
				{
					ConsultarCanciones consultar = new ConsultarCanciones(this);
					consultar.start();
					
					ThreadRecibir recibir = new ThreadRecibir(this);
					recibir.start();
					
					while(recibir.isAlive())
					{
						
					}
					
					if(!recibir.darMensaje().equals(""))
						mostrarMensaje(recibir.darMensaje());
				}
				else
				{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		            builder1.setMessage("Todavía no estas conectado a un computador con Entonado. Debes escanear un código QR");
		            builder1.setCancelable(false);
		            builder1.setPositiveButton("OK",
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    dialog.cancel();
		                }
		            });

		            AlertDialog alert11 = builder1.create();
		            alert11.show();
				}
			}
			
		}
	}
	
	//-----------------------------
	//Metodos De Conexion
	//-----------------------------

	/**
	 * Establece el estado de la coneccion wifi
	 * @param b
	 */
	public void setIsWifiP2pEnabled(boolean b) 
	{
		String mensaje = "";
		
		if(b)
			mensaje = "Conexion WiFi: Disponible";
		else
			mensaje = "Conexion WiFI: No Encendido";
		if(!b)
		{
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	        builder1.setMessage(mensaje);
	        builder1.setCancelable(false);
	        builder1.setPositiveButton("OK",
	                new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	            }
	        });
	
	        AlertDialog alert11 = builder1.create();
	        alert11.show();
		}
	}
	
	/**
	 * Realiza la conexion al servidor
	 * @return mensaje Indica si la conexion fue exitosa
	 */
	@SuppressLint("NewApi")
	public String conectar()
	{
		intentoConectar = true;
		try 
        {
        	System.out.println("Conectado en 4 ");
        	canal = new Socket( );
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
        	System.out.println("Conectado en 3");
        	canal.bind(null);
            System.out.println("Conectado en 2");
            canal.connect((new InetSocketAddress(ip, 10052)));
            System.out.println("Conectado en 1");
            out = new PrintWriter(canal.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(canal.getInputStream()));
            
            String info ="";
            
            AccountManager aManager = AccountManager.get(this);
            Account[] accounts = aManager.getAccountsByType("com.google");
             
            for (Account account : accounts)
            {
                String accountId = account.name;
                info+=accountId+" : ";
            }
            
            out.println(INFO_USUARIO+";"+"U:"+info);
            
            String mens = in.readLine();
            
            System.out.println("Estas conectado! "+mens+" : "+canal.getInetAddress());
            mensajeConexion = "¡Estas conectado!";
            return "¡Estas Conectado!";
        }
		catch(SocketException e)
		{
			System.out.println("No se establecio la conexion");
        	mensajeConexion = "No se estableció la conexión: "+e.getMessage();
        	return "No se establecio la conexión";
		}
        catch (Exception e) 
        {
        	System.out.println("No se establecio la conexion");
        	mensajeConexion = "No se estableció la conexión: "+ e.getMessage();
        	return "No se establecio la conexión";
        }   
	}
	
	/**
	 * Manda mensaje al servidor para pedir canciones
	 * @return
	 */
	public void consultarCanciones()
	{
		if(busqueda.getText().equals("Por Titulo"))
		{
			out.println(BUSCAR_POR_NOMBRE+";"+campoBusqueda.getText());
			System.out.println(BUSCAR_POR_NOMBRE+" - pidiendo canciones");
		}
		else if(busqueda.getText().equals("Por Artista"))
		{
			out.println(BUSCAR_POR_ARTISTA+";"+campoBusqueda.getText());
			System.out.println(BUSCAR_POR_NOMBRE+" - pidiendo canciones");
		}
	}
	
	/**
	 * Recibe la informacion del servidor con las canciones
	 * o cualcquier informacion	`
	 * @return Mensaje
	 */
	public String recibir()
	{
		String mensajeRecibir = "";
		try
		{
			System.out.println("recibiendo canciones metodo recibir");
			String linea = in.readLine();
			if(linea == null)
			{
				mensajeConexion = "desconectado";
				mensajeRecibir = mensajeConexion;
			}
			System.out.println("-----"+linea+"-----");
			
			int i = 0;
			while(linea != null && !linea.equals("TERMINO"))
			{
				i++;
				System.out.println(linea);
				
				if(linea.split(";").length>1)
				{
					String[] lineaSp = linea.split(";");
					
					if(lineaSp[0].equals(AGREGAR_CANCION))
					{
						String[] infoCancion = lineaSp[1].split(" : ");
						Cancion cancion = new Cancion(infoCancion[0], infoCancion[1], infoCancion[2]);
						System.out.println("agrega cancion : "+cancion);
						canciones.add(cancion);
					}
					else
					{
						mensajeRecibir =lineaSp[1];
						System.out.println("mensaje no == AGREGAR_CANCION");
					}
				}
				
				System.out.println("No entro AGREGAR");
				linea = in.readLine();
			}
			System.out.println("linae al final es: "+linea);
			
			Intent intent = new Intent(this, Resultados.class);
			intent.putParcelableArrayListExtra(EXTRA_CANCIONES, canciones);
			intent.putParcelableArrayListExtra(EXTRA_LISTA, milista);
			startActivityForResult(intent, 999);
		}
		catch(SocketException e)
		{
			mensajeRecibir = e.getMessage();
			e.printStackTrace();
		}
		catch(IOException e)
		{
			mensajeRecibir = e.getMessage();
			e.printStackTrace();
		}
		catch(Exception e)
		{
			mensajeRecibir = e.getMessage();
			e.printStackTrace();
		}
		return mensajeRecibir;
	}
	
	/**
	 * Envia las canciones de la lista al servidor
	 */
	public void enviarCanciones()
	{
		mensajeConexion ="¡Estas conectado!";
		for(int i =0; i<milista.size(); i++)
		{
			Cancion c = (Cancion)milista.get(i);
			out.println(AGREGAR_CANCION+";"+c.toString());
		}
		if(milista.size()>0)
			out.println(AGREGAR_LISTA);
	}
	
	/**
	 * Muestra mensaje de resultado de intento de conexion cuando
	 * se resume la aplicacion
	 * o cuando se envia la lista
	 */
	private void mostrarMensaje(String mensaje)
	{
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(mensaje);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0)
	{
			if(canal == null || !canal.isConnected() || canal.isClosed() || canal.isInputShutdown() || (mensajeConexion != null && mensajeConexion.equals("desconectado")) || mensajeConexion == null)
			{
				try 
				{
	
				    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
	
				    startActivityForResult(intent, 0);
	
				} catch (Exception e) 
				{
	
				    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
				    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
				    startActivity(marketIntent);
				}
			}
			else
			{
				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		        builder1.setMessage("Ya estas conectado, no necesitas escanear");
		        builder1.setCancelable(false);
		        builder1.setPositiveButton("OK",
		                new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		            }
		        });
	
		        AlertDialog alert11 = builder1.create();
		        alert11.show();
			}
			
			return true;
		}
	
	//------------------------------------
	//Metodos Privados
	//------------------------------------
}
