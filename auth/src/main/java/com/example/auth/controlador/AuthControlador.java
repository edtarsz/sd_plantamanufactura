package com.example.auth.controlador;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.UsuarioRequest;
import com.example.auth.feign.UsuarioClient;
import com.example.auth.servicios.AuthServicio;
import com.example.auth.servicios.JWTServicio;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la autenticación y registro de usuarios.
 * 
 * Define endpoints para:
 * - Registro de nuevos usuarios.
 * - Login y generación de token JWT.
 * - Validación de tokens.
 * 
 * Utiliza Spring Security para la autenticación y un cliente Feign para la
 * comunicación con el servicio de usuarios.
 * 
 * @author Ramos
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthControlador {

    @Autowired
    private AuthServicio servicioAuth;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private JWTServicio jwtServicio;

    /**
     * Endpoint para registrar un nuevo usuario.
     * 
     * Recibe un objeto {@link UsuarioRequest} con los datos del usuario.
     * Codifica la contraseña usando BCrypt y la envía al servicio de usuarios.
     * 
     * @param usuarioRequest Datos del usuario a registrar.
     * @return un mapa con el mensaje de éxito o error.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> addNewUser(@RequestBody UsuarioRequest usuarioRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            String encodedPassword = new BCryptPasswordEncoder().encode(usuarioRequest.getPassword());
            usuarioRequest.setPassword(encodedPassword);
            usuarioClient.createUsuario(usuarioRequest);

            response.put("message", "Usuario registrado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al registrar: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para autenticar a un usuario y generar un token JWT.
     * 
     * Recibe un objeto {@link AuthRequest} con usuario y contraseña.
     * Si la autenticación es exitosa, devuelve un token JWT.
     * 
     * @param request Datos de autenticación del usuario.
     * @return un mapa con el token JWT o un mensaje de error.
     */
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );

            String token = jwtServicio.generateToken(request.getUsername());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    /**
     * Endpoint para validar un token JWT.
     * 
     * Recibe un parámetro de consulta "token" y valida su vigencia.
     * 
     * @param token Token JWT a validar.
     * @return mensaje indicando si la llave es válida.
     */
    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        servicioAuth.validateToken(token);
        return "La llave es válida";
    }
}
