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
