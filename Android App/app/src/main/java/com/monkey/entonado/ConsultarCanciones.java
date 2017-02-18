package com.monkey.entonado;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConsultarCanciones extends Thread
{

	//-------------------------
	//Atributos
	//-------------------------		
	private MainActivity actividad;
	private String mensaje;

	//-------------------------
	//Constructor
	//-------------------------
		
	public ConsultarCanciones(MainActivity pAct)
	{
		actividad = pAct;
		mensaje = "";
	}
		
	//--------------------------
	//Metodos
	//--------------------------
		
	public void run()
	{
		actividad.consultarCanciones();
	}

}
