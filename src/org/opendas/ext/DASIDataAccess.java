package org.opendas.ext;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;

/**
 * Interface permettant de standardiser les échanges du DAS avec son
 * environnement
 * 
 * @author mlaroche
 */
public interface DASIDataAccess
{

	/**
	 * Method to call to send an object to Back-office consumers (through AMQ)
	 * 
	 * @param title
	 *            request name
	 * @param attachement
	 *            object to send
	 */
	public void insertToErp(String title, Serializable attachement);

	/**
	 * Method to call to send an object to DAS Server (through AMQ)
	 * 
	 * @param title
	 *            request name
	 * @param attachement
	 *            object to send
	 */
	public void insertToServer(String title, Serializable attachement);

	/**
	 * Method to call to get an object from Back-office consumers (through AMQ)
	 * 
	 * @param title
	 *            request name
	 * @param attachement
	 *            mandatory object for request
	 * @throws TimeoutException
	 */
	public Serializable getFromErp(String title, Serializable attachement) throws TimeoutException;

	/**
	 * Method to call to get an object from DAS Server (through AMQ)
	 * 
	 * @param title
	 *            request name
	 * @param attachement
	 *            mandatory object for request
	 * @throws TimeoutException
	 */
	public Serializable getFromServer(String title, Serializable attachement) throws TimeoutException;
}
