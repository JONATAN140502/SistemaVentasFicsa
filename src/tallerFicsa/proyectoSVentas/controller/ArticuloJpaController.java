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
import tallerFicsa.proyectoSVentas.entity.Categoria;
import tallerFicsa.proyectoSVentas.entity.DetalleVenta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import tallerFicsa.proyectoSVentas.controller.exceptions.IllegalOrphanException;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Articulo;
import tallerFicsa.proyectoSVentas.entity.DetalleIngreso;

/**
 *
 * @author Usuario
 */
public class ArticuloJpaController implements Serializable {

    public ArticuloJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;
    
    public ArticuloJpaController() {
        this.emf = Persistence.createEntityManagerFactory("ProyectoSVentasPU");
    }
    
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Articulo articulo) {
        if (articulo.getDetalleVentaCollection() == null) {
            articulo.setDetalleVentaCollection(new ArrayList<DetalleVenta>());
        }
        if (articulo.getDetalleIngresoCollection() == null) {
            articulo.setDetalleIngresoCollection(new ArrayList<DetalleIngreso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria idcategoria = articulo.getIdcategoria();
            if (idcategoria != null) {
                idcategoria = em.getReference(idcategoria.getClass(), idcategoria.getIdcategoria());
                articulo.setIdcategoria(idcategoria);
            }
            Collection<DetalleVenta> attachedDetalleVentaCollection = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaCollectionDetalleVentaToAttach : articulo.getDetalleVentaCollection()) {
                detalleVentaCollectionDetalleVentaToAttach = em.getReference(detalleVentaCollectionDetalleVentaToAttach.getClass(), detalleVentaCollectionDetalleVentaToAttach.getIddetalleVenta());
                attachedDetalleVentaCollection.add(detalleVentaCollectionDetalleVentaToAttach);
            }
            articulo.setDetalleVentaCollection(attachedDetalleVentaCollection);
            Collection<DetalleIngreso> attachedDetalleIngresoCollection = new ArrayList<DetalleIngreso>();
            for (DetalleIngreso detalleIngresoCollectionDetalleIngresoToAttach : articulo.getDetalleIngresoCollection()) {
                detalleIngresoCollectionDetalleIngresoToAttach = em.getReference(detalleIngresoCollectionDetalleIngresoToAttach.getClass(), detalleIngresoCollectionDetalleIngresoToAttach.getIddetalleIngreso());
                attachedDetalleIngresoCollection.add(detalleIngresoCollectionDetalleIngresoToAttach);
            }
            articulo.setDetalleIngresoCollection(attachedDetalleIngresoCollection);
            em.persist(articulo);
            if (idcategoria != null) {
                idcategoria.getArticuloList().add(articulo);
                idcategoria = em.merge(idcategoria);
            }
            for (DetalleVenta detalleVentaCollectionDetalleVenta : articulo.getDetalleVentaCollection()) {
                Articulo oldIdarticuloOfDetalleVentaCollectionDetalleVenta = detalleVentaCollectionDetalleVenta.getIdarticulo();
                detalleVentaCollectionDetalleVenta.setIdarticulo(articulo);
                detalleVentaCollectionDetalleVenta = em.merge(detalleVentaCollectionDetalleVenta);
                if (oldIdarticuloOfDetalleVentaCollectionDetalleVenta != null) {
                    oldIdarticuloOfDetalleVentaCollectionDetalleVenta.getDetalleVentaCollection().remove(detalleVentaCollectionDetalleVenta);
                    oldIdarticuloOfDetalleVentaCollectionDetalleVenta = em.merge(oldIdarticuloOfDetalleVentaCollectionDetalleVenta);
                }
            }
            for (DetalleIngreso detalleIngresoCollectionDetalleIngreso : articulo.getDetalleIngresoCollection()) {
                Articulo oldIdarticuloOfDetalleIngresoCollectionDetalleIngreso = detalleIngresoCollectionDetalleIngreso.getIdarticulo();
                detalleIngresoCollectionDetalleIngreso.setIdarticulo(articulo);
                detalleIngresoCollectionDetalleIngreso = em.merge(detalleIngresoCollectionDetalleIngreso);
                if (oldIdarticuloOfDetalleIngresoCollectionDetalleIngreso != null) {
                    oldIdarticuloOfDetalleIngresoCollectionDetalleIngreso.getDetalleIngresoCollection().remove(detalleIngresoCollectionDetalleIngreso);
                    oldIdarticuloOfDetalleIngresoCollectionDetalleIngreso = em.merge(oldIdarticuloOfDetalleIngresoCollectionDetalleIngreso);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Articulo articulo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulo persistentArticulo = em.find(Articulo.class, articulo.getIdarticulo());
            Categoria idcategoriaOld = persistentArticulo.getIdcategoria();
            Categoria idcategoriaNew = articulo.getIdcategoria();
            Collection<DetalleVenta> detalleVentaCollectionOld = persistentArticulo.getDetalleVentaCollection();
            Collection<DetalleVenta> detalleVentaCollectionNew = articulo.getDetalleVentaCollection();
            Collection<DetalleIngreso> detalleIngresoCollectionOld = persistentArticulo.getDetalleIngresoCollection();
            Collection<DetalleIngreso> detalleIngresoCollectionNew = articulo.getDetalleIngresoCollection();
            List<String> illegalOrphanMessages = null;
            for (DetalleVenta detalleVentaCollectionOldDetalleVenta : detalleVentaCollectionOld) {
                if (!detalleVentaCollectionNew.contains(detalleVentaCollectionOldDetalleVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleVenta " + detalleVentaCollectionOldDetalleVenta + " since its idarticulo field is not nullable.");
                }
            }
            for (DetalleIngreso detalleIngresoCollectionOldDetalleIngreso : detalleIngresoCollectionOld) {
                if (!detalleIngresoCollectionNew.contains(detalleIngresoCollectionOldDetalleIngreso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleIngreso " + detalleIngresoCollectionOldDetalleIngreso + " since its idarticulo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idcategoriaNew != null) {
                idcategoriaNew = em.getReference(idcategoriaNew.getClass(), idcategoriaNew.getIdcategoria());
                articulo.setIdcategoria(idcategoriaNew);
            }
            Collection<DetalleVenta> attachedDetalleVentaCollectionNew = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaCollectionNewDetalleVentaToAttach : detalleVentaCollectionNew) {
                detalleVentaCollectionNewDetalleVentaToAttach = em.getReference(detalleVentaCollectionNewDetalleVentaToAttach.getClass(), detalleVentaCollectionNewDetalleVentaToAttach.getIddetalleVenta());
                attachedDetalleVentaCollectionNew.add(detalleVentaCollectionNewDetalleVentaToAttach);
            }
            detalleVentaCollectionNew = attachedDetalleVentaCollectionNew;
            articulo.setDetalleVentaCollection(detalleVentaCollectionNew);
            Collection<DetalleIngreso> attachedDetalleIngresoCollectionNew = new ArrayList<DetalleIngreso>();
            for (DetalleIngreso detalleIngresoCollectionNewDetalleIngresoToAttach : detalleIngresoCollectionNew) {
                detalleIngresoCollectionNewDetalleIngresoToAttach = em.getReference(detalleIngresoCollectionNewDetalleIngresoToAttach.getClass(), detalleIngresoCollectionNewDetalleIngresoToAttach.getIddetalleIngreso());
                attachedDetalleIngresoCollectionNew.add(detalleIngresoCollectionNewDetalleIngresoToAttach);
            }
            detalleIngresoCollectionNew = attachedDetalleIngresoCollectionNew;
            articulo.setDetalleIngresoCollection(detalleIngresoCollectionNew);
            articulo = em.merge(articulo);
            if (idcategoriaOld != null && !idcategoriaOld.equals(idcategoriaNew)) {
                idcategoriaOld.getArticuloList().remove(articulo);
                idcategoriaOld = em.merge(idcategoriaOld);
            }
            if (idcategoriaNew != null && !idcategoriaNew.equals(idcategoriaOld)) {
                idcategoriaNew.getArticuloList().add(articulo);
                idcategoriaNew = em.merge(idcategoriaNew);
            }
            for (DetalleVenta detalleVentaCollectionNewDetalleVenta : detalleVentaCollectionNew) {
                if (!detalleVentaCollectionOld.contains(detalleVentaCollectionNewDetalleVenta)) {
                    Articulo oldIdarticuloOfDetalleVentaCollectionNewDetalleVenta = detalleVentaCollectionNewDetalleVenta.getIdarticulo();
                    detalleVentaCollectionNewDetalleVenta.setIdarticulo(articulo);
                    detalleVentaCollectionNewDetalleVenta = em.merge(detalleVentaCollectionNewDetalleVenta);
                    if (oldIdarticuloOfDetalleVentaCollectionNewDetalleVenta != null && !oldIdarticuloOfDetalleVentaCollectionNewDetalleVenta.equals(articulo)) {
                        oldIdarticuloOfDetalleVentaCollectionNewDetalleVenta.getDetalleVentaCollection().remove(detalleVentaCollectionNewDetalleVenta);
                        oldIdarticuloOfDetalleVentaCollectionNewDetalleVenta = em.merge(oldIdarticuloOfDetalleVentaCollectionNewDetalleVenta);
                    }
                }
            }
            for (DetalleIngreso detalleIngresoCollectionNewDetalleIngreso : detalleIngresoCollectionNew) {
                if (!detalleIngresoCollectionOld.contains(detalleIngresoCollectionNewDetalleIngreso)) {
                    Articulo oldIdarticuloOfDetalleIngresoCollectionNewDetalleIngreso = detalleIngresoCollectionNewDetalleIngreso.getIdarticulo();
                    detalleIngresoCollectionNewDetalleIngreso.setIdarticulo(articulo);
                    detalleIngresoCollectionNewDetalleIngreso = em.merge(detalleIngresoCollectionNewDetalleIngreso);
                    if (oldIdarticuloOfDetalleIngresoCollectionNewDetalleIngreso != null && !oldIdarticuloOfDetalleIngresoCollectionNewDetalleIngreso.equals(articulo)) {
                        oldIdarticuloOfDetalleIngresoCollectionNewDetalleIngreso.getDetalleIngresoCollection().remove(detalleIngresoCollectionNewDetalleIngreso);
                        oldIdarticuloOfDetalleIngresoCollectionNewDetalleIngreso = em.merge(oldIdarticuloOfDetalleIngresoCollectionNewDetalleIngreso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = articulo.getIdarticulo();
                if (findArticulo(id) == null) {
                    throw new NonexistentEntityException("The articulo with id " + id + " no longer exists.");
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
            Articulo articulo;
            try {
                articulo = em.getReference(Articulo.class, id);
                articulo.getIdarticulo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articulo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<DetalleVenta> detalleVentaCollectionOrphanCheck = articulo.getDetalleVentaCollection();
            for (DetalleVenta detalleVentaCollectionOrphanCheckDetalleVenta : detalleVentaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articulo (" + articulo + ") cannot be destroyed since the DetalleVenta " + detalleVentaCollectionOrphanCheckDetalleVenta + " in its detalleVentaCollection field has a non-nullable idarticulo field.");
            }
            Collection<DetalleIngreso> detalleIngresoCollectionOrphanCheck = articulo.getDetalleIngresoCollection();
            for (DetalleIngreso detalleIngresoCollectionOrphanCheckDetalleIngreso : detalleIngresoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articulo (" + articulo + ") cannot be destroyed since the DetalleIngreso " + detalleIngresoCollectionOrphanCheckDetalleIngreso + " in its detalleIngresoCollection field has a non-nullable idarticulo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Categoria idcategoria = articulo.getIdcategoria();
            if (idcategoria != null) {
                idcategoria.getArticuloList().remove(articulo);
                idcategoria = em.merge(idcategoria);
            }
            em.remove(articulo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Articulo> findArticuloEntities() {
        return findArticuloEntities(true, -1, -1);
    }

    public List<Articulo> findArticuloEntities(int maxResults, int firstResult) {
        return findArticuloEntities(false, maxResults, firstResult);
    }

    private List<Articulo> findArticuloEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Articulo.class));
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

    public Articulo findArticulo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Articulo.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticuloCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Articulo> rt = cq.from(Articulo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
