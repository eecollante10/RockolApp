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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class MiLista extends ListActivity implements OnClickListener
{

	//-----------------------------
	//Atributos
	//-----------------------------

	/**
	 * Vista de la lista de agregar cancion
	 * 		 */
	private ListView lista;

	/**
	 * Adaptador para la lista
	 */
	private ArrayAdapter adapter;

	/**
	 * Lista con canciones
	 */
	private ArrayList listaReproduccion;

	/**
	 *Esta Actividad
	 */
	private ListActivity actividad;

	/**
	 * El boton de enviar lista
	 */
	private ImageButton boton;

	//-----------------------------
	//Metodos
	//-----------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mi_lista);

		actividad = this;

		boton = (ImageButton)findViewById(R.id.imageButton1);
		boton.setOnClickListener(this);

		// Get the message from the intent
       Intent intent = getIntent();

       if(intent != null && intent.getParcelableArrayListExtra(MainActivity.EXTRA_LISTA) != null)
       	listaReproduccion = intent.getParcelableArrayListExtra(MainActivity.EXTRA_LISTA);

	     adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaReproduccion);
	     setListAdapter(adapter);
	     getListView().setOnItemClickListener(new OnItemClickListener() {

	    	    @Override
	    	    public void onItemClick(AdapterView<?> parent, View view, int position,long arg3)
	    	    {
	    	        view.setSelected(true);
	    	     // change the background color of the selected element
	                view.setBackgroundColor(Color.LTGRAY);
	                Cancion c = (Cancion)adapter.getItem(position);
	                listaReproduccion.remove(c);
	                adapter.notifyDataSetChanged();
	                getListView().invalidateViews();

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
	public void onClick(View v)
	{
		if(v.getId() == R.id.imageButton1)
		{
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("LISTA_ACTUALIZADA", listaReproduccion);
			setResult(MainActivity.RESULT_BOTON, intent);
			finish();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onOptionsItemSelected(null);
	}

}
