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
import tallerFicsa.proyectoSVentas.entity.DetalleIngreso;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import tallerFicsa.proyectoSVentas.controller.exceptions.IllegalOrphanException;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Ingreso;

/**
 *
 * @author Usuario
 */
public class IngresoJpaController implements Serializable {

    public IngresoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ingreso ingreso) {
        if (ingreso.getDetalleIngresoList() == null) {
            ingreso.setDetalleIngresoList(new ArrayList<DetalleIngreso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona idproveedor = ingreso.getIdproveedor();
            if (idproveedor != null) {
                idproveedor = em.getReference(idproveedor.getClass(), idproveedor.getIdpersona());
                ingreso.setIdproveedor(idproveedor);
            }
            Usuario idusuario = ingreso.getIdusuario();
            if (idusuario != null) {
                idusuario = em.getReference(idusuario.getClass(), idusuario.getIdusuario());
                ingreso.setIdusuario(idusuario);
            }
            List<DetalleIngreso> attachedDetalleIngresoList = new ArrayList<DetalleIngreso>();
            for (DetalleIngreso detalleIngresoListDetalleIngresoToAttach : ingreso.getDetalleIngresoList()) {
                detalleIngresoListDetalleIngresoToAttach = em.getReference(detalleIngresoListDetalleIngresoToAttach.getClass(), detalleIngresoListDetalleIngresoToAttach.getIddetalleIngreso());
                attachedDetalleIngresoList.add(detalleIngresoListDetalleIngresoToAttach);
            }
            ingreso.setDetalleIngresoList(attachedDetalleIngresoList);
            em.persist(ingreso);
            if (idproveedor != null) {
                idproveedor.getIngresoList().add(ingreso);
                idproveedor = em.merge(idproveedor);
            }
            if (idusuario != null) {
                idusuario.getIngresoList().add(ingreso);
                idusuario = em.merge(idusuario);
            }
            for (DetalleIngreso detalleIngresoListDetalleIngreso : ingreso.getDetalleIngresoList()) {
                Ingreso oldIdingresoOfDetalleIngresoListDetalleIngreso = detalleIngresoListDetalleIngreso.getIdingreso();
                detalleIngresoListDetalleIngreso.setIdingreso(ingreso);
                detalleIngresoListDetalleIngreso = em.merge(detalleIngresoListDetalleIngreso);
                if (oldIdingresoOfDetalleIngresoListDetalleIngreso != null) {
                    oldIdingresoOfDetalleIngresoListDetalleIngreso.getDetalleIngresoList().remove(detalleIngresoListDetalleIngreso);
                    oldIdingresoOfDetalleIngresoListDetalleIngreso = em.merge(oldIdingresoOfDetalleIngresoListDetalleIngreso);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ingreso ingreso) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ingreso persistentIngreso = em.find(Ingreso.class, ingreso.getIdingreso());
            Persona idproveedorOld = persistentIngreso.getIdproveedor();
            Persona idproveedorNew = ingreso.getIdproveedor();
            Usuario idusuarioOld = persistentIngreso.getIdusuario();
            Usuario idusuarioNew = ingreso.getIdusuario();
            List<DetalleIngreso> detalleIngresoListOld = persistentIngreso.getDetalleIngresoList();
            List<DetalleIngreso> detalleIngresoListNew = ingreso.getDetalleIngresoList();
            List<String> illegalOrphanMessages = null;
            for (DetalleIngreso detalleIngresoListOldDetalleIngreso : detalleIngresoListOld) {
                if (!detalleIngresoListNew.contains(detalleIngresoListOldDetalleIngreso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleIngreso " + detalleIngresoListOldDetalleIngreso + " since its idingreso field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idproveedorNew != null) {
                idproveedorNew = em.getReference(idproveedorNew.getClass(), idproveedorNew.getIdpersona());
                ingreso.setIdproveedor(idproveedorNew);
            }
            if (idusuarioNew != null) {
                idusuarioNew = em.getReference(idusuarioNew.getClass(), idusuarioNew.getIdusuario());
                ingreso.setIdusuario(idusuarioNew);
            }
            List<DetalleIngreso> attachedDetalleIngresoListNew = new ArrayList<DetalleIngreso>();
            for (DetalleIngreso detalleIngresoListNewDetalleIngresoToAttach : detalleIngresoListNew) {
                detalleIngresoListNewDetalleIngresoToAttach = em.getReference(detalleIngresoListNewDetalleIngresoToAttach.getClass(), detalleIngresoListNewDetalleIngresoToAttach.getIddetalleIngreso());
                attachedDetalleIngresoListNew.add(detalleIngresoListNewDetalleIngresoToAttach);
            }
            detalleIngresoListNew = attachedDetalleIngresoListNew;
            ingreso.setDetalleIngresoList(detalleIngresoListNew);
            ingreso = em.merge(ingreso);
            if (idproveedorOld != null && !idproveedorOld.equals(idproveedorNew)) {
                idproveedorOld.getIngresoList().remove(ingreso);
                idproveedorOld = em.merge(idproveedorOld);
            }
            if (idproveedorNew != null && !idproveedorNew.equals(idproveedorOld)) {
                idproveedorNew.getIngresoList().add(ingreso);
                idproveedorNew = em.merge(idproveedorNew);
            }
            if (idusuarioOld != null && !idusuarioOld.equals(idusuarioNew)) {
                idusuarioOld.getIngresoList().remove(ingreso);
                idusuarioOld = em.merge(idusuarioOld);
            }
            if (idusuarioNew != null && !idusuarioNew.equals(idusuarioOld)) {
                idusuarioNew.getIngresoList().add(ingreso);
                idusuarioNew = em.merge(idusuarioNew);
            }
            for (DetalleIngreso detalleIngresoListNewDetalleIngreso : detalleIngresoListNew) {
                if (!detalleIngresoListOld.contains(detalleIngresoListNewDetalleIngreso)) {
                    Ingreso oldIdingresoOfDetalleIngresoListNewDetalleIngreso = detalleIngresoListNewDetalleIngreso.getIdingreso();
                    detalleIngresoListNewDetalleIngreso.setIdingreso(ingreso);
                    detalleIngresoListNewDetalleIngreso = em.merge(detalleIngresoListNewDetalleIngreso);
                    if (oldIdingresoOfDetalleIngresoListNewDetalleIngreso != null && !oldIdingresoOfDetalleIngresoListNewDetalleIngreso.equals(ingreso)) {
                        oldIdingresoOfDetalleIngresoListNewDetalleIngreso.getDetalleIngresoList().remove(detalleIngresoListNewDetalleIngreso);
                        oldIdingresoOfDetalleIngresoListNewDetalleIngreso = em.merge(oldIdingresoOfDetalleIngresoListNewDetalleIngreso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ingreso.getIdingreso();
                if (findIngreso(id) == null) {
                    throw new NonexistentEntityException("The ingreso with id " + id + " no longer exists.");
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
            Ingreso ingreso;
            try {
                ingreso = em.getReference(Ingreso.class, id);
                ingreso.getIdingreso();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ingreso with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetalleIngreso> detalleIngresoListOrphanCheck = ingreso.getDetalleIngresoList();
            for (DetalleIngreso detalleIngresoListOrphanCheckDetalleIngreso : detalleIngresoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ingreso (" + ingreso + ") cannot be destroyed since the DetalleIngreso " + detalleIngresoListOrphanCheckDetalleIngreso + " in its detalleIngresoList field has a non-nullable idingreso field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Persona idproveedor = ingreso.getIdproveedor();
            if (idproveedor != null) {
                idproveedor.getIngresoList().remove(ingreso);
                idproveedor = em.merge(idproveedor);
            }
            Usuario idusuario = ingreso.getIdusuario();
            if (idusuario != null) {
                idusuario.getIngresoList().remove(ingreso);
                idusuario = em.merge(idusuario);
            }
            em.remove(ingreso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ingreso> findIngresoEntities() {
        return findIngresoEntities(true, -1, -1);
    }

    public List<Ingreso> findIngresoEntities(int maxResults, int firstResult) {
        return findIngresoEntities(false, maxResults, firstResult);
    }

    private List<Ingreso> findIngresoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ingreso.class));
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

    public Ingreso findIngreso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ingreso.class, id);
        } finally {
            em.close();
        }
    }

    public int getIngresoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ingreso> rt = cq.from(Ingreso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
