package com.algamoney.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Classe criada com o objetivo de deixar a aplicação configurável externamente dependendo do ambiente (desenvolvimento e produção),
// 		através do application-prod.properties

// O uso dessa anotação precisa da dependência spring-boot-configuration-processor no pom.xml
// É necessário adicionar a anotação @EnableConfigurationProperties(AlgamoneyApiProperty.class)
// 		a classe principal da aplicação AlgamoneyApiApplication
@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {

	private String originPermitida = "http://localhost:8080";

	private final Seguranca seguranca = new Seguranca();

	public Seguranca getSeguranca() {
		return seguranca;
	}

	public String getOriginPermitida() {
		return originPermitida;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}

	// Classe com propriedades de segurança
	public static class Seguranca {

		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}

	}

}
