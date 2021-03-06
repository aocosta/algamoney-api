package com.algamoney.api.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.exceptionhandler.AlgamoneyExceptionHandler.Erro;
import com.algamoney.api.model.Lancamento;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.projection.ResumoLancamento;
import com.algamoney.api.service.LancamentoService;
import com.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

// Classe que disponibiliza recursos de /lancamentos para os clientes

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	// Injeta a classe RecursoCriadoListener, que implementa esta interface
	@Autowired
	private ApplicationEventPublisher publisher;
	
	// Injeta um MessageSource, que representa o arquivo de mensagens messages.properties
	@Autowired
	private MessageSource messageSource;
	
	// LISTAR LANCAMENTOS ----------------------------------------------------------------
	/*
	@GetMapping
	public List<Lancamento> listar() {
		return lancamentoRepository.findAll();
	}
	*/
	
	/*
	@GetMapping
	public List<Lancamento> pesquisar(LancamentoFilter lancamentoFilter) {
		return lancamentoRepository.filtrar(lancamentoFilter);
	}
	*/

	// verifica os escopos - se o usu??rio e os clientes (angular e mobile) tem autoriza????o para chamar esse m??todo
	// Para que esta anota????o funcione foi necess??rio a anota????o @EnableGlobalMethodSecurity e o m??todo createExpressionHandler()
	// na classe ResourceServerConfig
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	@GetMapping
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}
	// -----------------------------------------------------------------------------------
	
	// LISTAR LANCAMENTOS RESUMIDO -------------------------------------------------------
	// verifica os escopos - se o usu??rio e os clientes (angular e mobile) tem autoriza????o para chamar esse m??todo
	// Para que esta anota????o funcione foi necess??rio a anota????o @EnableGlobalMethodSecurity e o m??todo createExpressionHandler()
	// na classe ResourceServerConfig
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	@GetMapping(params = "resumo")
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}
	// -----------------------------------------------------------------------------------
	
	// BUSCAR PELO C??DIGO ----------------------------------------------------------------
	// verifica os escopos - se o usu??rio e os clientes (angular e mobile) tem autoriza????o para chamar esse m??todo
	// Para que esta anota????o funcione foi necess??rio a anota????o @EnableGlobalMethodSecurity e o m??todo createExpressionHandler()
	// na classe ResourceServerConfig
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	@GetMapping("/{codigo}")
	public ResponseEntity<?> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Lancamento> obj = lancamentoRepository.findById(codigo);
		return !obj.isEmpty() ? ResponseEntity.ok(obj.get()) : ResponseEntity.notFound().build();
	}
	// -----------------------------------------------------------------------------------
	
	// INSERIR LAN??AMENTO ----------------------------------------------------------------
	// @RequestBody: o objeto ser?? passado no body da requisi????o
	// @Valid: valida as propriedades do objeto de acordo com as anota????es de valida????o cada um deles
	/*
	@PostMapping
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento lancamentoSalvo = lancamentoRepository.save(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	*/

	// verifica os escopos - se o usu??rio e os clientes (angular e mobile) tem autoriza????o para chamar esse m??todo
	// Para que esta anota????o funcione foi necess??rio a anota????o @EnableGlobalMethodSecurity e o m??todo createExpressionHandler()
	// na classe ResourceServerConfig
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	@PostMapping
											// @RequestBody: o objeto ser?? passado no body da requisi????o
											// @Valid: valida as propriedades do objeto de acordo com as anota????es de valida????o cada um deles
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	// -----------------------------------------------------------------------------------
	
	// DELETAR LAN??AMENTO ----------------------------------------------------------------
	// verifica os escopos - se o usu??rio e os clientes (angular e mobile) tem autoriza????o para chamar esse m??todo
	// Para que esta anota????o funcione foi necess??rio a anota????o @EnableGlobalMethodSecurity e o m??todo createExpressionHandler()
	// na classe ResourceServerConfig
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)	// c??digo de resposta do m??todo em caso de sucesso
	public void remover(@PathVariable Long codigo) {
		lancamentoRepository.deleteById(codigo);
	}
	// -----------------------------------------------------------------------------------
	
	// RESPOSTA DE PESSOA INEXISTENTE OU INATIVA -----------------------------------------
	@ExceptionHandler({ PessoaInexistenteOuInativaException.class })
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}
	
	// ALTERAR LAN??AMENTO ----------------------------------------------------------------
	// verifica os escopos - se o usu??rio e os clientes (angular e mobile) tem autoriza????o para chamar esse m??todo
	// Para que esta anota????o funcione foi necess??rio a anota????o @EnableGlobalMethodSecurity e o m??todo createExpressionHandler()
	// na classe ResourceServerConfig	
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	@PutMapping("/{codigo}")
	public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento) {
		try {
			Lancamento lancamentoSalvo = lancamentoService.atualizar(codigo, lancamento);
			return ResponseEntity.ok(lancamentoSalvo);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}
	// -----------------------------------------------------------------------------------
}
