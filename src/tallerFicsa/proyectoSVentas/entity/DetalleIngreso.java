/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tallerFicsa.proyectoSVentas.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "detalle_ingreso")
@NamedQueries({
    @NamedQuery(name = "DetalleIngreso.findAll", query = "SELECT d FROM DetalleIngreso d"),
    @NamedQuery(name = "DetalleIngreso.findByIddetalleIngreso", query = "SELECT d FROM DetalleIngreso d WHERE d.iddetalleIngreso = :iddetalleIngreso"),
    @NamedQuery(name = "DetalleIngreso.findByCantidad", query = "SELECT d FROM DetalleIngreso d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "DetalleIngreso.findByPrecioCompra", query = "SELECT d FROM DetalleIngreso d WHERE d.precioCompra = :precioCompra"),
    @NamedQuery(name = "DetalleIngreso.findByPrecioVenta", query = "SELECT d FROM DetalleIngreso d WHERE d.precioVenta = :precioVenta")})
public class DetalleIngreso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddetalle_ingreso")
    private Integer iddetalleIngreso;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private int cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "precio_compra")
    private BigDecimal precioCompra;
    @Basic(optional = false)
    @Column(name = "precio_venta")
    private BigDecimal precioVenta;
    @JoinColumn(name = "idarticulo", referencedColumnName = "idarticulo")
    @ManyToOne(optional = false)
    private Articulo idarticulo;
    @JoinColumn(name = "idingreso", referencedColumnName = "idingreso")
    @ManyToOne(optional = false)
    private Ingreso idingreso;

    public DetalleIngreso() {
    }

    public DetalleIngreso(Integer iddetalleIngreso) {
        this.iddetalleIngreso = iddetalleIngreso;
    }

    public DetalleIngreso(Integer iddetalleIngreso, int cantidad, BigDecimal precioCompra, BigDecimal precioVenta) {
        this.iddetalleIngreso = iddetalleIngreso;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
    }

    public Integer getIddetalleIngreso() {
        return iddetalleIngreso;
    }

    public void setIddetalleIngreso(Integer iddetalleIngreso) {
        this.iddetalleIngreso = iddetalleIngreso;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Articulo getIdarticulo() {
        return idarticulo;
    }

    public void setIdarticulo(Articulo idarticulo) {
        this.idarticulo = idarticulo;
    }

    public Ingreso getIdingreso() {
        return idingreso;
    }

    public void setIdingreso(Ingreso idingreso) {
        this.idingreso = idingreso;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddetalleIngreso != null ? iddetalleIngreso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleIngreso)) {
            return false;
        }
        DetalleIngreso other = (DetalleIngreso) object;
        if ((this.iddetalleIngreso == null && other.iddetalleIngreso != null) || (this.iddetalleIngreso != null && !this.iddetalleIngreso.equals(other.iddetalleIngreso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tallerFicsa.proyectoSVentas.entity.DetalleIngreso[ iddetalleIngreso=" + iddetalleIngreso + " ]";
    }
    
}
