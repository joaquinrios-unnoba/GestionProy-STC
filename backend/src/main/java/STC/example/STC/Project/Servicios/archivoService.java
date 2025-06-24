package STC.example.STC.Project.Servicios;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoService {
    private final IAServicio iaServicio;

    public ArchivoService(IAServicio iaServicio) {
        this.iaServicio = iaServicio;
    }

    public String procesarArchivo(MultipartFile file) throws Exception {
        return iaServicio.enviarImagenRetornarResultado(file);
    }

}
