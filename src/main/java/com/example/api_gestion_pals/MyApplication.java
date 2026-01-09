package com.example.api_gestion_pals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class MyApplication {

    @RequestMapping("/")
    public String home() {
        return "Bienvenue sur l'api de gestion de pals du jeu Palworld";
    }

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);

	}

}
