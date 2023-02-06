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
import tallerFicsa.proyectoSVentas.entity.Usuario;
import tallerFicsa.proyectoSVentas.entity.Permiso;
import tallerFicsa.proyectoSVentas.entity.UsuarioPermiso;

/**
 *
 * @author Usuario
 */
public class UsuarioPermisoJpaController implements Serializable {

    public UsuarioPermisoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UsuarioPermiso usuarioPermiso) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idusuario = usuarioPermiso.getIdusuario();
            if (idusuario != null) {
                idusuario = em.getReference(idusuario.getClass(), idusuario.getIdusuario());
                usuarioPermiso.setIdusuario(idusuario);
            }
            Permiso idpermiso = usuarioPermiso.getIdpermiso();
            if (idpermiso != null) {
                idpermiso = em.getReference(idpermiso.getClass(), idpermiso.getIdpermiso());
                usuarioPermiso.setIdpermiso(idpermiso);
            }
            em.persist(usuarioPermiso);
            if (idusuario != null) {
                idusuario.getUsuarioPermisoList().add(usuarioPermiso);
                idusuario = em.merge(idusuario);
            }
            if (idpermiso != null) {
                idpermiso.getUsuarioPermisoList().add(usuarioPermiso);
                idpermiso = em.merge(idpermiso);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UsuarioPermiso usuarioPermiso) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UsuarioPermiso persistentUsuarioPermiso = em.find(UsuarioPermiso.class, usuarioPermiso.getIdusuarioPermiso());
            Usuario idusuarioOld = persistentUsuarioPermiso.getIdusuario();
            Usuario idusuarioNew = usuarioPermiso.getIdusuario();
            Permiso idpermisoOld = persistentUsuarioPermiso.getIdpermiso();
            Permiso idpermisoNew = usuarioPermiso.getIdpermiso();
            if (idusuarioNew != null) {
                idusuarioNew = em.getReference(idusuarioNew.getClass(), idusuarioNew.getIdusuario());
                usuarioPermiso.setIdusuario(idusuarioNew);
            }
            if (idpermisoNew != null) {
                idpermisoNew = em.getReference(idpermisoNew.getClass(), idpermisoNew.getIdpermiso());
                usuarioPermiso.setIdpermiso(idpermisoNew);
            }
            usuarioPermiso = em.merge(usuarioPermiso);
            if (idusuarioOld != null && !idusuarioOld.equals(idusuarioNew)) {
                idusuarioOld.getUsuarioPermisoList().remove(usuarioPermiso);
                idusuarioOld = em.merge(idusuarioOld);
            }
            if (idusuarioNew != null && !idusuarioNew.equals(idusuarioOld)) {
                idusuarioNew.getUsuarioPermisoList().add(usuarioPermiso);
                idusuarioNew = em.merge(idusuarioNew);
            }
            if (idpermisoOld != null && !idpermisoOld.equals(idpermisoNew)) {
                idpermisoOld.getUsuarioPermisoList().remove(usuarioPermiso);
                idpermisoOld = em.merge(idpermisoOld);
            }
            if (idpermisoNew != null && !idpermisoNew.equals(idpermisoOld)) {
                idpermisoNew.getUsuarioPermisoList().add(usuarioPermiso);
                idpermisoNew = em.merge(idpermisoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuarioPermiso.getIdusuarioPermiso();
                if (findUsuarioPermiso(id) == null) {
                    throw new NonexistentEntityException("The usuarioPermiso with id " + id + " no longer exists.");
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
            UsuarioPermiso usuarioPermiso;
            try {
                usuarioPermiso = em.getReference(UsuarioPermiso.class, id);
                usuarioPermiso.getIdusuarioPermiso();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarioPermiso with id " + id + " no longer exists.", enfe);
            }
            Usuario idusuario = usuarioPermiso.getIdusuario();
            if (idusuario != null) {
                idusuario.getUsuarioPermisoList().remove(usuarioPermiso);
                idusuario = em.merge(idusuario);
            }
            Permiso idpermiso = usuarioPermiso.getIdpermiso();
            if (idpermiso != null) {
                idpermiso.getUsuarioPermisoList().remove(usuarioPermiso);
                idpermiso = em.merge(idpermiso);
            }
            em.remove(usuarioPermiso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UsuarioPermiso> findUsuarioPermisoEntities() {
        return findUsuarioPermisoEntities(true, -1, -1);
    }

    public List<UsuarioPermiso> findUsuarioPermisoEntities(int maxResults, int firstResult) {
        return findUsuarioPermisoEntities(false, maxResults, firstResult);
    }

    private List<UsuarioPermiso> findUsuarioPermisoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UsuarioPermiso.class));
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

    public UsuarioPermiso findUsuarioPermiso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UsuarioPermiso.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioPermisoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UsuarioPermiso> rt = cq.from(UsuarioPermiso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
