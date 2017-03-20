package com.girildo.programminoAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Classifica {
	private String titolo;
	private HashMap<Integer, Foto> dizionarioFoto;
	

	public Classifica(String titolo, Collection<Foto> sourceFoto) {
		this.dizionarioFoto = new HashMap<>();
		for (Foto f : sourceFoto) {
			Foto clonedFoto = f.clonaFoto();
			dizionarioFoto.put(clonedFoto.getID(), clonedFoto.clonaFoto());
		}
		this.titolo = titolo;
	}

	public void votaFoto(int id, int punti) {
		this.dizionarioFoto.get(id).aumentaVoti(punti);
	}

	public List<Foto> ordinaClassifica() {
		List<Foto> listaOrdinataPerClassifica = new ArrayList<Foto>(this.dizionarioFoto.values());
		Collections.sort(listaOrdinataPerClassifica); // ordina la lista (dal
														// più basso al più
														// alto)
		Collections.reverse(listaOrdinataPerClassifica); // inverte l'ordine
		return listaOrdinataPerClassifica;
	}
	
	

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public int getVoti(int id) {
		return this.dizionarioFoto.get(id).getVoti();
	}
}
