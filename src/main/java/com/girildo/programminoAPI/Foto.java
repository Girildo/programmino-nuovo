package com.girildo.programminoAPI;

import java.util.HashMap;
import java.util.List;

import com.girildo.programminoAPI.Commento.TipoCommento;

public class Foto implements Comparable<Foto>
{
	private Autore autore;
	private int ID;

	public int getID()
	{
		return ID;
	}


	private int voti;


	/**
	 * Genera FotoDictionary dai commenti
	 * @param sourceCommenti La lista dei commenti ripuliti
	 * @return HashMap con le {@link #Foto} associate al loro ID.
	 */
	public static HashMap<Integer, Foto> generaHashMapFotoDaCommenti(List<Commento> sourceCommenti)
	{
		HashMap<Integer, Foto> hashMapFoto = new HashMap<>();
		for (Commento c : sourceCommenti)
		{
			if(c.getTipo() == TipoCommento.FOTO)
			{
				Foto f = new Foto(c);
				hashMapFoto.put(f.ID, f);
			}
		}
		return hashMapFoto;
	}


	public Foto(Autore autore, int ID)
	{
		this.autore = autore;
		this.ID = ID;
		this.voti = 0;
	}

	private Foto (Commento c) throws NumberFormatException
	{
		Autore autore = c.getAutore();
		int ID = Integer.parseInt(c.getTesto().replace("#", ""));
		this.autore = autore;
		this.ID = ID;
		autore.setSuaFoto(this);
	}

	@Override
	public boolean equals(Object other)
	{
		return this.ID == ((Foto)other).ID;
	}

	public int getVoti()
	{
		return voti;
	}


	public void aumentaVoti(int voti)
	{
		this.voti += voti;
	}

	public Autore getAutore()
	{
		return this.autore;
	}

	@Override
	public String toString()
	{
		return String.format("Foto #%d di %s: %d voti", this.ID, this.autore.getNomeAbbreviato(), this.voti); 
	}


	public Foto clonaFoto()
	{
		return new Foto(this.autore, this.ID);
	}

	@Override
	public int compareTo(Foto other)
	{
		return Integer.compare(this.getVoti(), other.getVoti());
	}

}
