package org.opendas.equipment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opendas.DASLoader;
import org.opendas.DASLog;
import org.opendas.modele.DASConfigMaterial;
import org.opendas.modele.DASConfigTypeMaterial;
import org.opendas.modele.DASDialog;
import org.opendas.modele.DASTypeTransmitProtocol;

/**
 * Classe including relatives objects at equipements (balances, scanners...)
 */
public class DASEquipments
{

	public DASLoader loader	= DASLoader.getInstance();
	public DASEquipments()
	{		
	}

	/*public List<DASACPrinter> getPrinters()
	{
		return printersList;
	}*/
	
	public List<DASDialog> recupSupervisorDialog()
	{
		List<DASDialog> supervisors = new ArrayList<DASDialog>();
		List<DASConfigMaterial> temps = loader.getConfig().getSupervisor();
		for (DASConfigMaterial i : temps)
		{
			DASConfigMaterial material = i;
			DASConfigTypeMaterial configMaterial = material.getConfigTypeMaterial();
			if (configMaterial != null)
			{
				supervisors = configMaterial.getDialog();
				break;
			}
		}
		return supervisors;
	}

	public List<DASBaseMaterial> recupMaterials()
	{
		logDebug("Pass by recupMaterials");
		
		List<DASConfigMaterial> temps = this.loader.getConfig().getMaterials();
		List<DASBaseMaterial> materials = new ArrayList<DASBaseMaterial>();
		
		if(temps != null){
			for (DASConfigMaterial i : temps){
				DASConfigMaterial material = i;
				DASConfigTypeMaterial configTypeMaterial = material.getConfigTypeMaterial();
	
				if (configTypeMaterial != null)
				{	
					logDebug("*****"+ material.getCode() + "*****");
					//Material creation in function of type port material
					try{
						//Check if configurable material
						if(configTypeMaterial.isSimple().equals("true")){
							//Simple so none type transmit protocol
							configTypeMaterial.setType_transmit_protocols(new ArrayList<DASTypeTransmitProtocol>());
						}
						//Instanciation of material according to port type
						if(configTypeMaterial.getPort_type().equals("rj45")){
							DASRJ45Material rjmaterial = new DASRJ45Material(material);
							materials.add(rjmaterial);
						}else if(configTypeMaterial.getPort_type().equals("com") || configTypeMaterial.getPort_type().equals("usb")){
							DASCOMMaterial 	commaterial = new DASCOMMaterial(material);
							materials.add(commaterial);
						}
					}catch(NullPointerException ne){
						logErr("Error during materials initialization");
					}
				}
			}
		}
		return materials;
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}

	private void logErr(String log)
	{
		DASLog.logErr(getClass().getSimpleName(), log);
	}
}