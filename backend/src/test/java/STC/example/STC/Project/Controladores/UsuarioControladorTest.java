package STC.example.STC.Project.Controladores;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import STC.example.STC.Project.Servicios.UsuarioServicio;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioControlador.class)
class UsuarioControladorTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioServicio usuarioServicio;

    // Test para el endpoint /api/user
    @Test
    void userEndpointDebeDevolverDatosDelUsuario() throws Exception {
        // Datos simulados del usuario
        Map<String, Object> userData = Map.of(
                "nombre", "Usuario",
                "email", "usuario@prueba.com"
        );

        // Simula un usuario OAuth2
        OAuth2User fakeUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("sub", "123456789", "email", "usuario@prueba.com"),
                "sub"
        );

        // Simula el comportamiento del servicio
        Mockito.when(usuarioServicio.obtenerDatosUsuario(Mockito.any(OAuth2User.class)))
               .thenReturn(userData);

        // Realiza el request simulando un login OAuth2
        mockMvc.perform(get("/api/user").with(oauth2Login().oauth2User(fakeUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Usuario"))
                .andExpect(jsonPath("$.email").value("usuario@prueba.com"));

        // Verifica que el servicio fue llamado con el usuario correcto
        Mockito.verify(usuarioServicio, Mockito.times(1)).obtenerDatosUsuario(Mockito.any(OAuth2User.class));
        Mockito.verifyNoMoreInteractions(usuarioServicio);
    }
}
