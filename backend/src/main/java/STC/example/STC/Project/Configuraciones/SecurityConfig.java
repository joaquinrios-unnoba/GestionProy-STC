package STC.example.STC.Project.Configuraciones;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para simplificar la configuración
            .csrf(csrf -> csrf.disable())
            // Configuración de CORS para permitir solicitudes desde el frontend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error", "/webjars/**").permitAll() // Rutas públicas necesarias
                .anyRequest().authenticated() // El resto requiere autenticación
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("http://localhost:3000/dashboard", true) // Redirige a frontend tras login exitoso
                .failureUrl("/login?error") // Redirige en caso de error
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL para hacer logout
                .logoutSuccessUrl("http://localhost:3000/login") // Redirige al frontend después del logout
                .invalidateHttpSession(true) // Invalida sesión HTTP
                .clearAuthentication(true) // Limpia autenticación
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Uso de allowedOriginPatterns para mayor flexibilidad
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));

        // Permitir todos los métodos HTTP necesarios
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permitir todos los encabezados
        configuration.setAllowedHeaders(List.of("*"));
        // Permitir credenciales para manejar cookies y autenticación
        configuration.setAllowCredentials(true);

        // Configurar los encabezados expuestos
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Exponer encabezados específicos si es necesario
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
