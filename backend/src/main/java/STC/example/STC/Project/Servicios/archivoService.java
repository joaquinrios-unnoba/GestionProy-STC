package STC.example.STC.Project.Servicios;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoService {
    private final GeminiServicio geminiServicio;

    public ArchivoService(GeminiServicio geminiServicio) {
        this.geminiServicio = geminiServicio;
    }

    public String procesarArchivo(MultipartFile file) throws Exception {
        return geminiServicio.enviarImagenRetornarResultado(file);
    }

}
