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
