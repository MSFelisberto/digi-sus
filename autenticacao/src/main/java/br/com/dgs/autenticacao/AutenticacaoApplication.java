package br.com.dgs.autenticacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AutenticacaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutenticacaoApplication.class, args);
    }

}
