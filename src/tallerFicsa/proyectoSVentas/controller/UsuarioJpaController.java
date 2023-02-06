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
import tallerFicsa.proyectoSVentas.entity.Venta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import tallerFicsa.proyectoSVentas.controller.exceptions.IllegalOrphanException;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Ingreso;
import tallerFicsa.proyectoSVentas.entity.Usuario;
import tallerFicsa.proyectoSVentas.entity.UsuarioPermiso;

/**
 *
 * @author Usuario
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public UsuarioJpaController() {
        this.emf = Persistence.createEntityManagerFactory("ProyectoSVentasPU");
    }

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getVentaList() == null) {
            usuario.setVentaList(new ArrayList<Venta>());
        }
        if (usuario.getIngresoList() == null) {
            usuario.setIngresoList(new ArrayList<Ingreso>());
        }
        if (usuario.getUsuarioPermisoList() == null) {
            usuario.setUsuarioPermisoList(new ArrayList<UsuarioPermiso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Venta> attachedVentaList = new ArrayList<Venta>();
            for (Venta ventaListVentaToAttach : usuario.getVentaList()) {
                ventaListVentaToAttach = em.getReference(ventaListVentaToAttach.getClass(), ventaListVentaToAttach.getIdventa());
                attachedVentaList.add(ventaListVentaToAttach);
            }
            usuario.setVentaList(attachedVentaList);
            List<Ingreso> attachedIngresoList = new ArrayList<Ingreso>();
            for (Ingreso ingresoListIngresoToAttach : usuario.getIngresoList()) {
                ingresoListIngresoToAttach = em.getReference(ingresoListIngresoToAttach.getClass(), ingresoListIngresoToAttach.getIdingreso());
                attachedIngresoList.add(ingresoListIngresoToAttach);
            }
            usuario.setIngresoList(attachedIngresoList);
            List<UsuarioPermiso> attachedUsuarioPermisoList = new ArrayList<UsuarioPermiso>();
            for (UsuarioPermiso usuarioPermisoListUsuarioPermisoToAttach : usuario.getUsuarioPermisoList()) {
                usuarioPermisoListUsuarioPermisoToAttach = em.getReference(usuarioPermisoListUsuarioPermisoToAttach.getClass(), usuarioPermisoListUsuarioPermisoToAttach.getIdusuarioPermiso());
                attachedUsuarioPermisoList.add(usuarioPermisoListUsuarioPermisoToAttach);
            }
            usuario.setUsuarioPermisoList(attachedUsuarioPermisoList);
            em.persist(usuario);
            for (Venta ventaListVenta : usuario.getVentaList()) {
                Usuario oldIdusuarioOfVentaListVenta = ventaListVenta.getIdusuario();
                ventaListVenta.setIdusuario(usuario);
                ventaListVenta = em.merge(ventaListVenta);
                if (oldIdusuarioOfVentaListVenta != null) {
                    oldIdusuarioOfVentaListVenta.getVentaList().remove(ventaListVenta);
                    oldIdusuarioOfVentaListVenta = em.merge(oldIdusuarioOfVentaListVenta);
                }
            }
            for (Ingreso ingresoListIngreso : usuario.getIngresoList()) {
                Usuario oldIdusuarioOfIngresoListIngreso = ingresoListIngreso.getIdusuario();
                ingresoListIngreso.setIdusuario(usuario);
                ingresoListIngreso = em.merge(ingresoListIngreso);
                if (oldIdusuarioOfIngresoListIngreso != null) {
                    oldIdusuarioOfIngresoListIngreso.getIngresoList().remove(ingresoListIngreso);
                    oldIdusuarioOfIngresoListIngreso = em.merge(oldIdusuarioOfIngresoListIngreso);
                }
            }
            for (UsuarioPermiso usuarioPermisoListUsuarioPermiso : usuario.getUsuarioPermisoList()) {
                Usuario oldIdusuarioOfUsuarioPermisoListUsuarioPermiso = usuarioPermisoListUsuarioPermiso.getIdusuario();
                usuarioPermisoListUsuarioPermiso.setIdusuario(usuario);
                usuarioPermisoListUsuarioPermiso = em.merge(usuarioPermisoListUsuarioPermiso);
                if (oldIdusuarioOfUsuarioPermisoListUsuarioPermiso != null) {
                    oldIdusuarioOfUsuarioPermisoListUsuarioPermiso.getUsuarioPermisoList().remove(usuarioPermisoListUsuarioPermiso);
                    oldIdusuarioOfUsuarioPermisoListUsuarioPermiso = em.merge(oldIdusuarioOfUsuarioPermisoListUsuarioPermiso);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdusuario());
            List<Venta> ventaListOld = persistentUsuario.getVentaList();
            List<Venta> ventaListNew = usuario.getVentaList();
            List<Ingreso> ingresoListOld = persistentUsuario.getIngresoList();
            List<Ingreso> ingresoListNew = usuario.getIngresoList();
            List<UsuarioPermiso> usuarioPermisoListOld = persistentUsuario.getUsuarioPermisoList();
            List<UsuarioPermiso> usuarioPermisoListNew = usuario.getUsuarioPermisoList();
            List<String> illegalOrphanMessages = null;
            for (Venta ventaListOldVenta : ventaListOld) {
                if (!ventaListNew.contains(ventaListOldVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Venta " + ventaListOldVenta + " since its idusuario field is not nullable.");
                }
            }
            for (UsuarioPermiso usuarioPermisoListOldUsuarioPermiso : usuarioPermisoListOld) {
                if (!usuarioPermisoListNew.contains(usuarioPermisoListOldUsuarioPermiso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UsuarioPermiso " + usuarioPermisoListOldUsuarioPermiso + " since its idusuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Venta> attachedVentaListNew = new ArrayList<Venta>();
            for (Venta ventaListNewVentaToAttach : ventaListNew) {
                ventaListNewVentaToAttach = em.getReference(ventaListNewVentaToAttach.getClass(), ventaListNewVentaToAttach.getIdventa());
                attachedVentaListNew.add(ventaListNewVentaToAttach);
            }
            ventaListNew = attachedVentaListNew;
            usuario.setVentaList(ventaListNew);
            List<Ingreso> attachedIngresoListNew = new ArrayList<Ingreso>();
            for (Ingreso ingresoListNewIngresoToAttach : ingresoListNew) {
                ingresoListNewIngresoToAttach = em.getReference(ingresoListNewIngresoToAttach.getClass(), ingresoListNewIngresoToAttach.getIdingreso());
                attachedIngresoListNew.add(ingresoListNewIngresoToAttach);
            }
            ingresoListNew = attachedIngresoListNew;
            usuario.setIngresoList(ingresoListNew);
            List<UsuarioPermiso> attachedUsuarioPermisoListNew = new ArrayList<UsuarioPermiso>();
            for (UsuarioPermiso usuarioPermisoListNewUsuarioPermisoToAttach : usuarioPermisoListNew) {
                usuarioPermisoListNewUsuarioPermisoToAttach = em.getReference(usuarioPermisoListNewUsuarioPermisoToAttach.getClass(), usuarioPermisoListNewUsuarioPermisoToAttach.getIdusuarioPermiso());
                attachedUsuarioPermisoListNew.add(usuarioPermisoListNewUsuarioPermisoToAttach);
            }
            usuarioPermisoListNew = attachedUsuarioPermisoListNew;
            usuario.setUsuarioPermisoList(usuarioPermisoListNew);
            usuario = em.merge(usuario);
            for (Venta ventaListNewVenta : ventaListNew) {
                if (!ventaListOld.contains(ventaListNewVenta)) {
                    Usuario oldIdusuarioOfVentaListNewVenta = ventaListNewVenta.getIdusuario();
                    ventaListNewVenta.setIdusuario(usuario);
                    ventaListNewVenta = em.merge(ventaListNewVenta);
                    if (oldIdusuarioOfVentaListNewVenta != null && !oldIdusuarioOfVentaListNewVenta.equals(usuario)) {
                        oldIdusuarioOfVentaListNewVenta.getVentaList().remove(ventaListNewVenta);
                        oldIdusuarioOfVentaListNewVenta = em.merge(oldIdusuarioOfVentaListNewVenta);
                    }
                }
            }
            for (Ingreso ingresoListOldIngreso : ingresoListOld) {
                if (!ingresoListNew.contains(ingresoListOldIngreso)) {
                    ingresoListOldIngreso.setIdusuario(null);
                    ingresoListOldIngreso = em.merge(ingresoListOldIngreso);
                }
            }
            for (Ingreso ingresoListNewIngreso : ingresoListNew) {
                if (!ingresoListOld.contains(ingresoListNewIngreso)) {
                    Usuario oldIdusuarioOfIngresoListNewIngreso = ingresoListNewIngreso.getIdusuario();
                    ingresoListNewIngreso.setIdusuario(usuario);
                    ingresoListNewIngreso = em.merge(ingresoListNewIngreso);
                    if (oldIdusuarioOfIngresoListNewIngreso != null && !oldIdusuarioOfIngresoListNewIngreso.equals(usuario)) {
                        oldIdusuarioOfIngresoListNewIngreso.getIngresoList().remove(ingresoListNewIngreso);
                        oldIdusuarioOfIngresoListNewIngreso = em.merge(oldIdusuarioOfIngresoListNewIngreso);
                    }
                }
            }
            for (UsuarioPermiso usuarioPermisoListNewUsuarioPermiso : usuarioPermisoListNew) {
                if (!usuarioPermisoListOld.contains(usuarioPermisoListNewUsuarioPermiso)) {
                    Usuario oldIdusuarioOfUsuarioPermisoListNewUsuarioPermiso = usuarioPermisoListNewUsuarioPermiso.getIdusuario();
                    usuarioPermisoListNewUsuarioPermiso.setIdusuario(usuario);
                    usuarioPermisoListNewUsuarioPermiso = em.merge(usuarioPermisoListNewUsuarioPermiso);
                    if (oldIdusuarioOfUsuarioPermisoListNewUsuarioPermiso != null && !oldIdusuarioOfUsuarioPermisoListNewUsuarioPermiso.equals(usuario)) {
                        oldIdusuarioOfUsuarioPermisoListNewUsuarioPermiso.getUsuarioPermisoList().remove(usuarioPermisoListNewUsuarioPermiso);
                        oldIdusuarioOfUsuarioPermisoListNewUsuarioPermiso = em.merge(oldIdusuarioOfUsuarioPermisoListNewUsuarioPermiso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdusuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdusuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Venta> ventaListOrphanCheck = usuario.getVentaList();
            for (Venta ventaListOrphanCheckVenta : ventaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Venta " + ventaListOrphanCheckVenta + " in its ventaList field has a non-nullable idusuario field.");
            }
            List<UsuarioPermiso> usuarioPermisoListOrphanCheck = usuario.getUsuarioPermisoList();
            for (UsuarioPermiso usuarioPermisoListOrphanCheckUsuarioPermiso : usuarioPermisoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the UsuarioPermiso " + usuarioPermisoListOrphanCheckUsuarioPermiso + " in its usuarioPermisoList field has a non-nullable idusuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Ingreso> ingresoList = usuario.getIngresoList();
            for (Ingreso ingresoListIngreso : ingresoList) {
                ingresoListIngreso.setIdusuario(null);
                ingresoListIngreso = em.merge(ingresoListIngreso);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
