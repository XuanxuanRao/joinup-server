package cn.org.joinup.message.application.feature.service;

import java.util.Set;

public interface IFeatureDiscoveryService {
    /**
     * Discover all available feature names from request mappings
     * @return set of feature names
     */
    Set<String> discoverFeatures();
}
