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
import tallerFicsa.proyectoSVentas.entity.Persona;
import tallerFicsa.proyectoSVentas.entity.Usuario;
import tallerFicsa.proyectoSVentas.entity.DetalleVenta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import tallerFicsa.proyectoSVentas.controller.exceptions.IllegalOrphanException;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Venta;

/**
 *
 * @author Usuario
 */
public class VentaJpaController implements Serializable {

    public VentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Venta venta) {
        if (venta.getDetalleVentaList() == null) {
            venta.setDetalleVentaList(new ArrayList<DetalleVenta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona idcliente = venta.getIdcliente();
            if (idcliente != null) {
                idcliente = em.getReference(idcliente.getClass(), idcliente.getIdpersona());
                venta.setIdcliente(idcliente);
            }
            Usuario idusuario = venta.getIdusuario();
            if (idusuario != null) {
                idusuario = em.getReference(idusuario.getClass(), idusuario.getIdusuario());
                venta.setIdusuario(idusuario);
            }
            List<DetalleVenta> attachedDetalleVentaList = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListDetalleVentaToAttach : venta.getDetalleVentaList()) {
                detalleVentaListDetalleVentaToAttach = em.getReference(detalleVentaListDetalleVentaToAttach.getClass(), detalleVentaListDetalleVentaToAttach.getIddetalleVenta());
                attachedDetalleVentaList.add(detalleVentaListDetalleVentaToAttach);
            }
            venta.setDetalleVentaList(attachedDetalleVentaList);
            em.persist(venta);
            if (idcliente != null) {
                idcliente.getVentaList().add(venta);
                idcliente = em.merge(idcliente);
            }
            if (idusuario != null) {
                idusuario.getVentaList().add(venta);
                idusuario = em.merge(idusuario);
            }
            for (DetalleVenta detalleVentaListDetalleVenta : venta.getDetalleVentaList()) {
                Venta oldIdventaOfDetalleVentaListDetalleVenta = detalleVentaListDetalleVenta.getIdventa();
                detalleVentaListDetalleVenta.setIdventa(venta);
                detalleVentaListDetalleVenta = em.merge(detalleVentaListDetalleVenta);
                if (oldIdventaOfDetalleVentaListDetalleVenta != null) {
                    oldIdventaOfDetalleVentaListDetalleVenta.getDetalleVentaList().remove(detalleVentaListDetalleVenta);
                    oldIdventaOfDetalleVentaListDetalleVenta = em.merge(oldIdventaOfDetalleVentaListDetalleVenta);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Venta venta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta persistentVenta = em.find(Venta.class, venta.getIdventa());
            Persona idclienteOld = persistentVenta.getIdcliente();
            Persona idclienteNew = venta.getIdcliente();
            Usuario idusuarioOld = persistentVenta.getIdusuario();
            Usuario idusuarioNew = venta.getIdusuario();
            List<DetalleVenta> detalleVentaListOld = persistentVenta.getDetalleVentaList();
            List<DetalleVenta> detalleVentaListNew = venta.getDetalleVentaList();
            List<String> illegalOrphanMessages = null;
            for (DetalleVenta detalleVentaListOldDetalleVenta : detalleVentaListOld) {
                if (!detalleVentaListNew.contains(detalleVentaListOldDetalleVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleVenta " + detalleVentaListOldDetalleVenta + " since its idventa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idclienteNew != null) {
                idclienteNew = em.getReference(idclienteNew.getClass(), idclienteNew.getIdpersona());
                venta.setIdcliente(idclienteNew);
            }
            if (idusuarioNew != null) {
                idusuarioNew = em.getReference(idusuarioNew.getClass(), idusuarioNew.getIdusuario());
                venta.setIdusuario(idusuarioNew);
            }
            List<DetalleVenta> attachedDetalleVentaListNew = new ArrayList<DetalleVenta>();
            for (DetalleVenta detalleVentaListNewDetalleVentaToAttach : detalleVentaListNew) {
                detalleVentaListNewDetalleVentaToAttach = em.getReference(detalleVentaListNewDetalleVentaToAttach.getClass(), detalleVentaListNewDetalleVentaToAttach.getIddetalleVenta());
                attachedDetalleVentaListNew.add(detalleVentaListNewDetalleVentaToAttach);
            }
            detalleVentaListNew = attachedDetalleVentaListNew;
            venta.setDetalleVentaList(detalleVentaListNew);
            venta = em.merge(venta);
            if (idclienteOld != null && !idclienteOld.equals(idclienteNew)) {
                idclienteOld.getVentaList().remove(venta);
                idclienteOld = em.merge(idclienteOld);
            }
            if (idclienteNew != null && !idclienteNew.equals(idclienteOld)) {
                idclienteNew.getVentaList().add(venta);
                idclienteNew = em.merge(idclienteNew);
            }
            if (idusuarioOld != null && !idusuarioOld.equals(idusuarioNew)) {
                idusuarioOld.getVentaList().remove(venta);
                idusuarioOld = em.merge(idusuarioOld);
            }
            if (idusuarioNew != null && !idusuarioNew.equals(idusuarioOld)) {
                idusuarioNew.getVentaList().add(venta);
                idusuarioNew = em.merge(idusuarioNew);
            }
            for (DetalleVenta detalleVentaListNewDetalleVenta : detalleVentaListNew) {
                if (!detalleVentaListOld.contains(detalleVentaListNewDetalleVenta)) {
                    Venta oldIdventaOfDetalleVentaListNewDetalleVenta = detalleVentaListNewDetalleVenta.getIdventa();
                    detalleVentaListNewDetalleVenta.setIdventa(venta);
                    detalleVentaListNewDetalleVenta = em.merge(detalleVentaListNewDetalleVenta);
                    if (oldIdventaOfDetalleVentaListNewDetalleVenta != null && !oldIdventaOfDetalleVentaListNewDetalleVenta.equals(venta)) {
                        oldIdventaOfDetalleVentaListNewDetalleVenta.getDetalleVentaList().remove(detalleVentaListNewDetalleVenta);
                        oldIdventaOfDetalleVentaListNewDetalleVenta = em.merge(oldIdventaOfDetalleVentaListNewDetalleVenta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = venta.getIdventa();
                if (findVenta(id) == null) {
                    throw new NonexistentEntityException("The venta with id " + id + " no longer exists.");
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
            Venta venta;
            try {
                venta = em.getReference(Venta.class, id);
                venta.getIdventa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The venta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetalleVenta> detalleVentaListOrphanCheck = venta.getDetalleVentaList();
            for (DetalleVenta detalleVentaListOrphanCheckDetalleVenta : detalleVentaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the DetalleVenta " + detalleVentaListOrphanCheckDetalleVenta + " in its detalleVentaList field has a non-nullable idventa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Persona idcliente = venta.getIdcliente();
            if (idcliente != null) {
                idcliente.getVentaList().remove(venta);
                idcliente = em.merge(idcliente);
            }
            Usuario idusuario = venta.getIdusuario();
            if (idusuario != null) {
                idusuario.getVentaList().remove(venta);
                idusuario = em.merge(idusuario);
            }
            em.remove(venta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Venta> findVentaEntities() {
        return findVentaEntities(true, -1, -1);
    }

    public List<Venta> findVentaEntities(int maxResults, int firstResult) {
        return findVentaEntities(false, maxResults, firstResult);
    }

    private List<Venta> findVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Venta.class));
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

    public Venta findVenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Venta.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Venta> rt = cq.from(Venta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
