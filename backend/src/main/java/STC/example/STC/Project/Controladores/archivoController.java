package STC.example.STC.Project.Controladores;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import STC.example.STC.Project.Servicios.ArchivoService;

@RestController
@RequestMapping("/api")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> subirArchivo(@RequestParam MultipartFile archivo) throws Exception {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        String archivoNuevo = archivoService.procesarArchivo(archivo);

        return ResponseEntity.ok(archivoNuevo);
    }
}
