package com.example.demo.controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;


import org.springframework.http.MediaType;



import com.example.demo.model.Categoria;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.TipoRopaRepository;
import com.example.demo.service.InventarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/stock")
public class StockController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private TipoRopaRepository tipoRopaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Vista de stock
    @GetMapping
    public String mostrarStock(
        @RequestParam(required = false) Long tipoId,
        @RequestParam(required = false) Long categoriaId,
        @RequestParam(required = false) String talla,
        @RequestParam(required = false) String color,
        HttpSession session,
        Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login-admin";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", inventarioService.filtrarProductos(tipoId, categoriaId, talla, color));
        model.addAttribute("tipos", tipoRopaRepository.findAll());
        model.addAttribute("categorias", inventarioService.listarCategorias());

        //lineas de conteo de productos
        model.addAttribute("conteoPorTipo", inventarioService.contarProductosPorTipo());
        model.addAttribute("conteoPorTipoCategoria", inventarioService.contarProductosPorTipoYCategoria());
        model.addAttribute("conteoPorTipoCategoriaTalla", inventarioService.contarProductosPorTipoCategoriaYTalla());


        ///lineas de conteo para graficos
    Map<String, Long> conteoGraficoTipo = inventarioService.contarProductosPorTipo();
    Map<String, Long> conteoGraficoCategoria = inventarioService.contarProductosPorCategoria();
    
    model.addAttribute("graficoTipoLabels", new ArrayList<>(conteoGraficoTipo.keySet()));
    model.addAttribute("graficoTipoData", new ArrayList<>(conteoGraficoTipo.values()));
    model.addAttribute("graficoCategoriaLabels", new ArrayList<>(conteoGraficoCategoria.keySet()));
    model.addAttribute("graficoCategoriaData", new ArrayList<>(conteoGraficoCategoria.values()));

        return "stock";
    }

    // API para AJAX: listar categorías por tipo
    @GetMapping("/categorias")
    @ResponseBody
    public List<Map<String, Object>> listarCategoriasPorTipo(@RequestParam Long tipoId) {
        List<Categoria> categorias = categoriaRepository.findByTipoId(tipoId);
        return categorias.stream().map(cat -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cat.getId());
            map.put("nombreCategoria", cat.getNombreCategoria());
            return map;
        }).collect(Collectors.toList());
    }

@GetMapping("/exportar-excel")
public ResponseEntity<byte[]> exportarInventarioExcel(
    @RequestParam(required = false) Long tipoId,
    @RequestParam(required = false) Long categoriaId,
    @RequestParam(required = false) String talla,
    @RequestParam(required = false) String color    
) throws IOException {
    byte[] contenidoExcel = inventarioService.exportarInventarioExcel(tipoId, categoriaId, talla, color);

     // Configurar los encabezados de la respuesta

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
     headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Inventario.xlsx");

    return new ResponseEntity<>(contenidoExcel, headers, HttpStatus.OK);
}




}