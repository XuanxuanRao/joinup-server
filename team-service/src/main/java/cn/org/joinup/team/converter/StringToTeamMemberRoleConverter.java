package cn.org.joinup.team.converter;

import cn.org.joinup.team.enums.TeamMemberRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
public class StringToTeamMemberRoleConverter implements Converter<String, TeamMemberRole> {
    @Override
    public TeamMemberRole convert(String source) {
        if (source.trim().isEmpty()) {
            return null;
        }
        try {
            Integer intValue = Integer.parseInt(source);
            return TeamMemberRole.fromValue(intValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task status value format: " + source, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task status value: " + source, e);
        }
    }
}
