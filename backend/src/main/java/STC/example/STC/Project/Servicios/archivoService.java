package STC.example.STC.Project.Servicios;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class archivoService {
    private final GeminiServicio geminiServicio;

    public archivoService(GeminiServicio geminiServicio) {
        this.geminiServicio = geminiServicio;
    }

    public String procesarArchivo(MultipartFile file) throws Exception {
        return geminiServicio.enviarImagenRetornarResultado(file);
    }

}
