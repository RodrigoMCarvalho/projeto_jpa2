package br.com.caelum.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.caelum.model.Loja;
import br.com.caelum.model.Produto;

@Repository
public class ProdutoDao {

	@PersistenceContext
	private EntityManager em;

	public List<Produto> getProdutos() {
		return em.createQuery("from Produto", Produto.class).getResultList();
	}

	public Produto getProduto(Integer id) {
		Produto produto = em.find(Produto.class, id);
		return produto;
	}

	public List<Produto> getProdutos(String nome, Integer categoriaId, Integer lojaId) {
		// String jpql = "SELECT p FROM Produto p";
		//
		// if (categoriaId != null) {
		// jpql += " JOIN FETCH p.categorias c WHERE c.id = :pCategoriaID AND";
		// } else {
		// jpql += " WHERE"; //sen�o ocorrer� erro de sintaxe SQL ao buscar por nome,
		// por exemplo
		// }
		// if (lojaId != null) {
		// jpql += " p.loja.id = :pLojaId AND";
		// }
		// if (!nome.isEmpty()) {
		// jpql += " p.nome LIKE :pProdutoNome AND";
		// }
		// jpql += " 1 = 1";
		//
		// TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
		//
		// if (categoriaId != null)
		// query.setParameter("pCategoriaID", categoriaId);
		//
		// if (lojaId != null)
		// query.setParameter("pLojaId", lojaId);
		//
		// if (!nome.isEmpty())
		// query.setParameter("pProdutoNome", nome);
		//
		// return query.getResultList();

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		Root<Produto> root = query.from(Produto.class);

		Path<String> nomePath = root.<String>get("nome");
		Path<Integer> lojaPath = root.<Loja>get("loja").<Integer>get("id");
		Path<Integer> categoriaPath = root.join("categorias").<Integer>get("id");
		
		List<Predicate> predicates = new ArrayList<>();

		if (!nome.isEmpty()) {
			Predicate nomeIgual = criteriaBuilder.like(nomePath, "%" + nome + "%");
			predicates.add(nomeIgual);
		}
		if (lojaId != null) {
			Predicate localIgual = criteriaBuilder.equal(lojaPath, lojaId);
			predicates.add(localIgual);
		}
		if (categoriaId != null) {
			Predicate categoriaIgual = criteriaBuilder.equal(categoriaPath, categoriaId);
			predicates.add(categoriaIgual);
		}

		//query.where(nomeIgual, localIgual, categoriaIgual); - Desta forma apresenta erro pois as vari�veis s�o locais
		query.where((Predicate[]) predicates.toArray(new Predicate[0]));
		
		TypedQuery<Produto> typedQuery = em.createQuery(query);
		typedQuery.setHint("org.hibernate.cacheable", "true");

		return typedQuery.getResultList();
	}

	public void insere(Produto produto) {
		if (produto.getId() == null)
			em.persist(produto);
		else
			em.merge(produto);
	}

}
