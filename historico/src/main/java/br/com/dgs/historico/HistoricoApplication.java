package br.com.dgs.historico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HistoricoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HistoricoApplication.class, args);
    }

}
