package cn.org.joinup.message.application.feature.service;

import cn.org.joinup.message.interfaces.vo.FeatureVO;

import java.util.List;
import java.util.Set;

public interface IFeatureService {

    List<FeatureVO> listFeatures();

    /**
     * Check if a user can access a specific feature
     * @param featureName the name of the feature (module)
     * @param userId the user ID
     * @return true if access is allowed
     */
    boolean canAccess(String featureName, Long userId);

    /**
     * Add users to the feature whitelist
     * @param featureName feature name
     * @param userIds set of user IDs
     */
    void addUsersToWhitelist(String featureName, Set<Long> userIds);

    /**
     * Remove users from the feature whitelist
     * @param featureName feature name
     * @param userIds set of user IDs
     */
    void removeUsersFromWhitelist(String featureName, Set<Long> userIds);

    /**
     * Get the whitelist for a feature
     * @param featureName feature name
     * @return set of user IDs
     */
    Set<Long> getWhitelist(String featureName);

    /**
     * Set the public status of a feature
     * @param featureName feature name
     * @param isPublic true if public
     */
    void setFeaturePublic(String featureName, boolean isPublic);

    /**
     * Check if a feature is public
     * @param featureName feature name
     * @return true if public
     */
    boolean isFeaturePublic(String featureName);
}
