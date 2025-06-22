package STC.example.STC.Project.Servicios;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServicioTest {
    private UsuarioServicio usuarioServicio;

    @BeforeEach
    void setUp() {
        usuarioServicio = new UsuarioServicio();
    }

    // Test para verificar que se obtienen los datos del usuario correctamente
    @Test
    void obtenerDatosUsuarioDeberiaRetornarDatosCorrectos() {
        // Datos simulados del usuario
        Map<String, Object> atributos = Map.of(
            "name", "usuario",
            "email", "usuario@prueba.com",
            "picture", "http://foto.com/usuario.jpg",
            "sub", "12345"
        );

        // Simulación de un OAuth2User con los atributos del usuario
        OAuth2User mockUser = mock(OAuth2User.class);
        when(mockUser.getAttributes()).thenReturn(atributos);

        // Llamada al método que se está probando
        Map<String, Object> resultado = usuarioServicio.obtenerDatosUsuario(mockUser);

        // Verificación de que los datos obtenidos son correctos
        assertEquals("usuario", resultado.get("nombre"));
        assertEquals("usuario@prueba.com", resultado.get("email"));
        assertEquals("http://foto.com/usuario.jpg", resultado.get("foto"));
        assertEquals("12345", resultado.get("id"));
    }

    // Test para verificar que se lanza una excepción si el usuario es null
    @Test
    void obtenerDatosUsuarioConUsuarioNullDeberiaLanzarExcepcion() {
        // Llamada al método que se está probando con un usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioServicio.obtenerDatosUsuario(null);
        });
    }
}
