package com.girildo.programminoAPI;

import java.util.prefs.*;
import com.girildo.programminoAPI.LogicaProgramma.TipoLogica;

public final class StartUpManager 
{
	private Preferences prefs;
	
	public final class PrefsBundle
	{
		private TipoLogica tipo;
		private int prefsNumber;
		
		public TipoLogica getTipo() {
			return tipo;
		}
		public int getPrefsNumber()
		{
			return prefsNumber;
		}
		
		public PrefsBundle(TipoLogica tipo, int prefsNumber)
		{
			this.tipo = tipo;
			this.prefsNumber = prefsNumber;
		}
	}
	
	public StartUpManager()
	{
		prefs = Preferences.userNodeForPackage(this.getClass());
	}
	
	public void setDefaultPrefs(PrefsBundle bundle)
	{
		prefs.put("tipo", bundle.getTipo().toString());
		prefs.putInt("prefsNumber", bundle.getPrefsNumber());
	}
	
	public PrefsBundle getDefaultPrefs()
	{
		TipoLogica tipo = TipoLogica.valueOf(prefs.get("tipo", "LOGICA_SG"));
		int prefsNumber = prefs.getInt("prefsNumber", 5);
		return new PrefsBundle(tipo, prefsNumber);
	}
}
