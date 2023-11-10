package com.example.demo.controlador;

import com.example.demo.modelo.Usuario;
import com.example.demo.repos.UsuarioRepositorio;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/usuario")
public class UsuarioControlador {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioControlador(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping("/")
    public List<Usuario> getUsuarios() {
        return usuarioRepositorio.findAll();
    }

    // Add methods for POST, PUT, DELETE...
    @PostMapping("/")
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        if (usuarioRepositorio.existsById(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario ya existe");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepositorio.save(usuario));
    }
    @PutMapping("/{id}")
    public Usuario updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return usuarioRepositorio.findById(id)
                .map(existingUsuario -> {
                    existingUsuario.setName(usuario.getName());
                    existingUsuario.setEmail(usuario.getEmail());
                    return usuarioRepositorio.save(existingUsuario);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        return usuarioRepositorio.findById(id)
                .map(usuario -> {
                    usuarioRepositorio.delete(usuario);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id " + id));
    }
}
