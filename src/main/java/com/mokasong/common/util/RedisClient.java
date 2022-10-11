package com.mokasong.common.util;

import com.mokasong.common.state.RedisCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisClient {
    private ValueOperations<String, String> valueOperations;

    private final String PREFIX1 = "verification-code:register-cellphone:";
    private final String PREFIX2 = "verification-token:change-to-stand-by-register:";
    private final String PREFIX3 = "verification-code:find-email:";
    private final String PREFIX4 = "verification-code:find-password:";

    @Autowired
    public RedisClient(RedisTemplate<String, String> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void setString(RedisCategory category, String suffix, String value, int minuteToLive) throws Exception {
        String key = this.getKeyByCategory(category, suffix);
        this.valueOperations.set(key, value, Duration.ofMinutes(minuteToLive));
    }

    public String getString(RedisCategory category, String suffix) throws Exception {
        String key = this.getKeyByCategory(category, suffix);
        return this.valueOperations.get(key);
    }

    public void deleteKey(RedisCategory category, String suffix) throws Exception {
        String key = this.getKeyByCategory(category, suffix);
        this.valueOperations.getAndDelete(key);
    }

    private String getKeyByCategory(RedisCategory category, String suffix) throws Exception {
        String key;

        switch (category) {
            case REGISTER_CELLPHONE:
                key = this.PREFIX1 + suffix;
                break;
            case CHANGE_TO_STAND_BY_REGULAR:
                key = this.PREFIX2 + suffix;
                break;
            case FIND_EMAIL:
                key = this.PREFIX3 + suffix;
                break;
            case FIND_PASSWORD:
                key = this.PREFIX4 + suffix;
                break;
            default:
                throw new IllegalArgumentException("RedisSetCategory Enum에 정의된 값만 넣어주세요.");
        }

        return key;
    }
}
