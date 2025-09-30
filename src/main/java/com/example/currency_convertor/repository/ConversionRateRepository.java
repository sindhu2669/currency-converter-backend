package com.example.currency_convertor.repository;

import com.example.currency_convertor.model.ConversionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversionRateRepository extends JpaRepository<ConversionRate, Long> {

    @Query("SELECT r FROM ConversionRate r WHERE r.fromCurrency = :from AND r.toCurrency = :to AND r.isCache = true")
    Optional<ConversionRate> findCachedRate(@Param("from") String from, @Param("to") String to);

    @Modifying
    @Query("DELETE FROM ConversionRate r WHERE r.fromCurrency = :from AND r.toCurrency = :to AND r.isCache = true")
    void deleteCachedRate(@Param("from") String from, @Param("to") String to);

    @Query("SELECT r FROM ConversionRate r WHERE r.isCache = false ORDER BY r.fetchedAt DESC")
    List<ConversionRate> findConversionHistory();

}