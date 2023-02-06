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
@Table(name = "detalle_venta")
@NamedQueries({
    @NamedQuery(name = "DetalleVenta.findAll", query = "SELECT d FROM DetalleVenta d"),
    @NamedQuery(name = "DetalleVenta.findByIddetalleVenta", query = "SELECT d FROM DetalleVenta d WHERE d.iddetalleVenta = :iddetalleVenta"),
    @NamedQuery(name = "DetalleVenta.findByCantidad", query = "SELECT d FROM DetalleVenta d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "DetalleVenta.findByPrecioVenta", query = "SELECT d FROM DetalleVenta d WHERE d.precioVenta = :precioVenta"),
    @NamedQuery(name = "DetalleVenta.findByDescuento", query = "SELECT d FROM DetalleVenta d WHERE d.descuento = :descuento")})
public class DetalleVenta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddetalle_venta")
    private Integer iddetalleVenta;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private int cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "precio_venta")
    private BigDecimal precioVenta;
    @Basic(optional = false)
    @Column(name = "descuento")
    private BigDecimal descuento;
    @JoinColumn(name = "idarticulo", referencedColumnName = "idarticulo")
    @ManyToOne(optional = false)
    private Articulo idarticulo;
    @JoinColumn(name = "idventa", referencedColumnName = "idventa")
    @ManyToOne(optional = false)
    private Venta idventa;

    public DetalleVenta() {
    }

    public DetalleVenta(Integer iddetalleVenta) {
        this.iddetalleVenta = iddetalleVenta;
    }

    public DetalleVenta(Integer iddetalleVenta, int cantidad, BigDecimal precioVenta, BigDecimal descuento) {
        this.iddetalleVenta = iddetalleVenta;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.descuento = descuento;
    }

    public Integer getIddetalleVenta() {
        return iddetalleVenta;
    }

    public void setIddetalleVenta(Integer iddetalleVenta) {
        this.iddetalleVenta = iddetalleVenta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public Articulo getIdarticulo() {
        return idarticulo;
    }

    public void setIdarticulo(Articulo idarticulo) {
        this.idarticulo = idarticulo;
    }

    public Venta getIdventa() {
        return idventa;
    }

    public void setIdventa(Venta idventa) {
        this.idventa = idventa;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddetalleVenta != null ? iddetalleVenta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleVenta)) {
            return false;
        }
        DetalleVenta other = (DetalleVenta) object;
        if ((this.iddetalleVenta == null && other.iddetalleVenta != null) || (this.iddetalleVenta != null && !this.iddetalleVenta.equals(other.iddetalleVenta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tallerFicsa.proyectoSVentas.entity.DetalleVenta[ iddetalleVenta=" + iddetalleVenta + " ]";
    }
    
}
