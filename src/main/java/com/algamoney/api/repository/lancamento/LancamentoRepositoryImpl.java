package com.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.algamoney.api.model.Lancamento;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;
	
	/*
	@Override
	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		// Criar o array de retrições de filtragem para a clausula WHERE
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		// Executa a query
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		
		// Retorna a lista de lançamentos
		return query.getResultList();
	}
	*/
	
	// Método que retorna um modelo completo da entidade Lancamento com os dados filtrados e paginados
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		// Criar o array de restrições de filtragem para a clausula WHERE
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		
		// coloca as restrições na cláusula WHERE
		criteria.where(predicates);
		
		// Executa a query
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		
		// Adiciona as restrições de paginação
		adicionarRestricoesDePaginacao(query, pageable);
		
		// Retorna a lista de lançamentos
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}
	
	// Método que retorna um modelo resumido de entidade Lancamento com os dados filtrados e paginados
	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
	
		// Utilizando metamodel (não funcionou)
		/*
		criteria.select(builder.construct(ResumoLancamento.class
				, root.get(Lancamento_.codigo), root.get(Lancamento_.descricao)
				, root.get(Lancamento_.dataVencimento), root.get(Lancamento_.dataPagamento)
				, root.get(Lancamento_.valor), root.get(Lancamento_.tipo)
				, root.get(Lancamento_.categoria).get(Categoria_.nome)
				, root.get(Lancamento_.pessoa).get(Pessoa_.nome)));
		*/
		
		// Cria a clausula SELECT
		criteria.select(builder.construct(ResumoLancamento.class
				, root.get("codigo")
				, root.get("descricao")
				, root.get("dataVencimento")
				, root.get("dataPagamento")
				, root.get("valor")
				, root.get("tipo")
				, root.get("categoria").get("nome")
				, root.get("pessoa").get("nome")));
		
		// Criar o array de restrições de filtragem para a clausula WHERE
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		
		// coloca as restrições na cláusula WHERE
		criteria.where(predicates);
		
		// Executa a query
		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		
		// Adiciona as restrições de paginação
		adicionarRestricoesDePaginacao(query, pageable);
		
		// Retorna a lista de lançamentos
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		// WHERE descricao like "%xxxxxxxx%"
		if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
			
			// Utilizando metamodel (não funcionou)
			// predicates.add(builder.like(builder.lower(root.get(Lancamento_.descricao)), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
			
			predicates.add(builder.like(
					builder.lower(root.get("descricao")), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}
		
		// AND data_vencimento >= 9999/99/99
		if (lancamentoFilter.getDataVencimentoDe() != null) {
			
			// Utilizando metamodel (não funcionou)
			// predicates.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));
			
			predicates.add(builder.greaterThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoDe()));
		}
		
		// AND data_vencimento <= 9999/99/99
		if (lancamentoFilter.getDataVencimentoAte() != null) {
			
			// Utilizando metamodel (não funcionou)
			// predicates.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
			
			predicates.add(builder.lessThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoAte()));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	// private void adicionarRestricoesDePaginacao(TypedQuery<Lancamento> query, Pageable pageable) {
	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	
	// OBTEM O TOTAL DE REGISTROS
	private Long total(LancamentoFilter lancamentoFilter) {
		
		// Obtem um objeto criador de query 
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		// Obtem um objeto Criteria para colocar as claúsulas da query
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		
		// Cláusula From
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		// Cláusula Where
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		// Faz um select count()
		criteria.select(builder.count(root));
		
		// Retorna a quantidade de registros
		return manager.createQuery(criteria).getSingleResult();
	}

}
