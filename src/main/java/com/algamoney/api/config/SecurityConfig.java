package com.algamoney.api.config;

/*
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

// Primeira implementação de segurança (autenticação básica)
// Biblioteca spring-boot-starter-security (pom.xml)
// Classe para autenticação do tipo Basic

@Configuration	// não precisa dessa anotação, apenas pra deixar explícito que é uma classe de configuração
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// Método para autenticar o usuário
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Validação em memória do usuário e senha que deverá ser passado na requisição
		auth.inMemoryAuthentication()
			.withUser("admin").password("admin").roles("ROLE");
	}
	
	// Método para configurar as autorizações das requisições
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Configuração das autorizações das requisições
		http.authorizeRequests()
				.antMatchers("/categorias").permitAll()		// não precisa de usuário e senha pra ser acessado
				.anyRequest().authenticated()				// todo o resto precisa ser autenticado
				.and()
			.httpBasic()									// define autenticação básica
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()	// desabilita criação de sessão
			.csrf().disable();
	}
	
}
*/
