package com.algamoney.api.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.algamoney.api.model.Usuario;
import com.algamoney.api.repository.UsuarioRepository;

// Classe para autenticar usuário e senha de acordo com o que está cadastrado no banco e obter suas permissões
// Implementação da interface UserDetailsService que será injetada na classe ResourceServerConfig

@Service
public class AppUserDetailsService implements UserDetailsService {

	// Injeta o repositório responsável pelo acesso a tabela usuario
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	// Método responsável por autenticar usuário
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// Busca usuário no banco
		Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
		
		// Se usuário ou senha estão incorretos, lança exceção
		Usuario usuario = usuarioOptional.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha incorretos"));
		
		// Retorna uma implementação de UserDetails exigido pelo método com o usuário, senha e permissões
		// return new User(email, usuario.getSenha(), getPermissoes(usuario));
		
		// Retorna uma extensão de User (que por sua vez implementa UserDetails) para que possamos pegar o nome do usuário logado
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}
	
	/*
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
		Usuario usuario = usuarioOptional.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha incorretos"));
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}
	*/

	// Método para pegar as permissões e retoná-las em uma coleção
	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		
		// Pega a lista de permissões e, para cada permissão,
		// adiciona ao Set authorities um novo SimpleGrantedAuthority passando a descrição da permissão
		usuario.getPermissoes().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getDescricao().toUpperCase())));
		
		return authorities;
	}

}
