package com.zjlab.dataservice.config.storage;

import com.zjlab.dataservice.common.util.storage.ServerUtil;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ServerConfig {

//    private static String host = "10.101.32.14";
    private String host = "10.105.12.47";
    private int port = 22;
//    private static String username = "root";
    private String username = "zj";
    private String password = "9#ezV7Bx!&";

    @Bean
    public void initServer(){
        ServerUtil.setHost(host);
        ServerUtil.setPort(port);
        ServerUtil.setUsername(username);
        ServerUtil.setPassword(password);
    }
}
