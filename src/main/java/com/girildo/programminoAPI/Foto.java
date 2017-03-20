package com.girildo.programminoAPI;

public class Foto implements Comparable<Foto>
{
	private Autore autore;
	private int ID;
	
	public int getID()
	{
		return ID;
	}


	private int voti;
	
	
	public Foto(Autore autore, int ID)
	{
		this.autore = autore;
		this.ID = ID;
		this.voti = 0;
	}
	
	public Foto (Commento c) throws NumberFormatException
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
		//int diff = this.getVoti() - other.getVoti();
		return Integer.compare(this.getVoti(), other.getVoti());
	}
	
}
