package br.com.dgs.triagem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TriagemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TriagemApplication.class, args);
    }
}
