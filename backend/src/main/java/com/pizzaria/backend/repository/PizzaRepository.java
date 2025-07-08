package com.pizzaria.backend.repository;

import com.pizzaria.backend.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
}
