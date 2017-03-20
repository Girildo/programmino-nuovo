package com.girildo.programminoAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.girildo.programminoAPI.Commento.TipoCommento;
import com.girildo.programminoAPI.Messaggio.FlagMessaggio;



public class LogicaProgrammaCM extends LogicaProgramma
{
	@Override
	public Messaggio GeneraClassifica(int numPreferenze, File file)
	{
		HashMap<Integer, Foto> dictionaryFoto = new HashMap<Integer, Foto>();
		HashMap<Integer, Foto> classificaGenerale = new HashMap<Integer, Foto>();
		HashMap<Integer, Foto> classificaTecn = new HashMap<Integer, Foto>();
		HashMap<Integer, Foto> classificaEspr = new HashMap<Integer, Foto>();
		HashMap<Integer, Foto> classificaOrig = new HashMap<Integer, Foto>();

		ArrayList<Autore> listaAutoriCheHannoVotato = new ArrayList<Autore>();
		ArrayList<Autore> listaAutoriConAutovoto = new ArrayList<Autore>();
		ArrayList<Commento> commentiConErrore = new ArrayList<Commento>();

		for(Commento c:listaCommenti)
		{
			ArrayList<Foto> votateNelCommentoTecn = new ArrayList<Foto>();
			ArrayList<Foto> votateNelCommentoEspr = new ArrayList<Foto>();
			ArrayList<Foto> votateNelCommentoOrig = new ArrayList<Foto>();

			if(c.getTipo() == TipoCommento.IGNORA)
				continue;

			else if (c.getTipo() == TipoCommento.STARTVOTING) //popola le classifiche con le foto
			{
				for(Foto f:dictionaryFoto.values())
				{
					classificaGenerale.put(f.getID(), f.clonaFoto());
					classificaTecn.put(f.getID(), f.clonaFoto());
					classificaEspr.put(f.getID(), f.clonaFoto());
					classificaOrig.put(f.getID(), f.clonaFoto());
				}
			}


			else if(c.getTipo() == TipoCommento.FOTO)
			{
				try
				{
					Foto foto = super.generaFotoDaCommento(c);
					dictionaryFoto.put(foto.getID(), foto);
				}
				catch (NumberFormatException ex)
				{
					return new Messaggio("Nel commento contenente la foto di " + c.getAutore().getNome()
							+ "ho trovato un errore nel numero", FlagMessaggio.ERRORE);
				}
			}


			else if(c.getTipo() == TipoCommento.VOTAZIONE)
			{
				String[] split = c.getTesto().split("!");
				if(split.length < 3*numPreferenze)
				{
					return new Messaggio("La votazione di "+ c.getAutore().getNome() +
							" sembra avere meno di "+ 3*numPreferenze +" voti", FlagMessaggio.ERRORE);
				}

				for(String s:split)
				{
					//System.out.println("qui");
					s = s.trim();
					if(!s.matches("[teoTEO] ?:? ?# ?\\d{1,2} ?"))
						return new Messaggio("La votazione di " + c.getAutore().getNome() +
								" sembra avere un errore di formato", FlagMessaggio.ERRORE);
					else
					{
						String testo = s.replace(":", "").replace(" ", ""); //pulisce la stringa iterata da : e spazi
						char tipoVoto = Character.toUpperCase(testo.charAt(0)); //Salva per reference il primo carattere
						testo = testo.substring(2);	//rimuove il primo carattere e il cancelletto
						int parseID = Integer.parseInt(testo);
						Foto fotoVotata = dictionaryFoto.get(parseID);

						if(fotoVotata == null)
							return new Messaggio(c.getAutore().getNome()+" ha votato una foto che non esiste"
									, FlagMessaggio.ERRORE);

						if(fotoVotata.getAutore().equals(c.getAutore())) //se l'autore della foto è lo stesso del commenot
						{
							listaAutoriConAutovoto.add(c.getAutore()); //c'è autovoto
						}

						if(!listaAutoriCheHannoVotato.contains(c.getAutore()))
							listaAutoriCheHannoVotato.add(c.getAutore());

						classificaGenerale.get(parseID).aumentaVoti(1);
						switch (tipoVoto) //Definisce la classifica da recuperare (T; E; O)
						{
							case 'T':
								if(votateNelCommentoTecn.contains(fotoVotata))
									return new Messaggio(c.getAutore().getNome() + " ha votato due volte la stessa"
											+ " foto nella categoria 'tecnica'", FlagMessaggio.ERRORE);
								classificaTecn.get(parseID).aumentaVoti(1);
								votateNelCommentoTecn.add(fotoVotata);
								break;
							case 'E':
								if(votateNelCommentoEspr.contains(fotoVotata))
									return new Messaggio(c.getAutore().getNome() + " ha votato due volte la stessa"
											+ " foto nella categoria 'espressivit�'", FlagMessaggio.ERRORE);
								classificaEspr.get(parseID).aumentaVoti(1);
								votateNelCommentoEspr.add(fotoVotata);
								break;
							case 'O':
								if(votateNelCommentoOrig.contains(fotoVotata))
									return new Messaggio(c.getAutore().getNome() + " ha votato due volte la stessa"
											+ " foto nella categoria 'originalit�'", FlagMessaggio.ERRORE);
								classificaOrig.get(parseID).aumentaVoti(1);
								votateNelCommentoOrig.add(fotoVotata);
								break;
						}
					}
				}
			}
		}
		List<Foto> listaOrdinataPerClassifica = new ArrayList<Foto>(classificaGenerale.values());
		Collections.sort(listaOrdinataPerClassifica); //ordina la lista (dal più basso al più alto)
		Collections.reverse(listaOrdinataPerClassifica); //inverte l'ordine
		StringBuilder builderClassifica = new StringBuilder();
		StringBuilder builderNonVotanti = new StringBuilder("Non hanno votato: \n");
		StringBuilder builderAutoVoto = new StringBuilder("Si sono autovotati: \n");

		FlagMessaggio flagXMessaggio = FlagMessaggio.NESSUN_ERRORE;

		for(Foto f:listaOrdinataPerClassifica)
		{
			if(!listaAutoriCheHannoVotato.contains(f.getAutore()))
			{
				builderNonVotanti.append("\u2022 " + f.getAutore().getNomeAbbreviato()+
						" (#"+f.getID()+" con "+f.getVoti()+" punti)\n");
				flagXMessaggio = FlagMessaggio.ERRORE_PARZIALE;
			}
			if(listaAutoriConAutovoto.contains(f.getAutore()))
			{
				builderAutoVoto.append("\u2022 " + f.getAutore().getNomeAbbreviato()+"\n");
				flagXMessaggio = FlagMessaggio.ERRORE_PARZIALE;
			}
			builderClassifica.append(f.toString());
			builderClassifica.append(" (T:" + classificaTecn.get(f.getID()).getVoti());
			builderClassifica.append("| E:" + classificaEspr.get(f.getID()).getVoti());
			builderClassifica.append("| O:" + classificaOrig.get(f.getID()).getVoti());
			builderClassifica.append(")\n");
		}
		builderNonVotanti.append("-----------------------\n");
		builderAutoVoto.append("-----------------------\n");
		builderClassifica.append("-----------------------\n");
		builderClassifica.append("Foto trovate: "+dictionaryFoto.size()+"\n");
		builderClassifica.append("Hanno votato in "+listaAutoriCheHannoVotato.size()+"\n");
		builderClassifica.append("Si sono autovotati in "+listaAutoriConAutovoto.size()+"\n");
		builderClassifica.append("Voti con errore: "+commentiConErrore.size()+"\n");

		if(listaAutoriConAutovoto.size() == 0)
			builderAutoVoto.delete(0, builderAutoVoto.length());


		return new Messaggio(builderClassifica.toString(), flagXMessaggio
				, builderNonVotanti.toString()+builderAutoVoto.toString());
	}

	@Override
	protected boolean pulisciCommenti(ArrayList<Commento> commentiSporchi)
	{
		super.listaCommenti = new ArrayList<Commento>();
		for(Commento c : commentiSporchi)
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

				if(s.matches("Esempio di votazione:"))
				{
					Commento.Voting = true;
					c.setTipo(TipoCommento.STARTVOTING);
				}

				if(s.matches(" *[TEOteo] ?:? ?(# ?\\d{1,2} ?) *")) //per beccare le votazioni
					builder.append(s.replace(" ", "").trim()+"!");
				else if(s.matches(" *[TEOteo] ?:? ?(# ?\\d{1,2} ?) ?STOP *")) //per beccare le votazioni con stop
					builder.append(s.replace("STOP", "").replace(" ", "").trim()+"!");
				else //per beccare le foto
				{
					Matcher matcher = pattern.matcher(s);
					if(matcher.find())
						builder.append(matcher.group());
				}
			}
			c.setTesto(builder.toString()); //setta il testo di questo commento al builder
			if(c.getTipo() != TipoCommento.IGNORA)
				c.AggiornaTipo(TipoLogica.LOGICA_CM); //aggiorna il tipo
			listaCommenti.add(c); //aggiorna la lista
			System.out.println(c.getTipo());
			if(c.getTipo() == TipoCommento.VOTAZIONE)
				System.out.println(c.getTesto());
		}
		return listaCommenti.size() == commentiSporchi.size();
	}

}
