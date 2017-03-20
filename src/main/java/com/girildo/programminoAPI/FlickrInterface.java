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

/**
 * Questa classe ha il compito di interfacciarsi con le api di Flickr
 * @author ggg94
 *
 */


public class FlickrInterface 
{
	private final static String KEY = "00b9cc2a3bf5e2896905d1fd621a20eb";
	private final static String SECRET = "a5be5f21bd03edc2";

	/**
	 * 
	 * @param url Link alla discussione di Flickr.
	 * @return Un ArrayList<Commento> con tutti i commenti (in {@link #Commento classe custom}) grezzi nella discussione.
	 * @throws FlickrException
	 * @throws Exception
	 */
	public static ArrayList<Commento> getCommentsFromDiscussion(String url) throws FlickrException, Exception
	{

		Flickr flickr = new Flickr(KEY, SECRET, new REST());
		GroupDiscussInterface dInterface = flickr.getDiscussionInterface();
		ArrayList <Reply> repList = null;
		String topicID = getTopicIDFromUrl(url);
		int count = dInterface.getTopicInfo(topicID).getCountReplies(); //count delle risposte
		ReplyObject rep = dInterface.getReplyList(topicID, count, 1); //ottiene l'oggetto dall'API
		repList = rep.getReplyList(); //estrae la lista delle risposte
		//return new Messaggio(ex2.getLocalizedMessage(), FlagMessaggio.ERRORE);


		if (repList == null)
			throw new Exception("RepList null");
		ArrayList<Commento> listaCommenti = new ArrayList<Commento>(); //lista di commenti con classe custom
		for(Reply reply : repList) //itera oggetto API
		{
			Commento commento = new Commento(reply.getMessage(), new Autore(reply.getAuthorname(), reply.getAuthorId()));
			listaCommenti.add(commento);
		}
		return listaCommenti;
	}

	private static String getTopicIDFromUrl(String url)
	{
		Pattern pattern = Pattern.compile("(?<=\\/discuss\\/)(\\d+)(?=\\/?)");
		Matcher  matcher = pattern.matcher(url);
		matcher.find();
		return matcher.group();
	}
}
