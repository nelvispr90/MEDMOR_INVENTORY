package com.medmor.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.medmor.inventory.domain.enumeration.ProductType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Section.
 */
@Table("section")
public class Section implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("area")
    private Float area;

    @NotNull(message = "must not be null")
    @Column("product_type")
    private ProductType productType;

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

    public Section id(Long id) {
        this.id = id;
        return this;
    }

    public Float getArea() {
        return this.area;
    }

    public Section area(Float area) {
        this.area = area;
        return this;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    public ProductType getProductType() {
        return this.productType;
    }

    public Section productType(ProductType productType) {
        this.productType = productType;
        return this;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Set<Registry> getRegistries() {
        return this.registries;
    }

    public Section registries(Set<Registry> registries) {
        this.setRegistries(registries);
        return this;
    }

    public Section addRegistry(Registry registry) {
        this.registries.add(registry);
        registry.setSection(this);
        return this;
    }

    public Section removeRegistry(Registry registry) {
        this.registries.remove(registry);
        registry.setSection(null);
        return this;
    }

    public void setRegistries(Set<Registry> registries) {
        if (this.registries != null) {
            this.registries.forEach(i -> i.setSection(null));
        }
        if (registries != null) {
            registries.forEach(i -> i.setSection(this));
        }
        this.registries = registries;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        return id != null && id.equals(((Section) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Section{" +
            "id=" + getId() +
            ", area=" + getArea() +
            ", productType='" + getProductType() + "'" +
            "}";
    }
}
