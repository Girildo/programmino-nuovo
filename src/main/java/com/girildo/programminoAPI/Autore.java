package com.girildo.programminoAPI;

public class Autore
{
	private String nome;
	private String ID;
	private Foto suaFoto;
	
	public Autore(String nome, String ID)
	{
		this.nome = nome;
		this.ID=ID;
	}

	public String getID()
	{
		return ID;
	}
	public String getNome()
	{
		return this.nome;
	}
	
	public String getNomeAbbreviato()
	{
//		if(this.getNome().length() < 15)
//			return this.getNome();
//		else
//			return this.getNome().substring(0, 14);
		return this.getNome();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Autore)
			return this.ID.equalsIgnoreCase(((Autore)obj).ID);
			else return false;
	}

	public Foto getSuaFoto()
	{
		return suaFoto;
	}

	public void setSuaFoto(Foto suaFoto)
	{
		this.suaFoto = suaFoto;
	}

	
}
