package com.monkey.entonado;

public class ThreadEnviarLista extends Thread
{

	//-------------------------
		//Atributos
		//-------------------------		
		private MainActivity actividad;
		private String mensaje;

		//-------------------------
		//Constructor
		//-------------------------
			
		public ThreadEnviarLista(MainActivity pAct)
		{
			actividad = pAct;
			mensaje = "";
		}
			
		//--------------------------
		//Metodos
		//--------------------------
			
		public void run()
		{
			actividad.enviarCanciones();
		}
			
		public String darMensaje()
		{
			return mensaje;
		}
}
