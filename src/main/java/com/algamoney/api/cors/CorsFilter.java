package com.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.algamoney.api.config.property.AlgamoneyApiProperty;

// Classe para filtrar as origens que terão acesso a aplicação

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)	// // Ordered.HIGHEST_PRECEDENCE: alta prioridade, analisa a requisição antes de todo mundo
public class CorsFilter implements Filter {

	// Essa configuração passou a ser dada pela classe AlgamoneyApiProperty
	// private String originPermitida = "http://localhost:8080"; // TODO: Configurar para diferentes ambientes
	
	// Classe de configuração de ambiente (desenvolvimento ou produção)
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		// Realiza castings para poder acessar a requisição e a resposta
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		// Esses dois Headers precisam estar fora do if porque precisam ser enviados sempre na resposta,
		// para que "POST, GET, DELETE e PUT continuem funcionado para requisições de outra origem
		// ----------------------------------------------------------------------------------------------------------
		// Seta a origem permitida
		// response.setHeader("Access-Control-Allow-Origin", originPermitida);
		response.setHeader("Access-Control-Allow-Origin", algamoneyApiProperty.getOriginPermitida());
		
		// Responde que a origem tem permissão para enviar cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // ----------------------------------------------------------------------------------------------------------
		
        // Se a requisição for um OPTIONS, ou seja, for um preflight do browser antes da requisição original
        // e a requisição for de uma origem permitida
		// if ("OPTIONS".equals(request.getMethod()) && originPermitida.equals(request.getHeader("Origin"))) {
		if ("OPTIONS".equals(request.getMethod()) && algamoneyApiProperty.getOriginPermitida().equals(request.getHeader("Origin"))) {
			// Responde quais os métodos permitidos para essa origem
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			// Responde quais os headers permitidos para essa origem
        	response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        	// Responde qual o tempo permitido para essa origem
        	response.setHeader("Access-Control-Max-Age", "3600");	// 1 hora
			
        	// Código de resposta OK
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			// Continua o processamento normal
			chain.doFilter(req, resp);
		}
		
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
