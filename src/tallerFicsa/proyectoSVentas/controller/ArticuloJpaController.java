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

    public ArticuloJpaController() {
        this.emf = Persistence.createEntityManagerFactory("ProyectoSVentasPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Articulo articulo) {
        if (articulo.getDetalleVentaList() == null) {
            articulo.setDetalleVentaList(new ArrayList<DetalleVenta>());
        }
        if (articulo.getDetalleIngresoList() == null) {
            articulo.setDetalleIngresoList(new ArrayList<DetalleIngreso>());
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
            List<DetalleVenta> attachedDetalleVentaList = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListDetalleVentaToAttach : articulo.getDetalleVentaList()) {
                detalleVentaListDetalleVentaToAttach = em.getReference(detalleVentaListDetalleVentaToAttach.getClass(), detalleVentaListDetalleVentaToAttach.getIddetalleVenta());
                attachedDetalleVentaList.add(detalleVentaListDetalleVentaToAttach);
            }
            articulo.setDetalleVentaList(attachedDetalleVentaList);
            List<DetalleIngreso> attachedDetalleIngresoList = new ArrayList<DetalleIngreso>();
            for (DetalleIngreso detalleIngresoListDetalleIngresoToAttach : articulo.getDetalleIngresoList()) {
                detalleIngresoListDetalleIngresoToAttach = em.getReference(detalleIngresoListDetalleIngresoToAttach.getClass(), detalleIngresoListDetalleIngresoToAttach.getIddetalleIngreso());
                attachedDetalleIngresoList.add(detalleIngresoListDetalleIngresoToAttach);
            }
            articulo.setDetalleIngresoList(attachedDetalleIngresoList);
            em.persist(articulo);
            if (idcategoria != null) {
                idcategoria.getArticuloList().add(articulo);
                idcategoria = em.merge(idcategoria);
            }
            for (DetalleVenta detalleVentaListDetalleVenta : articulo.getDetalleVentaList()) {
                Articulo oldIdarticuloOfDetalleVentaListDetalleVenta = detalleVentaListDetalleVenta.getIdarticulo();
                detalleVentaListDetalleVenta.setIdarticulo(articulo);
                detalleVentaListDetalleVenta = em.merge(detalleVentaListDetalleVenta);
                if (oldIdarticuloOfDetalleVentaListDetalleVenta != null) {
                    oldIdarticuloOfDetalleVentaListDetalleVenta.getDetalleVentaList().remove(detalleVentaListDetalleVenta);
                    oldIdarticuloOfDetalleVentaListDetalleVenta = em.merge(oldIdarticuloOfDetalleVentaListDetalleVenta);
                }
            }
            for (DetalleIngreso detalleIngresoListDetalleIngreso : articulo.getDetalleIngresoList()) {
                Articulo oldIdarticuloOfDetalleIngresoListDetalleIngreso = detalleIngresoListDetalleIngreso.getIdarticulo();
                detalleIngresoListDetalleIngreso.setIdarticulo(articulo);
                detalleIngresoListDetalleIngreso = em.merge(detalleIngresoListDetalleIngreso);
                if (oldIdarticuloOfDetalleIngresoListDetalleIngreso != null) {
                    oldIdarticuloOfDetalleIngresoListDetalleIngreso.getDetalleIngresoList().remove(detalleIngresoListDetalleIngreso);
                    oldIdarticuloOfDetalleIngresoListDetalleIngreso = em.merge(oldIdarticuloOfDetalleIngresoListDetalleIngreso);
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
            List<DetalleVenta> detalleVentaListOld = persistentArticulo.getDetalleVentaList();
            List<DetalleVenta> detalleVentaListNew = articulo.getDetalleVentaList();
            List<DetalleIngreso> detalleIngresoListOld = persistentArticulo.getDetalleIngresoList();
            List<DetalleIngreso> detalleIngresoListNew = articulo.getDetalleIngresoList();
            List<String> illegalOrphanMessages = null;
            for (DetalleVenta detalleVentaListOldDetalleVenta : detalleVentaListOld) {
                if (!detalleVentaListNew.contains(detalleVentaListOldDetalleVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleVenta " + detalleVentaListOldDetalleVenta + " since its idarticulo field is not nullable.");
                }
            }
            for (DetalleIngreso detalleIngresoListOldDetalleIngreso : detalleIngresoListOld) {
                if (!detalleIngresoListNew.contains(detalleIngresoListOldDetalleIngreso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleIngreso " + detalleIngresoListOldDetalleIngreso + " since its idarticulo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idcategoriaNew != null) {
                idcategoriaNew = em.getReference(idcategoriaNew.getClass(), idcategoriaNew.getIdcategoria());
                articulo.setIdcategoria(idcategoriaNew);
            }
            List<DetalleVenta> attachedDetalleVentaListNew = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListNewDetalleVentaToAttach : detalleVentaListNew) {
                detalleVentaListNewDetalleVentaToAttach = em.getReference(detalleVentaListNewDetalleVentaToAttach.getClass(), detalleVentaListNewDetalleVentaToAttach.getIddetalleVenta());
                attachedDetalleVentaListNew.add(detalleVentaListNewDetalleVentaToAttach);
            }
            detalleVentaListNew = attachedDetalleVentaListNew;
            articulo.setDetalleVentaList(detalleVentaListNew);
            List<DetalleIngreso> attachedDetalleIngresoListNew = new ArrayList<DetalleIngreso>();
            for (DetalleIngreso detalleIngresoListNewDetalleIngresoToAttach : detalleIngresoListNew) {
                detalleIngresoListNewDetalleIngresoToAttach = em.getReference(detalleIngresoListNewDetalleIngresoToAttach.getClass(), detalleIngresoListNewDetalleIngresoToAttach.getIddetalleIngreso());
                attachedDetalleIngresoListNew.add(detalleIngresoListNewDetalleIngresoToAttach);
            }
            detalleIngresoListNew = attachedDetalleIngresoListNew;
            articulo.setDetalleIngresoList(detalleIngresoListNew);
            articulo = em.merge(articulo);
            if (idcategoriaOld != null && !idcategoriaOld.equals(idcategoriaNew)) {
                idcategoriaOld.getArticuloList().remove(articulo);
                idcategoriaOld = em.merge(idcategoriaOld);
            }
            if (idcategoriaNew != null && !idcategoriaNew.equals(idcategoriaOld)) {
                idcategoriaNew.getArticuloList().add(articulo);
                idcategoriaNew = em.merge(idcategoriaNew);
            }
            for (DetalleVenta detalleVentaListNewDetalleVenta : detalleVentaListNew) {
                if (!detalleVentaListOld.contains(detalleVentaListNewDetalleVenta)) {
                    Articulo oldIdarticuloOfDetalleVentaListNewDetalleVenta = detalleVentaListNewDetalleVenta.getIdarticulo();
                    detalleVentaListNewDetalleVenta.setIdarticulo(articulo);
                    detalleVentaListNewDetalleVenta = em.merge(detalleVentaListNewDetalleVenta);
                    if (oldIdarticuloOfDetalleVentaListNewDetalleVenta != null && !oldIdarticuloOfDetalleVentaListNewDetalleVenta.equals(articulo)) {
                        oldIdarticuloOfDetalleVentaListNewDetalleVenta.getDetalleVentaList().remove(detalleVentaListNewDetalleVenta);
                        oldIdarticuloOfDetalleVentaListNewDetalleVenta = em.merge(oldIdarticuloOfDetalleVentaListNewDetalleVenta);
                    }
                }
            }
            for (DetalleIngreso detalleIngresoListNewDetalleIngreso : detalleIngresoListNew) {
                if (!detalleIngresoListOld.contains(detalleIngresoListNewDetalleIngreso)) {
                    Articulo oldIdarticuloOfDetalleIngresoListNewDetalleIngreso = detalleIngresoListNewDetalleIngreso.getIdarticulo();
                    detalleIngresoListNewDetalleIngreso.setIdarticulo(articulo);
                    detalleIngresoListNewDetalleIngreso = em.merge(detalleIngresoListNewDetalleIngreso);
                    if (oldIdarticuloOfDetalleIngresoListNewDetalleIngreso != null && !oldIdarticuloOfDetalleIngresoListNewDetalleIngreso.equals(articulo)) {
                        oldIdarticuloOfDetalleIngresoListNewDetalleIngreso.getDetalleIngresoList().remove(detalleIngresoListNewDetalleIngreso);
                        oldIdarticuloOfDetalleIngresoListNewDetalleIngreso = em.merge(oldIdarticuloOfDetalleIngresoListNewDetalleIngreso);
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
            List<DetalleVenta> detalleVentaListOrphanCheck = articulo.getDetalleVentaList();
            for (DetalleVenta detalleVentaListOrphanCheckDetalleVenta : detalleVentaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articulo (" + articulo + ") cannot be destroyed since the DetalleVenta " + detalleVentaListOrphanCheckDetalleVenta + " in its detalleVentaList field has a non-nullable idarticulo field.");
            }
            List<DetalleIngreso> detalleIngresoListOrphanCheck = articulo.getDetalleIngresoList();
            for (DetalleIngreso detalleIngresoListOrphanCheckDetalleIngreso : detalleIngresoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articulo (" + articulo + ") cannot be destroyed since the DetalleIngreso " + detalleIngresoListOrphanCheckDetalleIngreso + " in its detalleIngresoList field has a non-nullable idarticulo field.");
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
