package com.girildo.programminoAPI;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.girildo.programminoAPI.Commento.TipoCommento;

public class LogicaProgrammaCMS extends LogicaProgramma {

	@Override
	public Messaggio GeneraClassifica(int numPreferenze) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<Commento> pulisciCommenti(ArrayList<Commento> commentiDaPulire) {
		super.listaCommenti = new ArrayList<Commento>();
		for(Commento c : commentiDaPulire)
		{
			String testo = c.getTesto();
			if(!testo.contains("#") && testo.contains("<a href")) //se il commento contiene un link ad una foto ma niente cancelletto
			{													  //spara una exception catched dal chiamante
				throw new IllegalArgumentException("Il commento di "+c.getAutore().getNome()
						+ " dovrebbe contenere una foto ma non trovo il cancelletto" );
			}
			if(!testo.contains("#") && !testo.contains("<a href"))
			{
				c.setTipo(TipoCommento.IGNORA);
			}
			testo = testo.replaceAll("<a.+\\/><\\/a>", "");
			String[] split = testo.split("\n");
			StringBuilder builder = new StringBuilder();
			Pattern pattern = Pattern.compile("(# ?\\d{1,2})+");
			for(String s : split)
			{
				s = s.trim();
				if(s.isEmpty())
					continue;
				Matcher matcher = pattern.matcher(s);
				if(matcher.find())
					builder.append(matcher.group());
			}
			c.setTesto(builder.toString()); //setta il testo di questo commento al builder
			if(c.getTipo() != TipoCommento.IGNORA)
				c.AggiornaTipo(TipoLogica.LOGICA_CMS); //aggiorna il tipo
			listaCommenti.add(c); //aggiorna la lista
			System.out.println(c.getTipo());
			if(c.getTipo() == TipoCommento.VOTAZIONE)
				System.out.println(c.getTesto());
		}
	}

}
