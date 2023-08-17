package santander.cloud.sap.service.impl;

import com.sap.cloud.security.spring.config.XsuaaServiceConfiguration;
import com.sap.cloud.security.spring.token.SpringSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Simple DataLayer interface that shows how Spring global message security
 * can be used to control access to data objects on a method level.
 */
@Service
public class DataService {
    @Autowired
    XsuaaServiceConfiguration xsuaaConfig;

    /**
     * Reads sensitive data from the data layer.
     * User requires scope {@code Admin}
     * for this to succeed.
     *
     */
    public String readSensitiveData() {
        String zoneId = SpringSecurityContext.getToken().getZoneId();
        return "You got the sensitive data for zone '" + zoneId + "'.";
    }
}
