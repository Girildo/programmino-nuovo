package com.girildo.programminoAPI;

import org.jdesktop.beansbinding.Converter;

import com.girildo.programminoAPI.LogicaProgramma.TipoLogica;

public class EnumBooleanConverter extends Converter<TipoLogica, Boolean> {
 
    private TipoLogica compareAgainst;
 
    public EnumBooleanConverter() {}
 
    public EnumBooleanConverter(TipoLogica compareAgainst) 
    {
        this.compareAgainst = compareAgainst;
    }
 
	@Override
	public Boolean convertForward(TipoLogica value) 
	{
		return value.equals(compareAgainst);
	}

	@Override
	public TipoLogica convertReverse(Boolean value) 
	{
		return value?compareAgainst:null;
	}
}
