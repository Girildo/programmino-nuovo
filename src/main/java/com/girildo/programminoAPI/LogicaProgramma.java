package com.girildo.programminoAPI;

import java.util.ArrayList;
import com.girildo.programminoAPI.Messaggio.FlagMessaggio;

public abstract class LogicaProgramma 
{
	public enum TipoLogica {LOGICA_SG, LOGICA_CM, LOGICA_CMS}
	
	public LogicaProgramma()
	{
		Commento.resetVotingFlag();
	}
	
	protected String buildMessageFoto()
	{
		StringBuilder builder = new StringBuilder();
	        for(Commento c:listaCommenti)
	        {
	        	if(c.getTipo() == Commento.TipoCommento.FOTO)
	        		builder.append(c.toString()+'\n');
	        }
	        return builder.toString();
	}
	
	public abstract Messaggio GeneraClassifica(int numPreferenze);
	protected abstract ArrayList<Commento> pulisciCommenti(ArrayList<Commento> commentiDaPulire);
	
}
