package STC.example.STC.Project.Configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/webjars/**").permitAll() // Rutas públicas para el flujo de login
                        .anyRequest().authenticated() // Cualquier otra ruta, aunque no tendremos, requiere autenticación
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .defaultSuccessUrl("http://localhost:3000/dashboard", true) // Redirige a tu frontend después del login exitoso
                        .failureUrl("/login?error") // Redirige en caso de error
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("http://localhost:3000/login") // Redirige al frontend después del logout
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // ¡Asegúrate de cambiar esto en producción!
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}