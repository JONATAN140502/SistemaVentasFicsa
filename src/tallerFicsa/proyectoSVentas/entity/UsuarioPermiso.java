/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tallerFicsa.proyectoSVentas.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "usuario_permiso")
@NamedQueries({
    @NamedQuery(name = "UsuarioPermiso.findAll", query = "SELECT u FROM UsuarioPermiso u"),
    @NamedQuery(name = "UsuarioPermiso.findByIdusuarioPermiso", query = "SELECT u FROM UsuarioPermiso u WHERE u.idusuarioPermiso = :idusuarioPermiso")})
public class UsuarioPermiso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idusuario_permiso")
    private Integer idusuarioPermiso;
    @JoinColumn(name = "idusuario", referencedColumnName = "idusuario")
    @ManyToOne(optional = false)
    private Usuario idusuario;
    @JoinColumn(name = "idpermiso", referencedColumnName = "idpermiso")
    @ManyToOne(optional = false)
    private Permiso idpermiso;

    public UsuarioPermiso() {
    }

    public UsuarioPermiso(Integer idusuarioPermiso) {
        this.idusuarioPermiso = idusuarioPermiso;
    }

    public Integer getIdusuarioPermiso() {
        return idusuarioPermiso;
    }

    public void setIdusuarioPermiso(Integer idusuarioPermiso) {
        this.idusuarioPermiso = idusuarioPermiso;
    }

    public Usuario getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Usuario idusuario) {
        this.idusuario = idusuario;
    }

    public Permiso getIdpermiso() {
        return idpermiso;
    }

    public void setIdpermiso(Permiso idpermiso) {
        this.idpermiso = idpermiso;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idusuarioPermiso != null ? idusuarioPermiso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsuarioPermiso)) {
            return false;
        }
        UsuarioPermiso other = (UsuarioPermiso) object;
        if ((this.idusuarioPermiso == null && other.idusuarioPermiso != null) || (this.idusuarioPermiso != null && !this.idusuarioPermiso.equals(other.idusuarioPermiso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tallerFicsa.proyectoSVentas.entity.UsuarioPermiso[ idusuarioPermiso=" + idusuarioPermiso + " ]";
    }
    
}
