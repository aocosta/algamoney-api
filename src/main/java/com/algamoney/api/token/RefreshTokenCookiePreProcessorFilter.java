package com.algamoney.api.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/* Esse filtro foi criado porque o refreshToken não virá mais como parâmetro do body, mas apenas no cookie.
 * O parâmetro refresh_token será criado dinamicamente
*/

// Ordered.HIGHEST_PRECEDENCE: alta prioridade, analisa a requisição primeiro
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// Realiza castings para poder acessar a requisição
		HttpServletRequest req = (HttpServletRequest) request;
		
		/*
		 *  Verifica se a requisição é para o oauth/token e
		 *  se a parâmetro grant_type contem o valor "refresh_token" e
		 *  se existe algum cookie
		*/
		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI()) 
				&& "refresh_token".equals(req.getParameter("grant_type"))
				&& req.getCookies() != null) {
			// Procura pelo cookie refreshToken
			for (Cookie cookie : req.getCookies()) {
				if (cookie.getName().equals("refreshToken")) {
					// Extrai o valor do cookie
					String refreshToken = cookie.getValue();
					// Como não é possível manipular os parâmetros da requisição original, cria uma nova com o parâmetro refresh_token
					req = new MyServletRequestWrapper(req, refreshToken);
				}
			}
		}
		
		// Continua o processamento
		chain.doFilter(req, response);
	}
	
	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}
	
	// Classe local criada apenas para o propósito de manipular a requisição, já que não é possível manipulá-la diretamente 
	static class MyServletRequestWrapper extends HttpServletRequestWrapper {

		private String refreshToken;
		
		// Construtor recebendo a requisição original e o refreshToken
		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}
		
		@Override
		public Map<String, String[]> getParameterMap() {
			// Cria um novo mapa de parâmetros inserindo os parâmetro originais da requisição
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			// Insere um novo parâmetro de nome refresh_token
			map.put("refresh_token", new String[] { refreshToken });
			// Tava o mapa para que não seja possível inserir mais nada
			map.setLocked(true);
			
			return map;
		}
		
	}

}