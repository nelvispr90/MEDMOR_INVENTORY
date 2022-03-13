package com.medmor.inventory.repository.rowmapper;

import com.medmor.inventory.domain.Product;
import com.medmor.inventory.domain.enumeration.ContainerType;
import com.medmor.inventory.domain.enumeration.ProductSize;
import com.medmor.inventory.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Product}, with proper type conversions.
 */
@Service
public class ProductRowMapper implements BiFunction<Row, String, Product> {

    private final ColumnConverter converter;

    public ProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Product} stored in the database.
     */
    @Override
    public Product apply(Row row, String prefix) {
        Product entity = new Product();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setProductSize(converter.fromRow(row, prefix + "_product_size", ProductSize.class));
        entity.setColor(converter.fromRow(row, prefix + "_color", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Float.class));
        entity.setFragile(converter.fromRow(row, prefix + "_fragile", Boolean.class));
        entity.setLote(converter.fromRow(row, prefix + "_lote", String.class));
        entity.setContainerType(converter.fromRow(row, prefix + "_container_type", ContainerType.class));
        return entity;
    }
}
