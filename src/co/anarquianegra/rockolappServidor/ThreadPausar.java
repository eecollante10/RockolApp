package co.anarquianegra.rockolappServidor;

import co.anarquianegra.rockolappServidor.mundo.ListaReproductor;

/**
 * Hilo que se usa al pausar la reproduccio
 * @author EdgardEduardo
 *
 */
public class ThreadPausar extends Thread
{
	//--------------------------
	//Atributos
	//--------------------------
	
	/**
	 * El mundo
	 */
	private ListaReproductor mundo;
	
	//--------------------------
	//Constructor
	//--------------------------
	
	/**
	 * Crea el hilo
	 * @param pMundo ListaReproductor
	 */
	public ThreadPausar(ListaReproductor pMundo)
	{
		mundo = pMundo;
	}
	
	//-------------------------
	//Metodos
	//-------------------------
	
	/**
	 * Run proviene de la clase Thread
	 */
	public void run()
	{
		//mundo.pausar();
	}
}
