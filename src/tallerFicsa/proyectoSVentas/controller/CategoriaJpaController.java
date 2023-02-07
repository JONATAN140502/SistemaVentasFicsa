/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tallerFicsa.proyectoSVentas.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import tallerFicsa.proyectoSVentas.entity.Articulo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import tallerFicsa.proyectoSVentas.controller.exceptions.IllegalOrphanException;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Categoria;

/**
 *
 * @author Usuario
 */
public class CategoriaJpaController implements Serializable {

    public CategoriaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public CategoriaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("ProyectoSVentasPU");
    }

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categoria categoria) {
        if (categoria.getArticuloList() == null) {
            categoria.setArticuloList(new ArrayList<Articulo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Articulo> attachedArticuloList = new ArrayList<Articulo>();
            for (Articulo articuloListArticuloToAttach : categoria.getArticuloList()) {
                articuloListArticuloToAttach = em.getReference(articuloListArticuloToAttach.getClass(), articuloListArticuloToAttach.getIdarticulo());
                attachedArticuloList.add(articuloListArticuloToAttach);
            }
            categoria.setArticuloList(attachedArticuloList);
            em.persist(categoria);
            for (Articulo articuloListArticulo : categoria.getArticuloList()) {
                Categoria oldIdcategoriaOfArticuloListArticulo = articuloListArticulo.getIdcategoria();
                articuloListArticulo.setIdcategoria(categoria);
                articuloListArticulo = em.merge(articuloListArticulo);
                if (oldIdcategoriaOfArticuloListArticulo != null) {
                    oldIdcategoriaOfArticuloListArticulo.getArticuloList().remove(articuloListArticulo);
                    oldIdcategoriaOfArticuloListArticulo = em.merge(oldIdcategoriaOfArticuloListArticulo);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categoria categoria) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getIdcategoria());
            List<Articulo> articuloListOld = persistentCategoria.getArticuloList();
            List<Articulo> articuloListNew = categoria.getArticuloList();
            List<String> illegalOrphanMessages = null;
            for (Articulo articuloListOldArticulo : articuloListOld) {
                if (!articuloListNew.contains(articuloListOldArticulo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Articulo " + articuloListOldArticulo + " since its idcategoria field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Articulo> attachedArticuloListNew = new ArrayList<Articulo>();
            for (Articulo articuloListNewArticuloToAttach : articuloListNew) {
                articuloListNewArticuloToAttach = em.getReference(articuloListNewArticuloToAttach.getClass(), articuloListNewArticuloToAttach.getIdarticulo());
                attachedArticuloListNew.add(articuloListNewArticuloToAttach);
            }
            articuloListNew = attachedArticuloListNew;
            categoria.setArticuloList(articuloListNew);
            categoria = em.merge(categoria);
            for (Articulo articuloListNewArticulo : articuloListNew) {
                if (!articuloListOld.contains(articuloListNewArticulo)) {
                    Categoria oldIdcategoriaOfArticuloListNewArticulo = articuloListNewArticulo.getIdcategoria();
                    articuloListNewArticulo.setIdcategoria(categoria);
                    articuloListNewArticulo = em.merge(articuloListNewArticulo);
                    if (oldIdcategoriaOfArticuloListNewArticulo != null && !oldIdcategoriaOfArticuloListNewArticulo.equals(categoria)) {
                        oldIdcategoriaOfArticuloListNewArticulo.getArticuloList().remove(articuloListNewArticulo);
                        oldIdcategoriaOfArticuloListNewArticulo = em.merge(oldIdcategoriaOfArticuloListNewArticulo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categoria.getIdcategoria();
                if (findCategoria(id) == null) {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getIdcategoria();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Articulo> articuloListOrphanCheck = categoria.getArticuloList();
            for (Articulo articuloListOrphanCheckArticulo : articuloListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Categoria (" + categoria + ") cannot be destroyed since the Articulo " + articuloListOrphanCheckArticulo + " in its articuloList field has a non-nullable idcategoria field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(categoria);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
