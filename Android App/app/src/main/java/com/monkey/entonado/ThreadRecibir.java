package com.monkey.entonado;

public class ThreadRecibir extends Thread
{
		//-------------------------
			//Atributos
			//-------------------------		
			private MainActivity actividad;
			private String mensaje;

			//-------------------------
			//Constructor
			//-------------------------
				
			public ThreadRecibir(MainActivity pAct)
			{
				actividad = pAct;
				mensaje = null;
			}
				
			//--------------------------
			//Metodos
			//--------------------------
				
			public void run()
			{
				mensaje = actividad.recibir();
			}
				
			public String darMensaje()
			{
				return mensaje;
			}

}
