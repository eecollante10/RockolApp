package co.anarquianegra.rockolappServidor.mundo;

import java.util.Comparator;

public class ComparadorBusquedas implements Comparator<String>
{

	@Override
	public int compare(String o1, String o2) 
	{	
		 if(o1.trim().toLowerCase().contains(o2.trim().toLowerCase()))
			 return 0;
		 else
			 return 1;
	}
	
}