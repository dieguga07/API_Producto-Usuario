package com.example.demo.controlador;

import com.example.demo.repos.ProductoRepositorio;
import com.example.demo.repos.UsuarioRepositorio;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.modelo.Producto;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Valid;
import java.util.List;
@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/api/producto")
public class ProductoControlador {
    private final ProductoRepositorio productoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public ProductoControlador(ProductoRepositorio productoRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public class ProductoService {

        private final ProductoRepositorio productoRepositorio;

        public ProductoService(ProductoRepositorio productoRepositorio) {
            this.productoRepositorio = productoRepositorio;
        }

        public Producto guardarProducto(Producto nuevoProducto) {
            if (productoRepositorio.existsById(nuevoProducto.getId())) {
                // Producto existente, realizar actualización en lugar de creación
                Producto productoExistente = productoRepositorio.findById(nuevoProducto.getId()).orElse(null);
                if (productoExistente != null) {
                    // Realizar actualización del producto existente
                    productoExistente.setId(nuevoProducto.getId());

                    // Puedes actualizar otros campos según tu modelo de datos
                    return productoRepositorio.save(productoExistente);
                }
            }
            // Si no existe, guarda el nuevo producto
            return productoRepositorio.save(nuevoProducto);
        }
    }

    @ControllerAdvice
    public class GlobalControllerExceptionHandler {

        @Configuration
        public class CorsConfig implements WebMvcConfigurer {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        }

        @GetMapping("/")
        List<Producto> getProductos() {
            return productoRepositorio.findAll();
        }

        @GetMapping("/{id}")
        Producto getProductoById(@PathVariable Long id) {
            return productoRepositorio.findById(id).orElse(null);
        }

        @PostMapping("/")
        public ResponseEntity<?> createProducto(@Valid @RequestBody Producto producto) {
            if (productoRepositorio.existsById(producto.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto ya existe");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(productoRepositorio.save(producto));
        }

        @PutMapping("/{id}")
        public Producto updateProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
            return productoRepositorio.findById(id)
                    .map(existingProducto -> {
                        existingProducto.setName(producto.getName());
                        existingProducto.setPrice(producto.getPrice());
                        return productoRepositorio.save(existingProducto);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Producto not found with id " + id));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
            return productoRepositorio.findById(id)
                    .map(producto -> {
                        productoRepositorio.delete(producto);
                        return ResponseEntity.ok().build();
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Producto not found with id " + id));
        }

        @PostMapping("/{id}/productos")
        public Producto addProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
            return productoRepositorio.findById(id)
                    .map(usuario -> {
                        producto.setUsuario(usuario.getUsuario());
                        return productoRepositorio.save(producto);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario not found with id " + id));
        }

        @PutMapping("/{id}/productos/{productoId}")
        public Producto updateProducto(@PathVariable Long id, @PathVariable Long productoId, @Valid @RequestBody Producto productoRequest) {
            if (!usuarioRepositorio.existsById(id)) {
                throw new ResourceNotFoundException("Usuario not found with id " + id);
            }

            return productoRepositorio.findById(productoId)
                    .map(producto -> {
                        producto.setName(productoRequest.getName());
                        producto.setPrice(productoRequest.getPrice());
                        return productoRepositorio.save(producto);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Producto not found with id " + productoId));
        }

        @GetMapping("/usuario/{usuarioId}")
        public List<Producto> getProductosByUsuario(@PathVariable Long usuarioId) {
            return productoRepositorio.findByUsuarioId(usuarioId);
        }

    }
}