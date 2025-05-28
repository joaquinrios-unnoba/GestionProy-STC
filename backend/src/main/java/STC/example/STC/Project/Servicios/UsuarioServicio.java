package STC.example.STC.Project.Servicios;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServicio {

    public Map<String, Object> obtenerDatosUsuario(OAuth2User principal) {
        if (principal == null) {
            throw new IllegalArgumentException("El usuario no está autenticado");
        }

        Map<String, Object> atributos = principal.getAttributes();
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", atributos.get("name"));
        datosUsuario.put("email", atributos.get("email"));
        datosUsuario.put("foto", atributos.get("picture"));
        datosUsuario.put("id", atributos.get("sub"));

        return datosUsuario;
    }
}
