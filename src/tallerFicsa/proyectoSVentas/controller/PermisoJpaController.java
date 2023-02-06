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
import tallerFicsa.proyectoSVentas.entity.UsuarioPermiso;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import tallerFicsa.proyectoSVentas.controller.exceptions.IllegalOrphanException;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Permiso;

/**
 *
 * @author Usuario
 */
public class PermisoJpaController implements Serializable {

    public PermisoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Permiso permiso) {
        if (permiso.getUsuarioPermisoList() == null) {
            permiso.setUsuarioPermisoList(new ArrayList<UsuarioPermiso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<UsuarioPermiso> attachedUsuarioPermisoList = new ArrayList<UsuarioPermiso>();
            for (UsuarioPermiso usuarioPermisoListUsuarioPermisoToAttach : permiso.getUsuarioPermisoList()) {
                usuarioPermisoListUsuarioPermisoToAttach = em.getReference(usuarioPermisoListUsuarioPermisoToAttach.getClass(), usuarioPermisoListUsuarioPermisoToAttach.getIdusuarioPermiso());
                attachedUsuarioPermisoList.add(usuarioPermisoListUsuarioPermisoToAttach);
            }
            permiso.setUsuarioPermisoList(attachedUsuarioPermisoList);
            em.persist(permiso);
            for (UsuarioPermiso usuarioPermisoListUsuarioPermiso : permiso.getUsuarioPermisoList()) {
                Permiso oldIdpermisoOfUsuarioPermisoListUsuarioPermiso = usuarioPermisoListUsuarioPermiso.getIdpermiso();
                usuarioPermisoListUsuarioPermiso.setIdpermiso(permiso);
                usuarioPermisoListUsuarioPermiso = em.merge(usuarioPermisoListUsuarioPermiso);
                if (oldIdpermisoOfUsuarioPermisoListUsuarioPermiso != null) {
                    oldIdpermisoOfUsuarioPermisoListUsuarioPermiso.getUsuarioPermisoList().remove(usuarioPermisoListUsuarioPermiso);
                    oldIdpermisoOfUsuarioPermisoListUsuarioPermiso = em.merge(oldIdpermisoOfUsuarioPermisoListUsuarioPermiso);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Permiso permiso) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Permiso persistentPermiso = em.find(Permiso.class, permiso.getIdpermiso());
            List<UsuarioPermiso> usuarioPermisoListOld = persistentPermiso.getUsuarioPermisoList();
            List<UsuarioPermiso> usuarioPermisoListNew = permiso.getUsuarioPermisoList();
            List<String> illegalOrphanMessages = null;
            for (UsuarioPermiso usuarioPermisoListOldUsuarioPermiso : usuarioPermisoListOld) {
                if (!usuarioPermisoListNew.contains(usuarioPermisoListOldUsuarioPermiso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UsuarioPermiso " + usuarioPermisoListOldUsuarioPermiso + " since its idpermiso field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<UsuarioPermiso> attachedUsuarioPermisoListNew = new ArrayList<UsuarioPermiso>();
            for (UsuarioPermiso usuarioPermisoListNewUsuarioPermisoToAttach : usuarioPermisoListNew) {
                usuarioPermisoListNewUsuarioPermisoToAttach = em.getReference(usuarioPermisoListNewUsuarioPermisoToAttach.getClass(), usuarioPermisoListNewUsuarioPermisoToAttach.getIdusuarioPermiso());
                attachedUsuarioPermisoListNew.add(usuarioPermisoListNewUsuarioPermisoToAttach);
            }
            usuarioPermisoListNew = attachedUsuarioPermisoListNew;
            permiso.setUsuarioPermisoList(usuarioPermisoListNew);
            permiso = em.merge(permiso);
            for (UsuarioPermiso usuarioPermisoListNewUsuarioPermiso : usuarioPermisoListNew) {
                if (!usuarioPermisoListOld.contains(usuarioPermisoListNewUsuarioPermiso)) {
                    Permiso oldIdpermisoOfUsuarioPermisoListNewUsuarioPermiso = usuarioPermisoListNewUsuarioPermiso.getIdpermiso();
                    usuarioPermisoListNewUsuarioPermiso.setIdpermiso(permiso);
                    usuarioPermisoListNewUsuarioPermiso = em.merge(usuarioPermisoListNewUsuarioPermiso);
                    if (oldIdpermisoOfUsuarioPermisoListNewUsuarioPermiso != null && !oldIdpermisoOfUsuarioPermisoListNewUsuarioPermiso.equals(permiso)) {
                        oldIdpermisoOfUsuarioPermisoListNewUsuarioPermiso.getUsuarioPermisoList().remove(usuarioPermisoListNewUsuarioPermiso);
                        oldIdpermisoOfUsuarioPermisoListNewUsuarioPermiso = em.merge(oldIdpermisoOfUsuarioPermisoListNewUsuarioPermiso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = permiso.getIdpermiso();
                if (findPermiso(id) == null) {
                    throw new NonexistentEntityException("The permiso with id " + id + " no longer exists.");
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
            Permiso permiso;
            try {
                permiso = em.getReference(Permiso.class, id);
                permiso.getIdpermiso();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The permiso with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UsuarioPermiso> usuarioPermisoListOrphanCheck = permiso.getUsuarioPermisoList();
            for (UsuarioPermiso usuarioPermisoListOrphanCheckUsuarioPermiso : usuarioPermisoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Permiso (" + permiso + ") cannot be destroyed since the UsuarioPermiso " + usuarioPermisoListOrphanCheckUsuarioPermiso + " in its usuarioPermisoList field has a non-nullable idpermiso field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(permiso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Permiso> findPermisoEntities() {
        return findPermisoEntities(true, -1, -1);
    }

    public List<Permiso> findPermisoEntities(int maxResults, int firstResult) {
        return findPermisoEntities(false, maxResults, firstResult);
    }

    private List<Permiso> findPermisoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Permiso.class));
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

    public Permiso findPermiso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Permiso.class, id);
        } finally {
            em.close();
        }
    }

    public int getPermisoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Permiso> rt = cq.from(Permiso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
