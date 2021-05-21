package com.algamoney.api.token;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.algamoney.api.config.property.AlgamoneyApiProperty;

// Classe para interceptar a resposta da requisição do Access Token (/oauth/token),
// com o objetivo de colocar o Refresh Token em um cookie

// ResponseBodyAdvice<T>: intercepta um corpo de resposta do servidor do tipo especificado por T
// OAuth2AccessToken: Tipo da resposta da requisição /oauth/token

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	// Classe de configuração de ambiente (desenvolvimento ou produção)
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	// Método para validar a execução do método seguinte (retorna true ou false)
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// Retorna true se o método da requisição for um POST Access Token (/oauth/token)
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	// Método para tirar o Refresh Token do corpo da resposta e colocá-lo em um cookie
	// Só Executa se o método anterior (supports) retornar true
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		// Realiza castings para poder acessar a requisição, a resposta e o corpo da resposta
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		// Pega o refreshToken
		String refreshToken = body.getRefreshToken().getValue();
		
		// Adiciona o refreshToken em um cookie
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		
		// Remove o refreshToken do corpo da resposta
		removerRefreshTokenDoBody(token);
		
		// Retorna o corpo da resposta agora sem o refreshToken
		return body;
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		// Cria um cookie contendo a string do RefreshToken
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		
		// Faz dele um cookie de http (acessível apenas em http)
		refreshTokenCookie.setHttpOnly(true);
		
		// Informa se o cookie vai funcionar apenas de forma segura (https) - true ou false
		// refreshTokenCookie.setSecure(false); 	// TODO: Mudar para true em producao para funcionar apenas em https
		refreshTokenCookie.setSecure(algamoneyApiProperty.getSeguranca().isEnableHttps());
		
		// Informa o caminho pra qual o cookie deve ser enviado para o browser
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		
		// Informa o tempo de expiração do cookie
		refreshTokenCookie.setMaxAge(2592000); // 30 dias
		
		// Adiciona o cookie na resposta
		resp.addCookie(refreshTokenCookie);
	}
	
	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

}
