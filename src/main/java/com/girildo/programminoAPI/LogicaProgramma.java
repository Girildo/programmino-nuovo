package com.girildo.programminoAPI;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.groups.discuss.GroupDiscussInterface;
import com.flickr4java.flickr.groups.discuss.Reply;
import com.flickr4java.flickr.groups.discuss.ReplyObject;
import com.girildo.programminoAPI.Messaggio.FlagMessaggio;

public abstract class LogicaProgramma 
{
	protected final static String KEY = "00b9cc2a3bf5e2896905d1fd621a20eb";
	protected final static String SECRET = "a5be5f21bd03edc2";
	protected ArrayList<Commento> listaCommenti;
	public enum TipoLogica {LOGICA_SG, LOGICA_CM, LOGICA_CMS}
	
	public LogicaProgramma()
	{
		Commento.resetVotingFlag();
	}
	
	public Messaggio OttieniCommentiPulitiDaUrl(String url)
	{
		Pattern pattern = Pattern.compile(".+\\/discuss\\/");
		Matcher matcher = pattern.matcher(url);
		String idSporco = matcher.replaceAll("");
		String topicID = idSporco.endsWith("/")?idSporco.substring(0, idSporco.length()-1):idSporco;
		ArrayList<Commento> listaCommentiSporchi = new ArrayList<Commento>();
		Flickr flickr = new Flickr(KEY, SECRET, new REST());
        GroupDiscussInterface dInterface = flickr.getDiscussionInterface();
        ArrayList <Reply> repList = null;
        try
        {
        	int count = dInterface.getTopicInfo(topicID).getCountReplies(); //count delle risposte
        	ReplyObject rep = dInterface.getReplyList(topicID, count, 1); //ottiene l'oggetto dall'API
        	repList = rep.getReplyList(); //estrae la lista delle risposte
        }
        catch(FlickrException ex)
		{
        	if(ex.getErrorCode().equalsIgnoreCase("1"))
				return new Messaggio("Non ho trovato un topic di Flickr valido a quel link."
						+ "\nAssicurati di aver copiato l'URL per intero", FlagMessaggio.ERRORE);
			return new Messaggio("C'è un problema con Flickr...", FlagMessaggio.ERRORE);
		}
        catch(Exception ex2)
        {
        	return new Messaggio(ex2.getLocalizedMessage(), FlagMessaggio.ERRORE);
        }
        if (repList == null)
        	return new Messaggio("Errore sconosciuto (RepList null)", FlagMessaggio.ERRORE);
        
        
        listaCommentiSporchi = new ArrayList<Commento>(); //lista di commenti con classe custom
        for(Reply reply : repList) //itera oggetto API
        {
        	Pattern p = Pattern.compile("<img class.+alt=\"Classifica\" />");
        	if(p.matcher(reply.getMessage()).find())
        	{
        		break;
        	}
        	p = Pattern.compile("<img class.+alt=\"THE WINNER\" />");
        	if(p.matcher(reply.getMessage()).find())
        	{
        		break;
        	}
        	Commento commento = new Commento(reply.getMessage(), new Autore(reply.getAuthorname(), reply.getAuthorId()));
        	listaCommentiSporchi.add(commento);
        }
        boolean success = pulisciCommenti(listaCommentiSporchi);
        String message = buildMessageFoto(); //crea il messaggio con le foto trovate
        
        if (success)
        	return new Messaggio(message, FlagMessaggio.NESSUN_ERRORE);
        return new Messaggio("Errore Sconosciuto (!success)", FlagMessaggio.ERRORE);
        
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
	protected abstract boolean pulisciCommenti(ArrayList<Commento> commentiSporchi);
	
}
