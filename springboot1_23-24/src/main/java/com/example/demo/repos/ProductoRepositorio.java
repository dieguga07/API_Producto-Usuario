package com.example.demo.repos;

import com.example.demo.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
    List<Producto> findByUsuarioId(Long usuarioId);

}
