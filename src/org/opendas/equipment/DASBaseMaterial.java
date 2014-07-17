package org.opendas.equipment;

import java.util.HashMap;
import java.util.Map;

import org.opendas.modele.DASConfigTypeMaterial;


public class DASBaseMaterial
{
	private int									id;
	private String								code;
	private DASConfigTypeMaterial 				type;
	private String								portName;
	private Map<String, String>					propertyMap		= new HashMap<String, String>();
	private boolean								stopAsking		= false;
	public static 	Map<String,String> mat_inputs = new HashMap<String, String>();
	//dataReceived : data received from material by send or receive
	Map<String,String> dataReceived = new HashMap<String,String>();
	
	public DASBaseMaterial(String code, DASConfigTypeMaterial type, String portName)
	{
		super();
		this.code = code;
		this.type = type;
		this.portName = portName;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getCode()
	{
		return this.code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public void setType(DASConfigTypeMaterial type)
	{
		this.type = type;
	}

	public DASConfigTypeMaterial getType()
	{
		return this.type;
	}
	
	// recupere une propriete
	public String getProperty(String key)
	{
		return propertyMap.get(key);
	}

	// ajout nouvelle propriete scanner
	public void addProperty(String key, String value)
	{
		propertyMap.put(key, value);
	}

	public String getPortName()
	{
		return portName;
	}

	public void setPortName(String portName)
	{
		this.portName = portName;
	}

	protected String getPortname()
	{
		return this.portName;
	}

	public boolean getstopAsking()
	{
		return this.stopAsking;
	}

	public void setStopAsking(boolean ask)
	{
		this.stopAsking = ask;
	}

	public Map<String, String> getPropertyMap()
	{
		return propertyMap;
	}

	public void setPropertyMap(Map<String, String> propertyMap)
	{
		this.propertyMap = propertyMap;
	}

	public Map<String, String> getDataReceived()
	{
		return dataReceived;
	}
	
	public void setDataReceived(Map<String, String> dataReceived)
	{
		this.dataReceived = dataReceived;
	}

	public String toString()
	{
		return "DASBaseMaterial [type=" + type.toString() + "]";
	}
	
	
}
