package com.medmor.inventory.repository.rowmapper;

import com.medmor.inventory.domain.Registry;
import com.medmor.inventory.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Registry}, with proper type conversions.
 */
@Service
public class RegistryRowMapper implements BiFunction<Row, String, Registry> {

    private final ColumnConverter converter;

    public RegistryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Registry} stored in the database.
     */
    @Override
    public Registry apply(Row row, String prefix) {
        Registry entity = new Registry();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", Integer.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setSectionId(converter.fromRow(row, prefix + "_section_id", Long.class));
        return entity;
    }
}
