package krasa.core.backend.service;

import krasa.core.backend.dao.*;
import krasa.core.backend.domain.GlobalSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Vojtech Krasa
 */
@Service
public class GlobalSettingsProvider {

	private GenericDAO<GlobalSettings> globalSettingsDAO;
	private GenericDaoBuilder genericDAO;
	protected static GlobalSettings first;

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		this.genericDAO = genericDAO;
		this.globalSettingsDAO = genericDAO.build(GlobalSettings.class);
	}

	public GlobalSettings getGlobalSettings() {
		if (first == null) {
			first = globalSettingsDAO.findFirst();
			createGlobalSettings();
		}
		return globalSettingsDAO.findFirst();
	}

	private synchronized void createGlobalSettings() {
		if (first == null) {
			first = new GlobalSettings();
			globalSettingsDAO.save(first);
		}
	}
}
