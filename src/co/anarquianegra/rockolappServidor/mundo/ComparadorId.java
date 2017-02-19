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
package co.anarquianegra.rockolappServidor.mundo;

import java.util.Comparator;

import TablaHashing.NodoTablaHashing;

public class ComparadorId implements Comparator<String>
{

	@Override
	public int compare(String c1, String c2)
	{
		return c1.compareTo(c2);
	}
}
