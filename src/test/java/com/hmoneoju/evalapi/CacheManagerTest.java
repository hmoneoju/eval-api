package com.hmoneoju.evalapi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheManagerTest {

    @Autowired
    private CacheManager cacheManager;

    private Cache expressions;

    @Before
    public void init() {
        expressions = cacheManager.getCache("expressions");
        expressions.put( "2+2", 4);
        expressions.put( "5+1", 6);
        expressions.put( "6+4", 10);
    }

    @Test
    public void verifyCache() {
        assertEquals(expressions.get("2+2").get(), 4);
        assertEquals(expressions.get("5+1").get(), 6);
        assertEquals(expressions.get("6+4").get(), 10);
        assertTrue(expressions.get("Non existing") == null );
    }

}
