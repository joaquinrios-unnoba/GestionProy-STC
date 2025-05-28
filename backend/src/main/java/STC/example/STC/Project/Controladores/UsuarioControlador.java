package STC.example.STC.Project.Controladores;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import STC.example.STC.Project.Servicios.UsuarioServicio;

@RestController
@RequestMapping("/api/user")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    
    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return usuarioServicio.obtenerDatosUsuario(principal);
    }
}
