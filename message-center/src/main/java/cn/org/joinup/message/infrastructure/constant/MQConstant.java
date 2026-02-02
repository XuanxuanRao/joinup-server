package cn.org.joinup.message.infrastructure.constant;

public interface MQConstant {
    String TEAM_EXCHANGE = "team.direct";
    String USER_JOIN_TEAM_KEY = "user.join";
    String USER_JOIN_TEAM_QUEUE = "team.user.join.queue";
    String USER_QUIT_TEAM_KEY = "user.quit";
    String USER_QUIT_TEAM_QUEUE = "team.user.quit.queue";
    String TEAM_ESTABLISH_KEY = "team.establish";
    String TEAM_ESTABLISH_QUEUE = "team.establish.queue";
}
