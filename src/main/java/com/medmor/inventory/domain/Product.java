package com.medmor.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.medmor.inventory.domain.enumeration.ContainerType;
import com.medmor.inventory.domain.enumeration.ProductSize;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Product.
 */
@Table("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("product_size")
    private ProductSize productSize;

    @NotNull(message = "must not be null")
    @Column("color")
    private String color;

    @NotNull(message = "must not be null")
    @Column("price")
    private Float price;

    @NotNull(message = "must not be null")
    @Column("fragile")
    private Boolean fragile;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
    @Column("lote")
    private String lote;

    @NotNull(message = "must not be null")
    @Column("container_type")
    private ContainerType containerType;

    @Transient
    @JsonIgnoreProperties(value = { "product", "section" }, allowSetters = true)
    private Set<Registry> registries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product id(Long id) {
        this.id = id;
        return this;
    }

    public ProductSize getProductSize() {
        return this.productSize;
    }

    public Product productSize(ProductSize productSize) {
        this.productSize = productSize;
        return this;
    }

    public void setProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }

    public String getColor() {
        return this.color;
    }

    public Product color(String color) {
        this.color = color;
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Float getPrice() {
        return this.price;
    }

    public Product price(Float price) {
        this.price = price;
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Boolean getFragile() {
        return this.fragile;
    }

    public Product fragile(Boolean fragile) {
        this.fragile = fragile;
        return this;
    }

    public void setFragile(Boolean fragile) {
        this.fragile = fragile;
    }

    public String getLote() {
        return this.lote;
    }

    public Product lote(String lote) {
        this.lote = lote;
        return this;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public ContainerType getContainerType() {
        return this.containerType;
    }

    public Product containerType(ContainerType containerType) {
        this.containerType = containerType;
        return this;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    public Set<Registry> getRegistries() {
        return this.registries;
    }

    public Product registries(Set<Registry> registries) {
        this.setRegistries(registries);
        return this;
    }

    public Product addRegistry(Registry registry) {
        this.registries.add(registry);
        registry.setProduct(this);
        return this;
    }

    public Product removeRegistry(Registry registry) {
        this.registries.remove(registry);
        registry.setProduct(null);
        return this;
    }

    public void setRegistries(Set<Registry> registries) {
        if (this.registries != null) {
            this.registries.forEach(i -> i.setProduct(null));
        }
        if (registries != null) {
            registries.forEach(i -> i.setProduct(this));
        }
        this.registries = registries;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", productSize='" + getProductSize() + "'" +
            ", color='" + getColor() + "'" +
            ", price=" + getPrice() +
            ", fragile='" + getFragile() + "'" +
            ", lote='" + getLote() + "'" +
            ", containerType='" + getContainerType() + "'" +
            "}";
    }
}
