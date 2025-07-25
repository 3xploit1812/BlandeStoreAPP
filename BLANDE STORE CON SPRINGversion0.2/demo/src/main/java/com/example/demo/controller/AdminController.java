package com.example.demo.controller;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/menu")
    public String menuAdmin(HttpSession session, Model model) {
        // La seguridad ahora es manejada por AdminAuthInterceptor
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "menu-admin";
    }

@GetMapping("/usuarios")
public String gestionUsuarios(HttpSession session, Model model) {
    // La seguridad ahora es manejada por AdminAuthInterceptor
    model.addAttribute("usuario", session.getAttribute("usuario"));
    
    // Carga los usuarios
    List<Usuario> usuarios = usuarioService.listarTodos();
    model.addAttribute("usuarios", usuarios);
    
    return "Lista-Usuario"; // o "lista-usuario" según tu elección anterior
}

    @PostMapping("/usuarios/guardar")
    @ResponseBody
    public String guardarUsuario(@RequestBody Usuario usuario) {
        // La seguridad ahora es manejada por AdminAuthInterceptor

        // Validar si el username ya existe (para creación)
        if (usuario.getId() == null && usuarioService.existeUsername(usuario.getUsername())) {
            return "error:El nombre de usuario ya existe";
        }

        usuarioService.guardarUsuario(usuario);
        return "success";
    }

    @GetMapping("/usuarios/{id}")
    @ResponseBody
    public Usuario obtenerUsuario(@PathVariable Long id) {
        // La seguridad ahora es manejada por AdminAuthInterceptor
        return usuarioService.obtenerPorId(id).orElse(null);
    }

    @DeleteMapping("/usuarios/{id}")
    @ResponseBody
    public String eliminarUsuario(@PathVariable Long id, HttpSession session) {
        // La seguridad ahora es manejada por AdminAuthInterceptor
        Usuario admin = (Usuario) session.getAttribute("usuario");

        // No permitir eliminarse a sí mismo
        if (admin != null && admin.getId().equals(id)) {
            return "error:No puedes eliminarte a ti mismo";
        }

        usuarioService.eliminarUsuario(id);
        return "success";
    }
    @GetMapping("/usuarios/exportar-excel")
public ResponseEntity<byte[]> exportarUsuariosExcel() throws IOException {
    // La seguridad ahora es manejada por AdminAuthInterceptor
    byte[] excelBytes = usuarioService.exportarUsuariosExcel();
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "usuarios.xlsx");
    
    return ResponseEntity.ok()
            .headers(headers)
            .body(excelBytes);
}
}
