/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.plantamanufactura.servicio;

import com.example.plantamanufactura.entidades.Usuario;
import com.example.plantamanufactura.repositorio.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ramos
 */
@Service
@AllArgsConstructor
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    public void saveOrUpdate(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Servicio que implementa la interfaz UserDetailsService de Spring
     * Security.Este servicio se encarga de cargar los detalles del usuario a
     * partir de la base de datos.Se utiliza principalmente para la
     * autenticación de usuarios en la aplicación. La clase interactúa con el
     * repositorio de usuarios para obtener la información de los usuarios y
     * proporciona un método de autenticación a través del `loadUserByUsername`
     * para que Spring Security pueda autenticar a los usuarios en el sistema.
     *
     * Este metodo es llamado automáticamente por Spring Security durante el
     * proceso de autenticación cuando el usuario ingresa sus credenciales en el
     * formulario de inicio de sesión
     *
     * @param username
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent()) {
            var usuarioObj = usuario.get();
            return User.builder()
                    .username(usuarioObj.getUsername())
                    .password(usuarioObj.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
