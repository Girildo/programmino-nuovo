package com.girildo.programminoAPI;

import java.util.ArrayList;

public abstract class LogicaProgramma 
{
	public enum TipoLogica {LOGICA_SG, LOGICA_CM, LOGICA_CMS}
	
	public LogicaProgramma()
	{
		Commento.resetVotingFlag();
	}
	public abstract Messaggio generaClassifica(ArrayList<Commento> commentiPuliti, int numPreferenze) throws Exception;
	public abstract ArrayList<Commento> pulisciCommenti(ArrayList<Commento> commentiDaPulire) throws Exception;
	
}
