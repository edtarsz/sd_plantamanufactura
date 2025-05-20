package com.example.auth.config;

import com.example.auth.dto.UsuarioResponse;
import com.example.auth.feign.UsuarioClient;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado que implementa la interfaz {@link UserDetailsService} de Spring Security.
 * 
 * Este servicio se encarga de cargar los detalles del usuario (usuario y contraseña)
 * para la autenticación, obteniendo los datos mediante un cliente Feign {@link UsuarioClient}.
 * 
 * Utiliza un {@link PasswordEncoder} para manejar la codificación de contraseñas.
 * 
 * En caso de que el usuario no sea encontrado, lanza una excepción {@link UsernameNotFoundException}.
 * 
 * @author Ramos
 */
@Service
public class DetallesUsuarioServicio implements UserDetailsService {

    /**
     * Cliente Feign para comunicarse con el servicio externo que proporciona
     * información del usuario.
     */
    @Autowired
    private UsuarioClient usuarioClient;

    /**
     * Codificador de contraseñas para manejar la codificación y comparación segura.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Carga los detalles del usuario dado un nombre de usuario.
     * 
     * @param username Nombre de usuario a buscar.
     * @return una instancia de {@link UserDetails} con la información del usuario.
     * @throws UsernameNotFoundException si el usuario no es encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UsuarioResponse> usuario = usuarioClient.findByUsername(username);
        return usuario.map(u -> new DetallesUsuario(u, passwordEncoder))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
