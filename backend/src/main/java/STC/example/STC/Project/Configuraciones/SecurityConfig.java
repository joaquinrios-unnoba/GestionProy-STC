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
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS con configuración personalizada
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

        // Uso de allowedOriginPatterns para mayor flexibilidad (por ej: subdominios)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Permite enviar cookies o autenticación

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
