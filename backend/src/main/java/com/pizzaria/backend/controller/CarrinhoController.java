package com.pizzaria.backend.controller;

import com.pizzaria.backend.model.Carrinho;
import com.pizzaria.backend.model.ItemCarrinho;
import com.pizzaria.backend.model.Pizza;
import com.pizzaria.backend.repository.CarrinhoRepository;
import com.pizzaria.backend.repository.ItemCarrinhoRepository;
import com.pizzaria.backend.repository.ClienteRepository;
import com.pizzaria.backend.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;


@RestController
@RequestMapping("/carrinho")
@CrossOrigin(origins = { "http://localhost:80", "http://localhost:3000" }) // Permite requisições de múltiplas origens (CORS)
public class CarrinhoController {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    /**
     * Adiciona um item ao carrinho de um cliente.
     * Se o carrinho não existir, ele é criado.
     * 
     * @param clienteId ID do cliente que está adicionando a pizza
     * @param item Dados da pizza a ser adicionada
     * @return Item do carrinho salvo no banco de dados
     */
    @PostMapping
    public ResponseEntity<ItemCarrinho> adicionarItem(@RequestParam Long clienteId, @RequestBody ItemCarrinho item) {
        Carrinho carrinho = null;

        // Verifica se o cliente já tem um carrinho
        List<Carrinho> carrinhosCliente = carrinhoRepository.findAll().stream()
            .filter(c -> c.getCliente() != null && c.getCliente().getId().equals(clienteId))
            .toList();

        // Se sim, utiliza o primeiro carrinho encontrado
        if (!carrinhosCliente.isEmpty()) {
            carrinho = carrinhosCliente.get(0);
        }

        // Se não, cria um novo carrinho para o cliente
        if (carrinho == null) {
            carrinho = new Carrinho();
            carrinho.setCliente(clienteRepository.findById(clienteId).orElse(null));
            carrinho = carrinhoRepository.save(carrinho);
        }

        // Adiciona o item ao carrinho
        item.setCarrinho(carrinho);
        ItemCarrinho salvo = itemCarrinhoRepository.save(item);
        return ResponseEntity.ok(salvo);
    }

    /**
     * Lista os itens de um carrinho específico, incluindo nome da pizza.
     * 
     * @param carrinhoId ID do carrinho a ser consultado
     * @return Lista de itens com detalhes, incluindo nome da pizza
     */
    @GetMapping("/{carrinhoId}")
    public ResponseEntity<List<Map<String, Object>>> listarItens(@PathVariable Long carrinhoId) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId).orElse(null);
        if (carrinho == null)
            return ResponseEntity.notFound().build();

        List<ItemCarrinho> itens = carrinho.getItens();

        // Mapeia cada item para incluir nome da pizza, além dos dados básicos
        List<Map<String, Object>> itensComNome = itens.stream().map(item -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", item.getId());
            map.put("pizzaId", item.getPizzaId());
            map.put("quantidade", item.getQuantidade());
            map.put("preco", item.getPreco());

            // Busca o nome da pizza, se disponível
            Pizza pizza = null;
            if (item.getPizzaId() != null) {
                pizza = pizzaRepository.findById(item.getPizzaId()).orElse(null);
            }
            map.put("pizzaNome", pizza != null ? pizza.getNome() : null);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(itensComNome);
    }

    /**
     * Remove um item do carrinho com base no ID do item.
     * 
     * @param itemId ID do item a ser removido
     * @return 204 No Content se removido com sucesso
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long itemId) {
        itemCarrinhoRepository.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza a quantidade e o preço de um item do carrinho.
     * 
     * @param item Item com novas informações (quantidade/preço)
     * @return Item atualizado
     */
    @PutMapping
    public ResponseEntity<ItemCarrinho> atualizarQuantidade(@RequestBody ItemCarrinho item) {
        Optional<ItemCarrinho> opt = itemCarrinhoRepository.findById(item.getId());
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        ItemCarrinho existente = opt.get();
        existente.setQuantidade(item.getQuantidade());
        existente.setPreco(item.getPreco());

        itemCarrinhoRepository.save(existente);
        return ResponseEntity.ok(existente);
    }

    /**
     * Lista todos os carrinhos cadastrados no sistema.
     * Utilizado geralmente para fins administrativos ou de debug.
     * 
     * @return Lista de todos os carrinhos
     */
    @GetMapping("")
    public ResponseEntity<List<Carrinho>> listarTodos() {
        return ResponseEntity.ok(carrinhoRepository.findAll());
    }
}
