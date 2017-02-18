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
