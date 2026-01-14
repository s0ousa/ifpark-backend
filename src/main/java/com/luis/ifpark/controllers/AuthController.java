package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.auth.LoginDTO;
import com.luis.ifpark.dtos.auth.LoginResponseDTO;
import com.luis.ifpark.dtos.auth.RegistroCompletoDTO;
import com.luis.ifpark.dtos.usuario.UsuarioResponseDTO;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import com.luis.ifpark.security.JwtUtil;
import com.luis.ifpark.services.AuthService;
import com.luis.ifpark.services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @PostMapping(value = "/register")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody RegistroCompletoDTO dto) {
        UsuarioResponseDTO createdDto = authService.register(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdDto);
    }

    @PostMapping(value = "/create-usuario")
    public ResponseEntity<UsuarioResponseDTO> createUsuarioForExistingPessoa(
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam PapelUsuario papel,
            @RequestParam(required = false) java.util.UUID campusId) {
        UsuarioResponseDTO createdDto = authService.createUsuarioForExistingPessoa(cpf, email, senha, papel, campusId);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdDto);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha());

        var auth = authenticationManager.authenticate(usernamePassword);

        var user = (Usuario) auth.getPrincipal();


        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                expiration,
                user.getEmail(),
                user.getPapel().toString(),
                user.getId(),
                user.getPessoa().getId()
        ));
    }
}
