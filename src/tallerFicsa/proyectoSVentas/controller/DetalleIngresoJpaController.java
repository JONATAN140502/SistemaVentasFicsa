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
import tallerFicsa.proyectoSVentas.entity.DetalleIngreso;
import tallerFicsa.proyectoSVentas.entity.Ingreso;

/**
 *
 * @author Usuario
 */
public class DetalleIngresoJpaController implements Serializable {

    public DetalleIngresoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalleIngreso detalleIngreso) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articulo idarticulo = detalleIngreso.getIdarticulo();
            if (idarticulo != null) {
                idarticulo = em.getReference(idarticulo.getClass(), idarticulo.getIdarticulo());
                detalleIngreso.setIdarticulo(idarticulo);
            }
            Ingreso idingreso = detalleIngreso.getIdingreso();
            if (idingreso != null) {
                idingreso = em.getReference(idingreso.getClass(), idingreso.getIdingreso());
                detalleIngreso.setIdingreso(idingreso);
            }
            em.persist(detalleIngreso);
            if (idarticulo != null) {
                idarticulo.getDetalleIngresoList().add(detalleIngreso);
                idarticulo = em.merge(idarticulo);
            }
            if (idingreso != null) {
                idingreso.getDetalleIngresoList().add(detalleIngreso);
                idingreso = em.merge(idingreso);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalleIngreso detalleIngreso) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleIngreso persistentDetalleIngreso = em.find(DetalleIngreso.class, detalleIngreso.getIddetalleIngreso());
            Articulo idarticuloOld = persistentDetalleIngreso.getIdarticulo();
            Articulo idarticuloNew = detalleIngreso.getIdarticulo();
            Ingreso idingresoOld = persistentDetalleIngreso.getIdingreso();
            Ingreso idingresoNew = detalleIngreso.getIdingreso();
            if (idarticuloNew != null) {
                idarticuloNew = em.getReference(idarticuloNew.getClass(), idarticuloNew.getIdarticulo());
                detalleIngreso.setIdarticulo(idarticuloNew);
            }
            if (idingresoNew != null) {
                idingresoNew = em.getReference(idingresoNew.getClass(), idingresoNew.getIdingreso());
                detalleIngreso.setIdingreso(idingresoNew);
            }
            detalleIngreso = em.merge(detalleIngreso);
            if (idarticuloOld != null && !idarticuloOld.equals(idarticuloNew)) {
                idarticuloOld.getDetalleIngresoList().remove(detalleIngreso);
                idarticuloOld = em.merge(idarticuloOld);
            }
            if (idarticuloNew != null && !idarticuloNew.equals(idarticuloOld)) {
                idarticuloNew.getDetalleIngresoList().add(detalleIngreso);
                idarticuloNew = em.merge(idarticuloNew);
            }
            if (idingresoOld != null && !idingresoOld.equals(idingresoNew)) {
                idingresoOld.getDetalleIngresoList().remove(detalleIngreso);
                idingresoOld = em.merge(idingresoOld);
            }
            if (idingresoNew != null && !idingresoNew.equals(idingresoOld)) {
                idingresoNew.getDetalleIngresoList().add(detalleIngreso);
                idingresoNew = em.merge(idingresoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleIngreso.getIddetalleIngreso();
                if (findDetalleIngreso(id) == null) {
                    throw new NonexistentEntityException("The detalleIngreso with id " + id + " no longer exists.");
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
            DetalleIngreso detalleIngreso;
            try {
                detalleIngreso = em.getReference(DetalleIngreso.class, id);
                detalleIngreso.getIddetalleIngreso();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleIngreso with id " + id + " no longer exists.", enfe);
            }
            Articulo idarticulo = detalleIngreso.getIdarticulo();
            if (idarticulo != null) {
                idarticulo.getDetalleIngresoList().remove(detalleIngreso);
                idarticulo = em.merge(idarticulo);
            }
            Ingreso idingreso = detalleIngreso.getIdingreso();
            if (idingreso != null) {
                idingreso.getDetalleIngresoList().remove(detalleIngreso);
                idingreso = em.merge(idingreso);
            }
            em.remove(detalleIngreso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalleIngreso> findDetalleIngresoEntities() {
        return findDetalleIngresoEntities(true, -1, -1);
    }

    public List<DetalleIngreso> findDetalleIngresoEntities(int maxResults, int firstResult) {
        return findDetalleIngresoEntities(false, maxResults, firstResult);
    }

    private List<DetalleIngreso> findDetalleIngresoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleIngreso.class));
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

    public DetalleIngreso findDetalleIngreso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleIngreso.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleIngresoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleIngreso> rt = cq.from(DetalleIngreso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
