package com.medmor.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Registry.
 */
@Table("registry")
public class Registry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("amount")
    private Integer amount;

    @JsonIgnoreProperties(value = { "registries" }, allowSetters = true)
    @Transient
    private Product product;

    @Column("product_id")
    private Long productId;

    @JsonIgnoreProperties(value = { "registries" }, allowSetters = true)
    @Transient
    private Section section;

    @Column("section_id")
    private Long sectionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Registry id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public Registry amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return this.product;
    }

    public Registry product(Product product) {
        this.setProduct(product);
        this.productId = product != null ? product.getId() : null;
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    public Section getSection() {
        return this.section;
    }

    public Registry section(Section section) {
        this.setSection(section);
        this.sectionId = section != null ? section.getId() : null;
        return this;
    }

    public void setSection(Section section) {
        this.section = section;
        this.sectionId = section != null ? section.getId() : null;
    }

    public Long getSectionId() {
        return this.sectionId;
    }

    public void setSectionId(Long section) {
        this.sectionId = section;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Registry)) {
            return false;
        }
        return id != null && id.equals(((Registry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Registry{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            "}";
    }
}
