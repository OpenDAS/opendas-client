package org.opendas.ctrl;

//Arithmetic
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedList;

import org.opendas.modele.DASGeneric;

import gi.*;

public class DASExpression extends SLR1_Grammar
{
	public static Boolean getvalue(HashMap<String, Object> context, String arguments)
	{
		try
		{
			// HashMap<String, Object> context = new HashMap<String, Object>();
			// context.put("perso", new String("ddzfzef"));
			// LinkedList<String> groups = new LinkedList<String>();
			// groups.add("un");
			// groups.add("deux");
			// groups.add("trois");
			// context.put("groups", groups );
			// context.put("poste", new String("ddzzz"));
			DASExpression test = new DASExpression(context);
			ParseTree dd = (ParseTree) test.interpret(new ByteArrayInputStream(arguments.getBytes()));
			return (Boolean) dd.value;
		} catch (Exception e)
		{
			e.printStackTrace();

		}
		return new Boolean(false);
	}

	public DASExpression(final HashMap<String, Object> context) throws Exception
	{
		//System.out.println("Passage par DASExpression");
		Semantics and = new Semantics() {
			public void f(ParseTree t, int l)
			{
				//System.out.println("and");
				//System.out.println("and :"+t.child[l-1].value+" "+t.child[l-3].value);				
				t.value = (Boolean) t.child[l - 1].value & (Boolean) t.child[l - 3].value;
			}
		};
		Semantics or = new Semantics() {
			public void f(ParseTree t, int l)
			{
				//System.out.println("or");
				//System.out.println("or :"+t.child[l-1].value+" "+t.child[l-3].value);
				t.value = (Boolean) t.child[l - 1].value | (Boolean) t.child[l - 3].value;
			}
		};
		Semantics xor = new Semantics() {
			public void f(ParseTree t, int l)
			{
				//System.out.println("xor");
				//System.out.println("xor :"+t.child[l-1].value+" "+t.child[l-3].value);
				t.value = (Boolean) t.child[l - 1].value ^ (Boolean) t.child[l - 3].value;
			}
		};
		Semantics calc = new Semantics() {
			public void f(ParseTree t, int l)
			{
				//System.out.println("calc");
				String op = (String) t.child[l - 3].value;
				Object val1 = t.child[l - 4].value;
				Object val2 = t.child[l - 2].value;
				// System.out.println(val1+"  "+val2+"  "+op);
				Boolean result = new Boolean(false);
				if (val2 == null)
				{
					if (op.equals("="))
					{
						if (val1 == null)
						{
							result = true;
						}
					} else if (op.equals("!="))
					{
						if (val1 != null)
						{
							result = true;
						}
					} else if (op.equals("in"))
					{
						if (val1 == null)
						{
							result = true;
						}
					} else if (op.equals("!in"))
					{
						if (val1 != null)
						{
							result = true;
						}
					}
				} else
				{
					Class class1 = val1.getClass();
					Class class2 = val2.getClass();
					if (op.equals("="))
					{
						if (class1 == class2)
						{
							if (val1.equals(val2))
							{
								result = true;
							}
						} else
						{
							if (val1.toString().equals(val2.toString()))
							{
								result = true;
							}
						}
					} else if (op.equals("!="))
					{
						if (class1 == class2)
						{
							if (!val1.equals(val2))
							{
								result = true;
							} else
							{
								if (!val1.toString().equals(val2.toString()))
								{
									result = true;
								}
							}
						}
					} else if (op.equals("in"))
					{
						result = false;
						if (class1 == String.class && class2 == LinkedList.class)
						{
							if (((LinkedList) val2).contains((String) val1))
							{
								result = true;
							}
						}
						if (class1 == String.class && class2 == String.class)
						{
							if (((String) val2).charAt(0) == '[' && ((String) val2).charAt(((String) val2).length() - 1) == ']')
							{
								val2 = ((String) val2).split(",");
							}
							if (((LinkedList) val2).contains((String) val1))
							{
								result = true;
							}
						}
					} else if (op.equals("!in"))
					{
						result = true;
						if (class1 == String.class && class2 == LinkedList.class)
						{
							if (((LinkedList) val2).size() > 0)
							{
								if (((LinkedList) val2).getFirst().getClass() == String.class)
								{
									if (!((LinkedList) val2).contains((String) val1))
									{
										result = true;
									}
								} else if (((LinkedList) val2).getFirst().getClass() == DASGeneric.class)
								{
									for (int i = 0; i < ((LinkedList) val2).size(); ++i)
									{
										//System.out.println("val1 :" + val1 + " val2:"+ val2);
										if (((DASGeneric) (((LinkedList) val2).get(i))).getCode().equals(((String) val1)))
										{
											result = false;
										}
									}
								}

							} else
							{
								result = false;
							}

						}
						if (class1 == String.class && class2 == String.class)
						{
							if (((String) val2).charAt(0) == '[' && ((String) val2).charAt(((String) val2).length() - 1) == ']')
							{
								val2 = ((String) val2).split(",");
							}
							if (!((LinkedList) val2).contains((String) val1))
							{
								result = true;
							}
						}
					}
				}
				t.value = result;
			}
		};
		Semantics id = new Semantics() {
			public void f(ParseTree t, int l)
			{
				//System.out.println("id");
				t.value = t.child[l - 1].value;
			}
		};
		Semantics transform = new Semantics() {
			public void f(ParseTree t, int l)
			{
				//System.out.println("transform");
				if (context.containsKey((String) t.child[l - 1].value))
				{
					t.value = context.get((String) t.child[l - 1].value);
				} else if (context.containsKey((String) t.child[l - 1].value))
				{
					t.value = context.get((String) t.child[l - 1].value);
				}
			}
		};
		Semantics liste = new Semantics() {
			public void f(ParseTree t, int l)
			{
				LinkedList<String> result = new LinkedList<String>();
				if (t.child[l - 1].value != null)
				{
					if (t.child[l - 1].value.getClass() == LinkedList.class)
						result.addAll((LinkedList<String>) t.child[l - 1].value);
					else
						result.add((String) t.child[l - 1].value);
					result.add((String) t.child[l - 3].value);
				}
				t.value = result;
			}
		};
		
		Semantics result = new Semantics() {
			public void f(ParseTree t,int l)
			{
				t.value = t.child[l - 1].value;
			}
		};

		put("LITERAL", expression("[[:alnum:]/b[:alnum:]]+|_[[:alnum:]]+"));
		put("COMMA", expression(","));
		put("OP", expression("=|in|!=|!in"));
		put("SPACE", expression("[[:space:]]+"));
		put("S", new Object[][]{{"E1", result}});
		put("E1", new Object[][]{{"E2", id}, {"E1", "&", "E1", and},});
		put("E2", new Object[][]{{"E3", id}, {"E2", "|", "E1", or},});
		put("E3", new Object[][]{{"R", id}, {"E3", "&&", "R", xor},});
		put("R", new Object[][]{{"LITERAL", transform}, {"'", "LITERAL", id, "'"}, {"[", "LISTE", id, "]"}, {"(", "E1", id, ")"}, {"(", "E1", "OP", "E1", ")", calc},});
		put("LISTE", new Object[][]{{"'","LITERAL", id, "'"},{"'", "LITERAL", "'", "LISTE", liste},});
		// debug = PARSE_TREE;
	}
}
