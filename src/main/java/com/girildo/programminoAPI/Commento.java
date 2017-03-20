package com.girildo.programminoAPI;

import com.girildo.programminoAPI.LogicaProgramma.TipoLogica;

public class Commento
{
	public static boolean Voting;
	public enum TipoCommento{FOTO, VOTAZIONE, STARTVOTING, IGNORA}
	private String testo;
	private Autore autore;
	private TipoCommento tipo;
	
	public Commento(String testo, Autore autore)
	{
		this.autore = autore;
		this.testo = testo;
	}
	
	public void AggiornaTipo(TipoLogica logica)
	{
		if(tipo == TipoCommento.IGNORA)
			return;
		if(logica == TipoLogica.LOGICA_SG)
		{
			if(this.tipo != TipoCommento.STARTVOTING)
				this.tipo = Voting?TipoCommento.VOTAZIONE:TipoCommento.FOTO;
		}
		if(logica == TipoLogica.LOGICA_CM)
		{
			if(this.tipo != TipoCommento.STARTVOTING)
				this.tipo = Voting?TipoCommento.VOTAZIONE:TipoCommento.FOTO;
		}
	}
	
	public static void resetVotingFlag()
	{
		Commento.Voting = false;
	}
	
	public TipoCommento getTipo()
	{
		return tipo;
	}

	public void setTipo(TipoCommento tipo)
	{
		this.tipo = tipo;
	}
	
	public Autore getAutore()
	{
		return autore;
	}
	public void setAutore(Autore autore)
	{
		this.autore = autore;
	}
	public String getTesto()
	{
		return testo;
	}
	public void setTesto(String testo)
	{
		this.testo = testo;
	}

	
	
	@Override
	public String toString()
	{
		return this.getTesto() + " di " + this.getAutore().getNome();
	}
}
