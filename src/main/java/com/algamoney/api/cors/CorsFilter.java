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

// Esse filtro foi criado para permitir o acesso de origens permitidas

// Ordered.HIGHEST_PRECEDENCE: alta prioridade, analisa a requisição primeiro
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	// private String originPermitida = "http://localhost:8000"; // TODO: Configurar para diferentes ambientes
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		// Realiza castings para poder acessar a requisição e a resposta
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		// Seta a origem permitida
		// response.setHeader("Access-Control-Allow-Origin", originPermitida);
		response.setHeader("Access-Control-Allow-Origin", algamoneyApiProperty.getOriginPermitida());
		
		// Seta permissão para enviar cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
		
        // Se o método da requisição for um OPTIONS e a requisição for de uma origem permitida
		// if ("OPTIONS".equals(request.getMethod()) && originPermitida.equals(request.getHeader("Origin"))) {
		if ("OPTIONS".equals(request.getMethod()) && algamoneyApiProperty.getOriginPermitida().equals(request.getHeader("Origin"))) {
			// Seta os métodos permitidos para essa origem
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			// Seta os headers permitidos para essa origem
        	response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        	// Seta o tempo permitidos para essa origem
        	response.setHeader("Access-Control-Max-Age", "3600");
			
        	// Envia código de resposta OK
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
