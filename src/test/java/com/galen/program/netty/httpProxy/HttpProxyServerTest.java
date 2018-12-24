package com.galen.program.netty.httpProxy;

import org.junit.Test;

/**
 * Created by baogen.zhang on 2018/11/9
 *
 * @author baogen.zhang
 * @date 2018/11/9
 */
public class HttpProxyServerTest {

    @Test
    public void test(){

        try {
            new HttpProxyServer().run(8081);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
