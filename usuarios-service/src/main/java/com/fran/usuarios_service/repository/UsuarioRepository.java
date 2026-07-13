package com.fran.usuarios_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fran.usuarios_service.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/*
	 * Spring Data JPA genera la consulta automaticamente a partir del nombre del
	 * metodo No hace falta escribir SQL, solo declarar la firma
	 */
	
	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

}
