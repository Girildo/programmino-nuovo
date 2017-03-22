package com.girildo.programminoAPI;

import java.util.prefs.*;
import com.girildo.programminoAPI.LogicaProgramma.TipoLogica;

public final class StartUpManager 
{
	/**The app preferences instance.*/
	private static Preferences PREFERENCES;
	
	public final class PrefsBundle
	{
		private TipoLogica tipo;
		private int voteNumber;
		
		public TipoLogica getTipo() {
			return tipo;
		}
		public int getPrefsNumber()
		{
			return voteNumber;
		}
		public PrefsBundle(TipoLogica tipo, int prefsNumber)
		{
			this.tipo = tipo;
			this.voteNumber = prefsNumber;
		}
	}
	
	public StartUpManager()
	{
		PREFERENCES = Preferences.userNodeForPackage(this.getClass());
	}
	
	public void setDefaultPrefs(PrefsBundle bundle)
	{
		PREFERENCES.put("tipo", bundle.getTipo().toString());
		PREFERENCES.putInt("voteNumber", bundle.getPrefsNumber());
	}
	
	public PrefsBundle getDefaultPrefs()
	{
		TipoLogica tipo = TipoLogica.valueOf(PREFERENCES.get("tipo", "LOGICA_SG"));
		int prefsNumber = PREFERENCES.getInt("voteNumber", 5);
		return new PrefsBundle(tipo, prefsNumber);
	}
}