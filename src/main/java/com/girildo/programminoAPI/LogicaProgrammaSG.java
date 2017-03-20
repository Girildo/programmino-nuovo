package com.girildo.programminoAPI;

import java.util.ArrayList;
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
	
	public LogicaProgrammaSG()
	{
		super();
	}
	
	HashMap<Integer, Foto> dictionaryFoto;
	List<Autore> listaAutoVoto, listaAutoriCheHannoVotato;
	
	@Override
	public Messaggio GeneraClassifica(int numPreferenze)
	{
		dictionaryFoto = new HashMap<Integer, Foto>();
		listaAutoVoto = new ArrayList<Autore>();
		listaAutoriCheHannoVotato = new ArrayList<Autore>();
		ArrayList<Commento> commentiConErrori = new ArrayList<Commento>(); //lista che contiene i commenti con meno preferenze dell'imp.
		for(Commento c:listaCommenti)
		{
			if(c.getTipo() == Commento.TipoCommento.STARTVOTING || c.getTipo() == Commento.TipoCommento.IGNORA)
				continue;
			if(c.getTipo() == Commento.TipoCommento.VOTAZIONE)
			{
				String[] split = c.getTesto().split("#", -1);
				if(split.length<numPreferenze+1) //Se ho meno di quante preferenze mi aspetto c'è un problema
				{
					commentiConErrori.add(c);
					return new Messaggio("Il voto di " + c.getAutore().getNome() + " sembra avere un problema col "+
					"numero delle preferenze", FlagMessaggio.ERRORE);
				}
				ArrayList<Foto> fotoVotateInQuestoCommento = new ArrayList<Foto>();
				for(int i=0; i<numPreferenze; i++) //itera tra gli id ottenuti con lo split 
				{
					int id = 0;
					Foto foto = null;
					try
					{
						id = Integer.parseInt(split[i+1]); //cast a int della stringa
						foto = dictionaryFoto.get(id); //ottiene la foto dall'hash set
						if(!fotoVotateInQuestoCommento.contains(foto))
						{
							foto.aumentaVoti(numPreferenze-i); //aumenta i voti della foto
							fotoVotateInQuestoCommento.add(foto);
						}
						else
						{
							commentiConErrori.add(c);
							return new Messaggio("Il commento di " + c.getAutore().getNome() + " contiene un doppio voto",
									FlagMessaggio.ERRORE);
						}
						
						if(!listaAutoriCheHannoVotato.contains(c.getAutore()))
							listaAutoriCheHannoVotato.add(c.getAutore());
						
						if(foto.getAutore().equals(c.getAutore())) //se l'autore della foto è lo stesso del commenot
						{
							listaAutoVoto.add(c.getAutore()); //c'è autovoto
						}
					}
					catch(Exception e)
					{
						System.out.println(id);
					}
				}
			}
			else //se non è votazione (=È foto); qui si genera l'hash set;
			{
				Foto foto = new Foto(c);
				dictionaryFoto.put(foto.getID(), foto);
			}
		}
		List<Foto> listaOrdinataPerClassifica = new ArrayList<Foto>(dictionaryFoto.values());
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
			if(listaAutoVoto.contains(f.getAutore()))
			{
				builderAutoVoto.append("\u2022 " + f.getAutore().getNomeAbbreviato()+"\n");
				flagXMessaggio = FlagMessaggio.ERRORE_PARZIALE;
			}
			builderClassifica.append(f.toString() + "\n");
		}
		builderNonVotanti.append("-----------------------\n");
		builderAutoVoto.append("-----------------------\n");
		builderClassifica.append("-----------------------\n");
		builderClassifica.append("Foto trovate: "+dictionaryFoto.size()+"\n");
		builderClassifica.append("Hanno votato in "+listaAutoriCheHannoVotato.size()+"\n");
		builderClassifica.append("Si sono autovotati in "+listaAutoVoto.size()+"\n");
		builderClassifica.append("Voti con errore: "+commentiConErrori.size()+"\n");
		
		if(listaAutoVoto.size() == 0)
			builderAutoVoto.delete(0, builderAutoVoto.length());
		
		
		if(Collections.max(listaOrdinataPerClassifica).getVoti() == 0)
			return new Messaggio("Sembra che tutte le foto abbiano 0 voti; controlla che il numero delle"
					+ " preferenze di voto sia giusto!", FlagMessaggio.ERRORE);
		
		return new Messaggio(builderClassifica.toString(), flagXMessaggio
				, builderNonVotanti.toString()+builderAutoVoto.toString());
	}
	@Override
	protected ArrayList<Commento> pulisciCommenti(ArrayList<Commento> listaCommentiSporchi)
	{
		//System.out.println(listaCommentiSporchi.size());
		super.listaCommenti = new ArrayList<Commento>();
		for(Commento c : listaCommentiSporchi)
		{
			TipoCommento tipoNuovo = c.getTipo();
			String testoNuovo;
			String testo = c.getTesto();
			Autore autore = c.getAutore();
			if(!testo.contains("#") && testo.contains("<a href")) //se il commento contiene un link ad una foto ma niente cancelletto
			{													  //spara una exception catched dal chiamante
				throw new IllegalArgumentException("Il commento di "+autore.getNome()+" dovrebbe contenere una foto ma non trovo il cancelletto" );
			}
			if(!testo.contains("#") && !testo.contains("<a href")) //se il commento non contiene né foto né cancelletto è da ignorare
			{
				tipoNuovo = TipoCommento.IGNORA;
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
					tipoNuovo = TipoCommento.STARTVOTING;
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
			testoNuovo = builder.toString().replaceAll(" ", "");
			Commento cd = new Commento(testoNuovo, autore);
			cd.setTipo(tipoNuovo);
			cd.AggiornaTipo(TipoLogica.LOGICA_SG);
			System.out.println(cd.getTipo());
			listaCommenti.add(cd);
		}
		//return listaCommenti.size() == listaCommentiSporchi.size() && Commento.Voting;
	}
}
