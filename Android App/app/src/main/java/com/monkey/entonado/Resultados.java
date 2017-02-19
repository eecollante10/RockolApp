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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Resultados extends ListActivity
{

	//-----------------------------
	//Atributos
	//-----------------------------

	/**
	 * Vista de la lista de agregar cancion
	 */
	private ListView lista;

	/**
	 * Adaptador para la lista
	 */
	private ArrayAdapter adapter;

	/**
	 * Lista con canciones
	 */
	private ArrayList canciones;

	/**
	 * Lista de reproduccion
	 */
	private ArrayList listaReproduccion;

	/**
	 *Esta Actividad
	 */
	private ListActivity actividad;

	//-----------------------------
	//Metodos
	//-----------------------------

	/**
	 * Inicializa los componentes de la actividad
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resultados);

		canciones = new ArrayList();

		actividad = this;

		 // Get the message from the intent
        Intent intent = getIntent();

        if(intent != null && intent.getParcelableArrayListExtra(MainActivity.EXTRA_LISTA) != null)
        	listaReproduccion = intent.getParcelableArrayListExtra(MainActivity.EXTRA_LISTA);

        if(intent != null && intent.getParcelableArrayListExtra(MainActivity.EXTRA_CANCIONES) != null)
        	canciones = intent.getParcelableArrayListExtra(MainActivity.EXTRA_CANCIONES);

	     adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, canciones.toArray());
	     setListAdapter(adapter);
	     getListView().setOnItemClickListener(new OnItemClickListener() {

	    	    @Override
	    	    public void onItemClick(AdapterView<?> parent, View view, int position,long arg3)
	    	    {
	    	        view.setSelected(true);
	    	     // change the background color of the selected element
	                view.setBackgroundColor(Color.LTGRAY);
	                Cancion c = (Cancion)adapter.getItem(position);
	                if(listaReproduccion.size()<5)
	                {
	                	listaReproduccion.add(c);
	                }
	                else
	                {
	                	AlertDialog.Builder builder1 = new AlertDialog.Builder(actividad);
		                builder1.setMessage("Debes eliminar canciones de la lista si quieres agregar más. El máximo que puedes añadir son 5 canciones");
		                builder1.setCancelable(false);
		                builder1.setPositiveButton("OK",
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int id) {
		                        dialog.cancel();
		                    }
		                });

		                AlertDialog alert11 = builder1.create();
		                alert11.show();
	                }
	    	    }
	     });
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra("LISTA_ACTUALIZADA", listaReproduccion);
		setResult(MainActivity.RESULT_OK, intent);
		finish();
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onOptionsItemSelected(null);
	}


}
