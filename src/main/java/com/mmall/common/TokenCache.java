package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @program: mmall
 * @description: Guvva本地缓存
 * @author: hexin
 * @create: 2018-04-15 17:08
 **/
public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    public   static  final  String TOKEN_PREFIX = "token_";

    // 初始化容量1000 最大10000 有效期12小时
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000)
            .maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应值，就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }
    public static String getKey(String key){
        String value = null;
        try{
            value  = localCache.get(key);
            if("null".equals(value)){
                return  null;
            }
            return  value;
        }catch (Exception e){
            //打印堆栈信息
            logger.error("localcache get error",e);
        }
        return null;
    }

}
