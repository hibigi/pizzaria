package com.pizzaria.backend.controller;

import com.pizzaria.backend.model.*;
import com.pizzaria.backend.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = { "http://localhost:80", "http://localhost:3000" })
public class PedidoController {
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private CarrinhoRepository carrinhoRepository;
    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    // Finalizar pedido: transforma carrinho em pedido
    @PostMapping
    public ResponseEntity<Pedido> finalizarPedido(@RequestParam Long clienteId, @RequestParam Long carrinhoId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId).orElse(null);
        if (cliente == null || carrinho == null)
            return ResponseEntity.badRequest().build();
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setData(LocalDateTime.now());
        pedido.setStatus("PENDENTE");
        // Copia itens do carrinho para o pedido
        List<ItemPedido> itensPedido = carrinho.getItens().stream().map(itemCarrinho -> {
            ItemPedido ip = new ItemPedido();
            ip.setPedido(pedido);
            ip.setPizzaId(itemCarrinho.getPizzaId());
            ip.setQuantidade(itemCarrinho.getQuantidade());
            ip.setPreco(itemCarrinho.getPreco());
            return ip;
        }).collect(Collectors.toList());
        pedido.setItens(itensPedido);
        Pedido salvo = pedidoRepository.save(pedido);
        // Limpa o carrinho
        itemCarrinhoRepository.deleteAll(carrinho.getItens());
        return ResponseEntity.ok(salvo);
    }

    // Listar pedidos por cliente
    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidosPorCliente(@RequestParam Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .filter(p -> p.getCliente() != null && p.getCliente().getId().equals(clienteId))
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    // Listar todos os pedidos
    @CrossOrigin(origins = { "http://localhost:80", "http://localhost:3000" })
    @GetMapping("/all")
    public ResponseEntity<List<Pedido>> listarTodosPedidos() {
        return ResponseEntity.ok(pedidoRepository.findAll());
    }
}
