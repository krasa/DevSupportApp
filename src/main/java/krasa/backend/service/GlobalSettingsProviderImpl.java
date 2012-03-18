package krasa.backend.service;

import krasa.backend.dao.GlobalSettingsDAO;
import krasa.backend.domain.GlobalSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Vojtech Krasa
 */
@Service
public class GlobalSettingsProviderImpl implements GlobalSettingsProvider {

    @Autowired
    private GlobalSettingsDAO globalSettingsDAO;
    protected GlobalSettings first;

    public GlobalSettings getGlobalSettings() {
        if (first == null) {
            first = globalSettingsDAO.findFirst();
            if (first == null) {
                first = new GlobalSettings();
                globalSettingsDAO.save(first);
            }
        }
        return globalSettingsDAO.findFirst();
    }
}
