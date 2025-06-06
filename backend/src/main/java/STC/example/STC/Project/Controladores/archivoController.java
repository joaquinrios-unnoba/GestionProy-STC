package STC.example.STC.Project.Controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class archivoController {

    @PostMapping("/upload")
    public ResponseEntity<String> subirArchivo(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        // Logica de lo que se haria en con el archivo recibido (si queremos podemos guardarlo en la BD)
        
        String nombre = archivo.getOriginalFilename();
        long tamaño = archivo.getSize();

        System.out.println("Archivo recibido: " + nombre + ", tamaño: " + tamaño + " bytes");

        return ResponseEntity.ok("Archivo recibido correctamente: " + nombre);
    }
}

