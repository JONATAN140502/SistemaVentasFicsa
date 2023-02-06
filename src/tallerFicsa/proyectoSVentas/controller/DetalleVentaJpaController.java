/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tallerFicsa.proyectoSVentas.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Articulo;
import tallerFicsa.proyectoSVentas.entity.DetalleVenta;
import tallerFicsa.proyectoSVentas.entity.Venta;

/**
 *
 * @author Usuario
 */
public class DetalleVentaJpaController implements Serializable {

    public DetalleVentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalleVenta detalleVenta) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulo idarticulo = detalleVenta.getIdarticulo();
            if (idarticulo != null) {
                idarticulo = em.getReference(idarticulo.getClass(), idarticulo.getIdarticulo());
                detalleVenta.setIdarticulo(idarticulo);
            }
            Venta idventa = detalleVenta.getIdventa();
            if (idventa != null) {
                idventa = em.getReference(idventa.getClass(), idventa.getIdventa());
                detalleVenta.setIdventa(idventa);
            }
            em.persist(detalleVenta);
            if (idarticulo != null) {
                idarticulo.getDetalleVentaList().add(detalleVenta);
                idarticulo = em.merge(idarticulo);
            }
            if (idventa != null) {
                idventa.getDetalleVentaList().add(detalleVenta);
                idventa = em.merge(idventa);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalleVenta detalleVenta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleVenta persistentDetalleVenta = em.find(DetalleVenta.class, detalleVenta.getIddetalleVenta());
            Articulo idarticuloOld = persistentDetalleVenta.getIdarticulo();
            Articulo idarticuloNew = detalleVenta.getIdarticulo();
            Venta idventaOld = persistentDetalleVenta.getIdventa();
            Venta idventaNew = detalleVenta.getIdventa();
            if (idarticuloNew != null) {
                idarticuloNew = em.getReference(idarticuloNew.getClass(), idarticuloNew.getIdarticulo());
                detalleVenta.setIdarticulo(idarticuloNew);
            }
            if (idventaNew != null) {
                idventaNew = em.getReference(idventaNew.getClass(), idventaNew.getIdventa());
                detalleVenta.setIdventa(idventaNew);
            }
            detalleVenta = em.merge(detalleVenta);
            if (idarticuloOld != null && !idarticuloOld.equals(idarticuloNew)) {
                idarticuloOld.getDetalleVentaList().remove(detalleVenta);
                idarticuloOld = em.merge(idarticuloOld);
            }
            if (idarticuloNew != null && !idarticuloNew.equals(idarticuloOld)) {
                idarticuloNew.getDetalleVentaList().add(detalleVenta);
                idarticuloNew = em.merge(idarticuloNew);
            }
            if (idventaOld != null && !idventaOld.equals(idventaNew)) {
                idventaOld.getDetalleVentaList().remove(detalleVenta);
                idventaOld = em.merge(idventaOld);
            }
            if (idventaNew != null && !idventaNew.equals(idventaOld)) {
                idventaNew.getDetalleVentaList().add(detalleVenta);
                idventaNew = em.merge(idventaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleVenta.getIddetalleVenta();
                if (findDetalleVenta(id) == null) {
                    throw new NonexistentEntityException("The detalleVenta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleVenta detalleVenta;
            try {
                detalleVenta = em.getReference(DetalleVenta.class, id);
                detalleVenta.getIddetalleVenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleVenta with id " + id + " no longer exists.", enfe);
            }
            Articulo idarticulo = detalleVenta.getIdarticulo();
            if (idarticulo != null) {
                idarticulo.getDetalleVentaList().remove(detalleVenta);
                idarticulo = em.merge(idarticulo);
            }
            Venta idventa = detalleVenta.getIdventa();
            if (idventa != null) {
                idventa.getDetalleVentaList().remove(detalleVenta);
                idventa = em.merge(idventa);
            }
            em.remove(detalleVenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalleVenta> findDetalleVentaEntities() {
        return findDetalleVentaEntities(true, -1, -1);
    }

    public List<DetalleVenta> findDetalleVentaEntities(int maxResults, int firstResult) {
        return findDetalleVentaEntities(false, maxResults, firstResult);
    }

    private List<DetalleVenta> findDetalleVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleVenta.class));
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

    public DetalleVenta findDetalleVenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleVenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleVenta> rt = cq.from(DetalleVenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
