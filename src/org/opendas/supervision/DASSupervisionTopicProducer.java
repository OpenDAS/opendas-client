package org.opendas.supervision;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.opendas.DASLog;

/**
 * Use in conjunction with TopicPublisher to test the performance of ActiveMQ
 * Topics.
 */
public class DASSupervisionTopicProducer implements MessageListener {

	private Connection connection;
	private MessageProducer producer;
	private Session session;
	private Topic topic;
	private Topic control;
	private String topicName;
	MessageConsumer consumer;

	public void run() throws JMSException {

		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		topic = session.createTopic(this.topicName+".messages");

		control = session.createTopic(this.topicName+".control");

		consumer = session.createConsumer(topic);
		consumer.setMessageListener(this);

		connection.start();

		producer = session.createProducer(topic);
	}

	public void stop(){
		try
		{
			producer.close();
			producer = null;
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
	
	public DASSupervisionTopicProducer(Connection conn, String topicName){
		this.connection = conn;
		this.topicName = topicName;
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}

	private void logErr(String log)
	{
		DASLog.logErr(getClass().getSimpleName(), log);
	}
	
	public void publish(String value) throws Exception 
	{
		producer.send(session.createTextMessage("TP"+value));
	}

	public void onMessage(Message arg0)
	{
		
	}

}
