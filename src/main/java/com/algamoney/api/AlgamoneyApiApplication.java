package com.algamoney.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.algamoney.api.config.property.AlgamoneyApiProperty;

@SpringBootApplication
// habilita a classe de configuração de propriedades para que a aplicação possa ser configurável externamente
@EnableConfigurationProperties(AlgamoneyApiProperty.class)
public class AlgamoneyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgamoneyApiApplication.class, args);
	}
	
	// Configuração de Cross-origin para toda a aplicação
	// A configuração de Cross-origin não será feita dessa forma porque ela não está totalmente integrada com o oauth2,
	// será feito através de um filtro
	/*
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// Permite a origem localhost:8000 ter acesso a toda a aplicação
				registry.addMapping("/*").allowedOrigins("http://localhost:8000");
			}
		};
	}
	*/

}
