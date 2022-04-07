package main.java.com.yuzh.guava;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author yuzh
 * @date 2022/4/7 11:38
 */

public class CacheTest {

    public static void main(String[] args) throws Exception {
        LoadingCache<Object, Object> userCache = CacheBuilder.newBuilder()
                // 基于容量回收。缓存的最大数量。超过就取MAXIMUM_CAPACITY = 1 << 30。依靠LRU队列recencyQueue来进行容量淘汰
                .maximumSize(5)
                // 基于容量回收。但这是统计占用内存大小，maximumWeight与maximumSize不能同时使用。设置最大总权重
                //.maximumWeight(2)
                // 设置权重（可当成每个缓存占用的大小）
                //.weigher((o, o2) -> 5)
                // 软弱引用（引用强度顺序：强软弱虚）
                // -- 弱引用key
//                .weakKeys()
//                // -- 弱引用value
//                .weakValues()
                // -- 软引用value
                //.softValues()
                // 过期失效回收
                // -- 没读写访问下，超过5秒会失效(非自动失效，需有任意getput方法才会扫描过期失效数据)
               // .expireAfterAccess(5L, TimeUnit.SECONDS)
                // -- 没写访问下，超过5秒会失效(非自动失效，需有任意putget方法才会扫描过期失效数据)
                //.expireAfterWrite(5L, TimeUnit.SECONDS)
                // 没写访问下，超过5秒会失效(非自动失效，需有任意putget方法才会扫描过期失效数据。但区别是会开一个异步线程进行刷新，刷新过程中访问返回旧数据)
               // .refreshAfterWrite(5L, TimeUnit.SECONDS)
                // 移除监听事件
                .removalListener(removal -> {
                    // 可做一些删除后动作，比如上报删除数据用于统计
                    System.out.printf("触发删除动作，删除的key=%s%n", removal);
                })
                // 并行等级。决定segment数量的参数，concurrencyLevel与maxWeight共同决定
                .concurrencyLevel(16)
                // 开启缓存统计。比如命中次数、未命中次数等
                .recordStats()
                // 所有segment的初始总容量大小
                .initialCapacity(512)
                // 用于测试，可任意改变当前时间。参考：https://www.geek-share.com/detail/2689756248.html
//                .ticker(new Ticker() {
//                    @Override
//                    public long read() {
//                        return 0;
//                    }
//                })
                .build(new CacheLoader<Object, Object>() {
                    @Override
                    public Object load(Object name) {
                        // 在cache找不到就取数据
                        return String.format("load（%s）：%s", System.currentTimeMillis(), name);
                    }
                });
        // 简单使用
        System.out.println(userCache.get("a"));
        System.out.println(userCache.get("b"));
        System.out.println(userCache.get("c"));
        System.out.println(userCache.get("d"));
        System.out.println(userCache.get("e"));

        System.out.println(userCache.get("a"));

        System.out.println(userCache.get("f"));


    }
}
