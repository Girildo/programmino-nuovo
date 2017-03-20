package com.girildo.programminoAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.girildo.programminoAPI.Messaggio.FlagMessaggio;
import com.girildo.programminoAPI.LogicaProgramma;
import com.girildo.programminoAPI.Commento.TipoCommento;



public class LogicaProgrammaSG extends LogicaProgramma
{
	@Override
	public Messaggio generaClassifica(ArrayList<Commento> commentiPuliti, int numPreferenze) throws Exception
	{
		HashMap<Integer, Foto> dictionaryFoto = Foto.generaHashMapFotoDaCommenti(commentiPuliti);
		Classifica classificaGenerale = new Classifica("Classifica Generale", dictionaryFoto.values());

		ArrayList<Autore> listaAutoriCheHannoVotato = new ArrayList<Autore>();
		ArrayList<Autore> listaAutoriConAutovoto = new ArrayList<Autore>();		
		ArrayList<Commento> commentiConErrori = new ArrayList<Commento>(); //lista che contiene i commenti con meno preferenze dell'imp.
		for(Commento c:commentiPuliti)
		{
			if(c.getTipo() == Commento.TipoCommento.STARTVOTING || c.getTipo() == Commento.TipoCommento.IGNORA)
				continue;
			if(c.getTipo() == Commento.TipoCommento.VOTAZIONE)
			{
				ArrayList<Foto> fotoVotateInQuestoCommento = new ArrayList<Foto>();
				String[] split = c.getTesto().split("#", -1);
				if(split.length<numPreferenze+1) //Se ho meno di quante preferenze mi aspetto c'è un problema
				{
					commentiConErrori.add(c);
					throw new Exception("Il voto di " + c.getAutore().getNome() + " sembra avere un problema col "+
							"numero delle preferenze");
				}
				Foto currentPhoto;
				for(int i=0; i < numPreferenze; i++) //itera tra gli ID foto ottenuti con lo split 
				{
					int id = 0;
					id = Integer.parseInt(split[i+1]); //cast a int della stringa contenente l'ID
					currentPhoto = dictionaryFoto.get(id);
					if(!fotoVotateInQuestoCommento.contains(currentPhoto))
					{
						classificaGenerale.votaFoto(id, numPreferenze-i);
						fotoVotateInQuestoCommento.add(currentPhoto);
					}
					else
					{
						commentiConErrori.add(c);
						return new Messaggio("Il commento di " + c.getAutore().getNome() + " contiene un doppio voto",
								FlagMessaggio.ERRORE);
					}

					if(!listaAutoriCheHannoVotato.contains(c.getAutore()))
						listaAutoriCheHannoVotato.add(c.getAutore());

					if(currentPhoto.getAutore().equals(c.getAutore())) //se l'autore della foto è lo stesso del commenot
					{
						listaAutoriConAutovoto.add(c.getAutore()); //c'è autovoto
					}
				}
			}
		}
		Collection<Foto> listaOrdinataPerClassifica = classificaGenerale.ordinaClassifica();
		StringBuilder builderClassifica = new StringBuilder();
		StringBuilder builderNonVotanti = new StringBuilder("Non hanno votato: \n");
		StringBuilder builderAutoVoto = new StringBuilder("Si sono autovotati: \n");

		FlagMessaggio flagMessaggio = FlagMessaggio.NESSUN_ERRORE;

		for(Foto f:listaOrdinataPerClassifica)
		{
			if(!listaAutoriCheHannoVotato.contains(f.getAutore()))
			{
				builderNonVotanti.append("\u2022 " + f.getAutore().getNomeAbbreviato()+
						" (#"+f.getID()+" con "+f.getVoti()+" punti)\n");
				flagMessaggio = FlagMessaggio.ERRORE_PARZIALE;
			}
			if(listaAutoriConAutovoto.contains(f.getAutore()))
			{
				builderAutoVoto.append("\u2022 " + f.getAutore().getNomeAbbreviato()+"\n");
				flagMessaggio = FlagMessaggio.ERRORE_PARZIALE;
			}
			builderClassifica.append(f.toString() + "\n");
		}
		builderNonVotanti.append("-----------------------\n");
		builderAutoVoto.append("-----------------------\n");
		builderClassifica.append("-----------------------\n");
		builderClassifica.append("Foto trovate: "+dictionaryFoto.size()+"\n");
		builderClassifica.append("Hanno votato in "+listaAutoriCheHannoVotato.size()+"\n");
		builderClassifica.append("Si sono autovotati in "+listaAutoriConAutovoto.size()+"\n");
		builderClassifica.append("Voti con errore: "+commentiConErrori.size()+"\n");

		if(listaAutoriConAutovoto.size() == 0)
			builderAutoVoto.delete(0, builderAutoVoto.length());


		if(Collections.max(listaOrdinataPerClassifica).getVoti() == 0)
			throw new Exception("Sembra che tutte le foto abbiano 0 voti; controlla che il numero delle"
					+ " preferenze di voto sia giusto!");

		return new Messaggio(builderClassifica.toString(), flagMessaggio
				, builderNonVotanti.toString()+builderAutoVoto.toString());
	}


	@Override
	public ArrayList<Commento> pulisciCommenti(ArrayList<Commento> commentiDaPulire) throws Exception
	{
		boolean ignoring = false; //tutti i commenti a partire da questo punto vengono ignorati
		ArrayList<Commento> commentiRipuliti = new ArrayList<>();
		for(Commento c : commentiDaPulire)
		{
			String testo = c.getTesto();

			if(testo.contains("Stop Voting"))
				ignoring = true;
			if(!testo.contains("#") && testo.contains("<a href")) //se il commento contiene un link ad una foto ma niente cancelletto
			{													  //spara una exception catched dal chiamante
				throw new Exception("Il commento di "+c.getAutore().getNome()
						+ " dovrebbe contenere una foto ma non trovo il cancelletto" );
			}
			if((!testo.contains("#") && !testo.contains("<a href")) || ignoring)
			{
				c.setTipo(TipoCommento.IGNORA);
			}
			testo = testo.replaceAll("<a.+\\/><\\/a>", "");
			String[] split = testo.split("\n");
			StringBuilder builder = new StringBuilder(); 
			Pattern pattern = Pattern.compile("(# ?\\d{1,2})+");
			for(String s : split)
			{
				s=s.trim();
				if(s.isEmpty())
					continue;

				if(s.matches("#{6,100}"))
				{
					Commento.Voting = true;
					c.setTipo(TipoCommento.STARTVOTING);
					break;
				}

				if(s.matches("(# ?\\d{1,2})+"))
					builder.append(s);
				else if(s.matches("(# ?\\d{1,2}) ?STOP *"))
					builder.append(s.replace("STOP", "").trim());
				else
				{
					Matcher matcher = pattern.matcher(s);
					if(matcher.find())
						builder.append(matcher.group());
				}
			}
			c.setTesto(builder.toString().replaceAll(" ", ""));
			c.AggiornaTipo(TipoLogica.LOGICA_SG);
			commentiRipuliti.add(c);
			System.out.println(c.getTipo());
		}
		if(!(commentiDaPulire.size() == commentiRipuliti.size() && Commento.Voting))
			throw new Exception("C'� qualcosa che non va nei commenti");
		//return listaCommenti.size() == listaCommentiSporchi.size() && Commento.Voting;
		return commentiRipuliti;
	}
}
