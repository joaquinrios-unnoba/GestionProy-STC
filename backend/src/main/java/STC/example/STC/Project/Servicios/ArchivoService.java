package STC.example.STC.Project.Servicios;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoService {
    private final IAServicio iaServicio;

    private static final List<String> tiposPermitidos = List.of("application/pdf", "image/jpeg", "image/jpg", "image/png");

    public ArchivoService(IAServicio iaServicio) {
        this.iaServicio = iaServicio;
    }

    public String procesarArchivo(MultipartFile file) throws Exception {
        if (file.getContentType() == null || !tiposPermitidos.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + file.getContentType());
        }
        
        return iaServicio.enviarImagenRetornarResultado(file);
    }

}
