package krasa.backend.dao;

import krasa.backend.domain.GlobalSettings;
import org.springframework.stereotype.Repository;

/**
 * @author Vojtech Krasa
 */
@Repository
public class GlobalSettingsDAOImpl extends AbstractDAO<GlobalSettings> implements GlobalSettingsDAO {
    public GlobalSettingsDAOImpl() {
        super(GlobalSettings.class);
    }

    @Override
    protected Class<GlobalSettings> getEntityClass() {
        return GlobalSettings.class;
    }

}
