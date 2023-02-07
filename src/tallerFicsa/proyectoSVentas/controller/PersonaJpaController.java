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
import tallerFicsa.proyectoSVentas.controller.exceptions.IllegalOrphanException;
import tallerFicsa.proyectoSVentas.controller.exceptions.NonexistentEntityException;
import tallerFicsa.proyectoSVentas.entity.Ingreso;
import tallerFicsa.proyectoSVentas.entity.Persona;

/**
 *
 * @author Usuario
 */
public class PersonaJpaController implements Serializable {

    public PersonaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Persona persona) {
        if (persona.getVentaList() == null) {
            persona.setVentaList(new ArrayList<Venta>());
        }
        if (persona.getIngresoList() == null) {
            persona.setIngresoList(new ArrayList<Ingreso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Venta> attachedVentaList = new ArrayList<Venta>();
            for (Venta ventaListVentaToAttach : persona.getVentaList()) {
                ventaListVentaToAttach = em.getReference(ventaListVentaToAttach.getClass(), ventaListVentaToAttach.getIdventa());
                attachedVentaList.add(ventaListVentaToAttach);
            }
            persona.setVentaList(attachedVentaList);
            List<Ingreso> attachedIngresoList = new ArrayList<Ingreso>();
            for (Ingreso ingresoListIngresoToAttach : persona.getIngresoList()) {
                ingresoListIngresoToAttach = em.getReference(ingresoListIngresoToAttach.getClass(), ingresoListIngresoToAttach.getIdingreso());
                attachedIngresoList.add(ingresoListIngresoToAttach);
            }
            persona.setIngresoList(attachedIngresoList);
            em.persist(persona);
            for (Venta ventaListVenta : persona.getVentaList()) {
                Persona oldIdclienteOfVentaListVenta = ventaListVenta.getIdcliente();
                ventaListVenta.setIdcliente(persona);
                ventaListVenta = em.merge(ventaListVenta);
                if (oldIdclienteOfVentaListVenta != null) {
                    oldIdclienteOfVentaListVenta.getVentaList().remove(ventaListVenta);
                    oldIdclienteOfVentaListVenta = em.merge(oldIdclienteOfVentaListVenta);
                }
            }
            for (Ingreso ingresoListIngreso : persona.getIngresoList()) {
                Persona oldIdproveedorOfIngresoListIngreso = ingresoListIngreso.getIdproveedor();
                ingresoListIngreso.setIdproveedor(persona);
                ingresoListIngreso = em.merge(ingresoListIngreso);
                if (oldIdproveedorOfIngresoListIngreso != null) {
                    oldIdproveedorOfIngresoListIngreso.getIngresoList().remove(ingresoListIngreso);
                    oldIdproveedorOfIngresoListIngreso = em.merge(oldIdproveedorOfIngresoListIngreso);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Persona persona) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona persistentPersona = em.find(Persona.class, persona.getIdpersona());
            List<Venta> ventaListOld = persistentPersona.getVentaList();
            List<Venta> ventaListNew = persona.getVentaList();
            List<Ingreso> ingresoListOld = persistentPersona.getIngresoList();
            List<Ingreso> ingresoListNew = persona.getIngresoList();
            List<String> illegalOrphanMessages = null;
            for (Venta ventaListOldVenta : ventaListOld) {
                if (!ventaListNew.contains(ventaListOldVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Venta " + ventaListOldVenta + " since its idcliente field is not nullable.");
                }
            }
            for (Ingreso ingresoListOldIngreso : ingresoListOld) {
                if (!ingresoListNew.contains(ingresoListOldIngreso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ingreso " + ingresoListOldIngreso + " since its idproveedor field is not nullable.");
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
            persona.setVentaList(ventaListNew);
            List<Ingreso> attachedIngresoListNew = new ArrayList<Ingreso>();
            for (Ingreso ingresoListNewIngresoToAttach : ingresoListNew) {
                ingresoListNewIngresoToAttach = em.getReference(ingresoListNewIngresoToAttach.getClass(), ingresoListNewIngresoToAttach.getIdingreso());
                attachedIngresoListNew.add(ingresoListNewIngresoToAttach);
            }
            ingresoListNew = attachedIngresoListNew;
            persona.setIngresoList(ingresoListNew);
            persona = em.merge(persona);
            for (Venta ventaListNewVenta : ventaListNew) {
                if (!ventaListOld.contains(ventaListNewVenta)) {
                    Persona oldIdclienteOfVentaListNewVenta = ventaListNewVenta.getIdcliente();
                    ventaListNewVenta.setIdcliente(persona);
                    ventaListNewVenta = em.merge(ventaListNewVenta);
                    if (oldIdclienteOfVentaListNewVenta != null && !oldIdclienteOfVentaListNewVenta.equals(persona)) {
                        oldIdclienteOfVentaListNewVenta.getVentaList().remove(ventaListNewVenta);
                        oldIdclienteOfVentaListNewVenta = em.merge(oldIdclienteOfVentaListNewVenta);
                    }
                }
            }
            for (Ingreso ingresoListNewIngreso : ingresoListNew) {
                if (!ingresoListOld.contains(ingresoListNewIngreso)) {
                    Persona oldIdproveedorOfIngresoListNewIngreso = ingresoListNewIngreso.getIdproveedor();
                    ingresoListNewIngreso.setIdproveedor(persona);
                    ingresoListNewIngreso = em.merge(ingresoListNewIngreso);
                    if (oldIdproveedorOfIngresoListNewIngreso != null && !oldIdproveedorOfIngresoListNewIngreso.equals(persona)) {
                        oldIdproveedorOfIngresoListNewIngreso.getIngresoList().remove(ingresoListNewIngreso);
                        oldIdproveedorOfIngresoListNewIngreso = em.merge(oldIdproveedorOfIngresoListNewIngreso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = persona.getIdpersona();
                if (findPersona(id) == null) {
                    throw new NonexistentEntityException("The persona with id " + id + " no longer exists.");
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
            Persona persona;
            try {
                persona = em.getReference(Persona.class, id);
                persona.getIdpersona();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persona with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Venta> ventaListOrphanCheck = persona.getVentaList();
            for (Venta ventaListOrphanCheckVenta : ventaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Venta " + ventaListOrphanCheckVenta + " in its ventaList field has a non-nullable idcliente field.");
            }
            List<Ingreso> ingresoListOrphanCheck = persona.getIngresoList();
            for (Ingreso ingresoListOrphanCheckIngreso : ingresoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Ingreso " + ingresoListOrphanCheckIngreso + " in its ingresoList field has a non-nullable idproveedor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(persona);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Persona> findPersonaEntities() {
        return findPersonaEntities(true, -1, -1);
    }

    public List<Persona> findPersonaEntities(int maxResults, int firstResult) {
        return findPersonaEntities(false, maxResults, firstResult);
    }

    private List<Persona> findPersonaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Persona.class));
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

    public Persona findPersona(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Persona.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Persona> rt = cq.from(Persona.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
