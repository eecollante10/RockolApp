package co.anarquianegra.rockolappServidor.mundo;

import java.util.Comparator;

import TablaHashing.NodoTablaHashing;

public class ComparadorCanciones implements Comparator
{

	@Override
	public int compare(Object o1, Object o2)
	{
		NodoTablaHashing nodo = (NodoTablaHashing)o1;
		Cancion c1 = (Cancion)nodo.darValor();
		Cancion c2 = (Cancion)o2;
		return c1.toString().compareTo(c2.toString());
	}

}
