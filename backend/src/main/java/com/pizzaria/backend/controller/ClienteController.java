package com.pizzaria.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pizzaria.backend.model.Cliente;
import com.pizzaria.backend.repository.ClienteRepository;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping
    public ResponseEntity<Cliente> criarCliente(@RequestBody Cliente cliente) {
        Cliente salvo = clienteRepository.save(cliente);
        return ResponseEntity.ok(salvo);
    }

    // Listar todos os clientes
    @CrossOrigin(origins = { "http://localhost:80", "http://localhost:3000" })
    @GetMapping("")
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Cliente> login(@RequestBody Cliente loginData) {
        List<Cliente> clientes = clienteRepository.findAll();
        for (Cliente c : clientes) {
            if (c.getEmail().equals(loginData.getEmail()) && c.getSenha() != null && c.getSenha().equals(loginData.getSenha())) {
                return ResponseEntity.ok(c);
            }
        }
        return ResponseEntity.status(401).build();
    }
}
