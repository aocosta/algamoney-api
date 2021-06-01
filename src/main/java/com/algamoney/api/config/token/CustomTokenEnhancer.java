package com.algamoney.api.config.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.algamoney.api.security.UsuarioSistema;

public class CustomTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		// Obtem o usuário logado
		UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
		
		// Cria um mapa e adiciona o nome nome do usuário logado 
		Map<String, Object> addInfo = new HashMap<>();
		addInfo.put("nome", usuarioSistema.getUsuario().getNome());
		
		// Adiciona o mapa no token
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addInfo);
		
		// Retorna o token
		return accessToken;
	}

}
