package STC.example.STC.Project.Servicios;
import org.springframework.web.multipart.MultipartFile;

public interface IAServicio {
    String enviarImagenRetornarResultado(MultipartFile file) throws Exception;
}
