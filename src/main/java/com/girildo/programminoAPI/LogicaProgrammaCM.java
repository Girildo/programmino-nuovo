package com.girildo.programminoAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.girildo.programminoAPI.Commento.TipoCommento;
import com.girildo.programminoAPI.Messaggio.FlagMessaggio;



public class LogicaProgrammaCM extends LogicaProgramma
{
	@Override
	public Messaggio generaClassifica(ArrayList<Commento> commentiPuliti, int numPreferenze) throws Exception
	{
		HashMap<Integer, Foto> dictionaryFoto = Foto.generaHashMapFotoDaCommenti(commentiPuliti);
		Classifica classificaGenerale = new Classifica("Classifica Generale", dictionaryFoto.values());
		Classifica classificaTecn = new Classifica("Classifica Tecnica", dictionaryFoto.values());
		Classifica classificaEspr = new Classifica("Classifica Espressivit‡", dictionaryFoto.values());
		Classifica classificaOrig = new Classifica("Classifica Originalit‡", dictionaryFoto.values());

		ArrayList<Autore> listaAutoriCheHannoVotato = new ArrayList<Autore>();
		ArrayList<Autore> listaAutoriConAutovoto = new ArrayList<Autore>();
		ArrayList<Commento> commentiConErrore = new ArrayList<Commento>();

		for(Commento c:commentiPuliti)
		{
			ArrayList<Foto> votateNelCommentoTecn = new ArrayList<Foto>();
			ArrayList<Foto> votateNelCommentoEspr = new ArrayList<Foto>();
			ArrayList<Foto> votateNelCommentoOrig = new ArrayList<Foto>();

			if(c.getTipo() == TipoCommento.IGNORA)
				continue;
			else if(c.getTipo() == TipoCommento.VOTAZIONE)
			{
				String[] split = c.getTesto().split("!"); //Spezza il commento nei capturing groups
				if(split.length < 3*numPreferenze)
				{
					throw new Exception("La votazione di "+ c.getAutore().getNome() +
							" sembra avere meno di "+ 3*numPreferenze +" voti");
				}

				for(String s:split) //Itera sui capturing groups
				{
					s = s.trim();
					if(!s.matches("[teoTEO] ?:? ?# ?\\d{1,2} ?"))
						throw new Exception("La votazione di " + c.getAutore().getNome() +
								" sembra avere un errore di formato");
					else
					{
						String testo = s.replace(":", "").replace(" ", ""); //pulisce la stringa iterata da : e spazi
						char tipoVoto = Character.toUpperCase(testo.charAt(0)); //Salva per reference il primo carattere
						testo = testo.substring(2);	//rimuove il primo carattere e il cancelletto
						int parseID = Integer.parseInt(testo); //estrae l'ID della foto incluso nel capturing group
						Foto fotoVotata = dictionaryFoto.get(parseID);

						if(fotoVotata == null)
							throw new Exception(c.getAutore().getNome()+" ha votato una foto che non esiste");

						if(fotoVotata.getAutore().equals(c.getAutore())) //se l'autore della foto √® lo stesso del commenot
						{
							listaAutoriConAutovoto.add(c.getAutore()); //c'√® autovoto
						}

						if(!listaAutoriCheHannoVotato.contains(c.getAutore()))
							listaAutoriCheHannoVotato.add(c.getAutore());

						classificaGenerale.votaFoto(parseID, 1);
						switch (tipoVoto) //Definisce la classifica da recuperare (T; E; O)
						{
						case 'T':
							if(votateNelCommentoTecn.contains(fotoVotata))
								throw new Exception(c.getAutore().getNome() + " ha votato due volte la stessa"
										+ " foto nella categoria 'tecnica'");
							classificaTecn.votaFoto(parseID, 1);
							votateNelCommentoTecn.add(fotoVotata);
							break;
						case 'E':
							if(votateNelCommentoEspr.contains(fotoVotata))
								throw new Exception(c.getAutore().getNome() + " ha votato due volte la stessa"
										+ " foto nella categoria 'espressivit‡'");
							classificaEspr.votaFoto(parseID, 1);
							votateNelCommentoEspr.add(fotoVotata);
							break;
						case 'O':
							if(votateNelCommentoOrig.contains(fotoVotata))
								throw new Exception(c.getAutore().getNome() + " ha votato due volte la stessa"
										+ " foto nella categoria 'originalit‡'");
							classificaOrig.votaFoto(parseID, 1);
							votateNelCommentoOrig.add(fotoVotata);
							break;
						}
					}
				}
			}
		}
		//List<Foto> listaOrdinataPerClassifica = new ArrayList<Foto>(classificaGenerale.values());
		//Collections.sort(listaOrdinataPerClassifica); //ordina la lista (dal pi√π basso al pi√π alto)
		//Collections.reverse(listaOrdinataPerClassifica); //inverte l'ordine
		Collection<Foto> listaOrdinataPerClassifica = classificaGenerale.ordinaClassifica();
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
			builderClassifica.append(" (T:" + classificaTecn.getVoti(f.getID()));
			builderClassifica.append("|E:" + classificaEspr.getVoti(f.getID()));
			builderClassifica.append("|O:" + classificaOrig.getVoti(f.getID()));
			builderClassifica.append(")\n");
		}
		builderNonVotanti.append("-----------------------\n");
		builderAutoVoto.append("-----------------------\n");
		builderClassifica.append("-----------------------\n");
		builderClassifica.append("Foto trovate: "+dictionaryFoto.size()+"\n");
		builderClassifica.append("Hanno votato in: "+listaAutoriCheHannoVotato.size()+"\n");
		builderClassifica.append("Si sono autovotati in: "+listaAutoriConAutovoto.size()+"\n");
		builderClassifica.append("Voti con errore: "+commentiConErrore.size()+"\n");

		if(listaAutoriConAutovoto.size() == 0)
			builderAutoVoto.delete(0, builderAutoVoto.length());


		return new Messaggio(builderClassifica.toString(), flagXMessaggio
				, builderNonVotanti.toString()+builderAutoVoto.toString());
	}

	@Override
	public ArrayList<Commento> pulisciCommenti(ArrayList<Commento> commentiDaPulire) throws Exception
	{
		ArrayList<Commento> commentiRipuliti = new ArrayList<>();
		for(Commento c : commentiDaPulire)
		{
			String testo = c.getTesto();
			if(!testo.contains("#") && testo.contains("<a href")) //se il commento contiene un link ad una foto ma niente cancelletto
			{													  //spara una exception catched dal chiamante
				throw new Exception("Il commento di "+c.getAutore().getNome()
						+ " dovrebbe contenere una foto ma non trovo il cancelletto" );
			}
			if(!testo.contains("#") && !testo.contains("<a href"))
				c.setTipo(TipoCommento.IGNORA);
			if(testo.contains("Risultato Finale"))
				c.setTipo(TipoCommento.IGNORA);
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
			commentiRipuliti.add(c); //aggiorna la lista
		}
		if(commentiRipuliti.size() != commentiDaPulire.size())
			throw new Exception("C'Ë qualcosa che non va nei commenti");
		return commentiRipuliti;
	}
}
