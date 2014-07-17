package org.opendas.ext;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;

import org.opendas.DASLoader;
import org.opendas.jms.MessageSender;
import org.opendas.modele.ServerRequest;
import org.opendas.modele.WsConfig;

/**
 * Class of data access object (DAO)
 * 
 * singleton, to get a reference :
 * <code>DASConnexion connexion = DASConnexion.getInstance();</code>
 * 
 */
public class DASConnexion implements DASIDataAccess
{

	/**
	 * Singleton
	 */
	private static DASConnexion	instance	= new DASConnexion();

	private DASConnexion()
	{
	}

	public static DASConnexion getInstance()
	{
		return DASConnexion.instance;
	}

	/**
	 * Return la chaine qui sert à parser la chaine de la balance
	 * 
	 * @return regular expression
	 * @throws TimeoutException
	 */
	public String recupChaineParser() throws TimeoutException
	{
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setTitle("getChaineParser");
		serverRequest.setAttachement(DASLoader.getWorkstationCode());
		MessageSender messageSender = MessageSender.getInstance();
		messageSender.setSubject(DASLoader.getSubject("Server"));
		ServerRequest response = null;
		response = messageSender.sendSync(serverRequest, "getChaineParser", DASLoader.getUniqueId());
		String chaine = (String) response.getAttachement();
		return chaine;
	}

	/**
	 * 
	 * @param workstationID
	 * @return
	 * @throws TimeoutException
	 */
	public WsConfig getWsConfig(String workstationID) throws TimeoutException
	{
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setTitle("getWsConfig_workstationID");
		serverRequest.setAttachement(workstationID);
		MessageSender messageSender = MessageSender.getInstance();
		messageSender.setSubject(DASLoader.getSubject("Server"));
		ServerRequest response = null;
		response = messageSender.sendSync(serverRequest, "getWsConfig_workstationID", DASLoader.getUniqueId());
		WsConfig config = (WsConfig) response.getAttachement();
		return config;
	}

	// @SuppressWarnings("unchecked")
	// public ArrayList<EAN128Identifier> getListEAN128() {
	//
	// ServerRequest serverRequest = new ServerRequest();
	// serverRequest.setTitle("getListEAN128");
	//
	// MessageSender messageSender = MessageSender.getInstance();
	// messageSender.setSubject(DASLoader.getSubject("Server"));
	// ServerRequest response = messageSender.sendSync(serverRequest,
	// "getListEAN128", DASLoader.getUniqueId());
	//
	// ArrayList<EAN128Identifier> listEan128 = (ArrayList)
	// response.getAttachement();
	//
	// return listEan128;
	// }
	/**
	 * Method called to send object outside of DAS
	 * 
	 * @param title
	 *            the name of request
	 * @param attachement
	 *            object to send
	 */
	public void insertToErp(String title, Serializable attachement)
	{
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setTitle(title);
		serverRequest.setAttachement(attachement);
		MessageSender messageSender = MessageSender.getInstance();
		messageSender.setSubject(DASLoader.getSubject("Ext"));
		messageSender.send(serverRequest, title, DASLoader.getUniqueId());
	}

	/**
	 * Method called to ask object at the outside of DAS
	 * 
	 * @param title
	 *            the name of request
	 * @param attachement
	 *            object to send
	 * @throws TimeoutException
	 */
	public Serializable getFromErp(String title, Serializable attachement) throws TimeoutException
	{
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setTitle(title);
		serverRequest.setAttachement(attachement);
		MessageSender messageSender = MessageSender.getInstance();
		messageSender.setSubject(DASLoader.getSubject("Ext"));
		ServerRequest response = null;
		response = messageSender.sendSync(serverRequest, title, DASLoader.getUniqueId());
		return response.getAttachement();
	}

	/**
	 * Method called to send object at server
	 * 
	 * @param title
	 *            the name of request
	 * @param attachement
	 *            object to send
	 */
	public void insertToServer(String title, Serializable attachement)
	{
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setTitle(title);
		serverRequest.setAttachement(attachement);
		MessageSender messageSender = MessageSender.getInstance();
		messageSender.setSubject(DASLoader.getSubject("Server"));
		messageSender.send(serverRequest, title, DASLoader.getUniqueId());
	}

	/**
	 * Method called to ask object at server
	 * 
	 * @param title
	 *            the name of request
	 * @param attachement
	 *           object falc specifing requests
	 * @throws TimeoutException
	 */
	public Serializable getFromServer(String title, Serializable attachement) throws TimeoutException
	{
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setTitle(title);
		serverRequest.setAttachement(attachement);
		MessageSender messageSender = MessageSender.getInstance();
		messageSender.setSubject(DASLoader.getSubject("Server"));
		ServerRequest response = null;
		response = messageSender.sendSync(serverRequest, title, DASLoader.getUniqueId());
		return response.getAttachement();
	}
}