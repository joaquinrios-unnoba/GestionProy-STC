package STC.example.STC.Project.Controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import STC.example.STC.Project.Servicios.archivoService;
@RestController
@RequestMapping("/api")
public class archivoController {

    private final archivoService archivoService;

    public archivoController(archivoService archivoService) {
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

