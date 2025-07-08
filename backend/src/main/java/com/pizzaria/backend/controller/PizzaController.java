package com.pizzaria.backend.controller;

import com.pizzaria.backend.model.Pizza;
import com.pizzaria.backend.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pizzas")
@CrossOrigin(origins = { "http://localhost:80", "http://localhost:3000" })
public class PizzaController {
    @Autowired
    private PizzaRepository pizzaRepository;

    @PostMapping
    public ResponseEntity<Pizza> criarPizza(@RequestBody Pizza pizza) {
        Pizza salva = pizzaRepository.save(pizza);
        return ResponseEntity.ok(salva);
    }

    @GetMapping
    public ResponseEntity<List<Pizza>> listarPizzas() {
        return ResponseEntity.ok(pizzaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pizza> buscarPizza(@PathVariable Long id) {
        return pizzaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pizza> atualizarPizza(@PathVariable Long id, @RequestBody Pizza pizza) {
        return pizzaRepository.findById(id)
                .map(p -> {
                    p.setNome(pizza.getNome());
                    p.setDescricao(pizza.getDescricao());
                    p.setPreco(pizza.getPreco());
                    pizzaRepository.save(p);
                    return ResponseEntity.ok(p);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPizza(@PathVariable Long id) {
        pizzaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
