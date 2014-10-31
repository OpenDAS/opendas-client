package org.opendas.ext;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DASFunctions extends LinkedHashMap<String, Object>
{
	public DASFunctions()
	{
		super();
	}

	public DASFunctions get_childs()
	{
		if (this.containsKey("_childs"))
		{
			return (DASFunctions) this.get("_childs");
		}
		return new DASFunctions();
	}

	public DASFunctions get_child(String str)
	{
		DASFunctions result = null;

		Set keys = this.get_childs().keySet();
		Iterator it = keys.iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			if (this.get_childs().get(key).getClass() == DASFunctions.class)
			{
				if (((DASFunctions) this.get_childs().get(key)).containsKey("_name"))
				{
					if (((DASFunctions) this.get_childs().get(key)).get("_name").equals(str))
					{
						result = (DASFunctions) this.get_childs().get(key);
					}
				}
			}
		}
		return result;
	}

	public String fctParams_get(String model, int sequence, String attribute, String other)
	{
		String result = null;
		attribute = attribute.substring(1);
		DASFunctions functions = (DASFunctions) ((DASFunctions) fctParams_get2(model)).get("_childs");
		Set keys = functions.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			{
				if (functions.get(key).getClass() == String.class)
				{
				} else if (functions.get(key).getClass() == Integer.class)
				{
				} else
				{
					if (((Integer) ((DASFunctions) functions.get(key)).get("_sequence")).equals(new Integer(sequence)))
					{
						if (((DASFunctions) ((DASFunctions) functions.get(key)).get("_childs")).containsKey(attribute))
						{
							return (String) ((DASFunctions) ((DASFunctions) ((DASFunctions) ((DASFunctions) functions.get(key)).get("_childs")).get(attribute)).get("_childs")).get("_value");
						}
					}
				}
			}
		}
		return result;
	}

	public String fctParams_get(String model, int sequence, String attribute)
	{
		String result = null;
		attribute = attribute.substring(1);
		DASFunctions functions = (DASFunctions) ((DASFunctions) fctParams_get2(model)).get("_childs");
		Set keys = functions.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			{
				if (functions.get(key).getClass() == String.class)
				{
				} else if (functions.get(key).getClass() == Integer.class)
				{
				} else
				{
					if (((Integer) ((DASFunctions) functions.get(key)).get("_sequence")).equals(new Integer(sequence)))
					{
						if (((DASFunctions) ((DASFunctions) functions.get(key)).get("_childs")).containsKey(attribute))
						{
							return (String) ((DASFunctions) ((DASFunctions) ((DASFunctions) ((DASFunctions) functions.get(key)).get("_childs")).get(attribute)).get("_childs")).get("_value");
						}
					}
				}
			}
		}
		return result;
	}

	public String fctParams_get(String model, int sequence)
	{
		String result = null;
		DASFunctions functions = (DASFunctions) ((DASFunctions) fctParams_get2(model)).get("_childs");
		if (functions != null)
		{
			if (functions.containsKey(String.valueOf(sequence)))
			{
				result = (String) ((DASFunctions) functions.get(String.valueOf(sequence))).get("_name");
			}
		}
		return result;
	}

	public DASFunctions fctParams_get2(String model, int sequence)
	{
		DASFunctions result = null;
		DASFunctions functions = (DASFunctions) ((DASFunctions) fctParams_get2(model)).get("_childs");
		result = (DASFunctions) functions.get(String.valueOf(sequence));
		return result;
	}

	public DASFunctions fctParams_get2(String model)
	{
		Set keys = this.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			if (this.get(key).getClass() == String.class)
			{

			} else if (this.get(key).getClass() == Integer.class)
			{

			} else
			{
				if (((DASFunctions) this.get(key)).get("_name").equals(model))
				{
					return (DASFunctions) this.get(key);
				}
			}
		}
		return null;
	}

	public List<String> getFctSequenceList()
	{
		Set keys = this.keySet();
		Iterator it = keys.iterator();
		List<String> tmp_list = new LinkedList<String>();

		while (it.hasNext())
		{
			String key = (String) it.next();
			if (this.get(key).getClass() == String.class)
			{

			} else if (this.get(key).getClass() == Integer.class)
			{

			} else
			{
				tmp_list.add((String) ((DASFunctions) this.get(key)).get("_name"));
			}
		}
		return tmp_list;
	}

}
