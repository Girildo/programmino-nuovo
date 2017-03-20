package com.girildo.programminoAPI;

public class Messaggio
{
	public enum FlagMessaggio{NESSUN_ERRORE, ERRORE_PARZIALE, ERRORE};
	
	private FlagMessaggio flag;
	private String testoNessunErrore;
	private String testoErroreParziale;

	
	
	public Messaggio (String testo, FlagMessaggio flag)
	{
		this(testo, flag, "");
	}
	public Messaggio (String testo, FlagMessaggio flag, String testoErroreParziale)
	{
		this.setTestoNessunErrore(testo);
		this.setFlag(flag);
		this.setTestoErroreParziale(testoErroreParziale);
	}

	public FlagMessaggio getFlag()
	{
		return flag;
	}
	public void setFlag(FlagMessaggio flag)
	{
		this.flag = flag;
	}
	public String getTestoErroreParziale()
	{
		return testoErroreParziale;
	}
	

	public String getTestoNessunErrore()
	{
		return testoNessunErrore;
	}

	private void setTestoNessunErrore(String testo)
	{
		this.testoNessunErrore = testo;
	}
	private void setTestoErroreParziale(String testoErroreParziale)
	{
		this.testoErroreParziale = testoErroreParziale;
	}
	
}
