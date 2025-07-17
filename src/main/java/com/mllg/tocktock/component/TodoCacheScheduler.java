package com.mllg.tocktock.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class TodoCacheScheduler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearCompletedTodoCacheAtMidnight (){
        log.info("todo 완료 목록 캐시 삭제 작업 시작: {}", LocalDateTime.now());

        String cacheKeyPattern = "todolist::user:*";
        int deletedCount = 0;
        try{
            Set<String> keysToDelete = new HashSet<>();
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                ScanOptions options = ScanOptions.scanOptions()
                        .match(cacheKeyPattern)
                        .count(1000)
                        .build();
                Cursor<byte[]> cursor = connection.scan(options);

                while (cursor.hasNext()){
                    String key = new String(cursor.next(), StandardCharsets.UTF_8);
                    keysToDelete.add(key);
                }
                return null;
            });

            if (!keysToDelete.isEmpty()){
                Long deleted = redisTemplate.delete(keysToDelete);
                deletedCount = deleted.intValue();
            }
            log.info("패턴 '{}'에 해당하는 캐시 키 {}개 삭제 완료.", cacheKeyPattern, deletedCount);
        }catch (Exception e){
            log.info("Todo 완료 목록 캐시 삭제 중 오류 발생: {}", e.getMessage());
        }
        log.info("Todo 완료 목록 캐시 삭제 작업 완료");
    }
}
