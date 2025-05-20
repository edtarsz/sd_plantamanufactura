package com.example.auth.config;

import com.example.auth.dto.UsuarioResponse;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementación personalizada de UserDetails para integrar la información
 * del usuario en el contexto de seguridad de Spring Security.
 * 
 * Esta clase adapta un objeto {@link UsuarioResponse} al contrato que
 * Spring Security necesita para autenticar y autorizar usuarios.
 * 
 * Nota: Actualmente, el método {@code getAuthorities()} devuelve null,
 * por lo que no se manejan roles o permisos explícitos.
 * 
 * @author Ramos
 */
public class DetallesUsuario implements UserDetails {

    private String username;
    private String password;

    /**
     * Constructor que crea una instancia de DetallesUsuario a partir de un DTO
     * {@link UsuarioResponse} y un codificador de contraseña.
     * 
     * @param usuarioResponse DTO con los datos del usuario.
     * @param encoder Codificador de contraseñas (no utilizado en este constructor, pero puede ser útil para futuras modificaciones).
     */
    public DetallesUsuario(UsuarioResponse usuarioResponse, PasswordEncoder encoder) {
        this.username = usuarioResponse.getUsername();
        this.password = usuarioResponse.getPassword();
    }

    /**
     * Obtiene las autoridades (roles o permisos) concedidos al usuario.
     * 
     * @return colección de GrantedAuthority, actualmente null porque nunca se ocupó.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * Obtiene la contraseña codificada del usuario.
     * 
     * @return contraseña codificada.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Obtiene el nombre de usuario del usuario.
     * 
     * @return nombre de usuario.
     */
    @Override
    public String getUsername() {
        return username;
    }
}
