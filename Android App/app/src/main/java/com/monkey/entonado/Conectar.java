package com.monkey.entonado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

public class Conectar extends Thread
{
	
	//-------------------------
	//Atributos
	//-------------------------
	private MainActivity actividad;
	private String mensaje;

	//-------------------------
	//Constructor
	//-------------------------
	
	public Conectar(MainActivity pAct)
	{
		actividad = pAct;
		mensaje = null;
	}
	
	//--------------------------
	//Metodos
	//--------------------------
	
	public void run()
	{
		
		mensaje = actividad.conectar();

	}
	
	public String darMensaje()
	{
		return mensaje;
	}
}
