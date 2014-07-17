package org.opendas.supervision;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.opendas.DASLog;
import org.opendas.modele.DASDialog;
import org.opendas.supervision.DASSupervisionPanel.DASSupervisor;

/**
 * Use in conjunction with TopicPublisher to test the performance of ActiveMQ
 * Topics.
 */
public class DASSupervisionTopicListener implements javax.jms.MessageListener {

	private Connection connection;
	private Session session;
	private Topic topic;
	private Topic control;
	private DASDialog dialog;
	private List<DASSupervisor> supervisorList = new LinkedList<DASSupervisor>();
	private MessageConsumer consumer;
	public void addSupervisor(DASSupervisor sup){
		supervisorList.add(sup);
	}
	
	private String topicName;

	public void run() throws JMSException {
		
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		topic = session.createTopic(this.topicName+".messages");

		//TODO DURABLE TOPIC ?
		//durableTopic = session.createDurableSubscriber(topic, "name");

		control = session.createTopic(this.topicName+".control");

		consumer = session.createConsumer(topic);
		consumer.setMessageListener(this);

		logDebug(this.topicName+" : Waiting for messages...");
	}
	
	public void stop(){
		try
		{
			consumer.close();
			consumer = null;
			session.unsubscribe(topic.getTopicName());
			session.unsubscribe(control.getTopicName());
			session.close();
			session = null;
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}

	public DASSupervisionTopicListener(Connection conn, String topicName, DASDialog dialog, DASSupervisor sup){
		this.connection = conn;
		this.topicName = topicName;
		this.dialog = dialog;
		this.supervisorList.add(sup);
	}

	public String parseWithDialog(String tmpDr){
		String dataReceived = null;

		if(dialog != null){

			logDebug("msg received => "+tmpDr);

			String waitedHeader = (String) dialog.getHeader_code();
			logDebug("Waited Header => "+waitedHeader);

			dataReceived = null;

			if(!tmpDr.contains(waitedHeader)){
				logDebug("Not Good Data Received");
			}else{
				dataReceived = tmpDr.replace(waitedHeader, "");
			}

			if(dataReceived != null){
				String tmpSRD = dialog.getSendReceiveData();
				logDebug("Parse : "+tmpSRD);
				if(tmpSRD != null){
					dataReceived = processingData(dataReceived, tmpSRD);
				}

				if (dataReceived != null)
				{
					logDebug("Received : "+dataReceived);
					return dataReceived;
				}
			}

		}else{
			dataReceived = tmpDr;
			logDebug("No Process Data : "+dataReceived);
			return dataReceived;
		}

		return null;
	}

	private String processingData(String data, String dataParser)
	{
		data = data.trim();
		Double pesee = -666.666;
		if(dataParser.contains(",")){
			String[] dPtmp = dataParser.split(",");
			for(int i=0;i<dPtmp.length;i++){
				if(dPtmp[i].contains("DASB")){
					parseData(data,dPtmp[i]);
				}else{
					pesee = parseData(data,dPtmp[i]);
				}
			}
		}else{
			pesee = parseData(data,dataParser);
		}

		if(pesee != -666.666){
			return pesee.toString();
		}
		return null;
	}

	private void setValueToVariable(String variablename, String variabledata){
		if(!supervisorList.isEmpty() && supervisorList.get(0) != null){
			supervisorList.get(0).getPanelSup().getPanelDAS().updateSupervisionFieldFromVariable(variablename, variabledata);
		}
	}

	private double parseData(String data, String dataParser){
		Pattern p;
		Matcher m;
		if(dataParser.contains("DASB")){
			Pattern patternParser = Pattern.compile("DASB\\{(.*?)\\}");
			Matcher matcherParser = patternParser.matcher(dataParser);
			List<String> paramFormatDataList = new LinkedList<String>();
			while (matcherParser.find())
			{
				paramFormatDataList.add(matcherParser.group().replace("DASB{", "").replace("}", ""));
			}

			for(String paramFormatData : paramFormatDataList){
				String dataTmp = data;
				logDebug(paramFormatData);
				String vTmp = paramFormatData.split(":")[0];
				String dPTmp = paramFormatData.split(":")[1];
				logDebug("-Variable : "+ vTmp);
				p = Pattern.compile(dPTmp);
				m = p.matcher(data);
				while (m.find()){
					if (m.group(0).contains(dPTmp.split("=")[0]))
					{
						logDebug("-Data : "+m.group(1));
						setValueToVariable(vTmp,m.group(1));
						break;
					}
				}
			}
		}else{
			String[] datasplit = data.split("/");
			for(String datasingle : datasplit){
				if(!datasingle.contains("=")){
					p = Pattern.compile(dataParser);
					m = p.matcher(datasingle);
					if (m.matches() && m.groupCount() == 1)
					{
						double pesee = Double.parseDouble(m.group(1));
						return pesee;
					}
				}
			}
		}

		return 0.0;
	}

	public void onMessage(Message message) {

		if(consumer != null){
		try
		{
			if (message instanceof TextMessage) {
				for(DASSupervisor sup : supervisorList){
					sup.setValue(parseWithDialog(((TextMessage) message).getText()));
				}
			}
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
		}
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName()+":"+this.topicName, log);
	}

	private void logErr(String log)
	{
		DASLog.logErr(getClass().getSimpleName(), log);
	}
}
