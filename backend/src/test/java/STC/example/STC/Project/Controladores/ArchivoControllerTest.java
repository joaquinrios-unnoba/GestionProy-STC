package STC.example.STC.Project.Controladores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import STC.example.STC.Project.Servicios.ArchivoService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArchivoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArchivoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArchivoService archivoService;

    // Test para el endpoint /api/upload
    @Test
    void subirArchivoDebeProcesarArchivoValido() throws Exception {
        // Datos simulados del archivo
        MockMultipartFile archivo = new MockMultipartFile(
            "archivo",
            "test.csv",
            "text/csv",
            "Contenido de prueba".getBytes()
        );

        // Simula el comportamiento del servicio
        when(archivoService.procesarArchivo(any())).thenReturn("Procesado correctamente");

        // Realiza el request simulando la subida del archivo
        mockMvc.perform(multipart("/api/upload")
            .file(archivo)
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("Procesado correctamente"));

        // Verifica que el servicio fue llamado con el archivo correcto
        verify(archivoService, times(1)).procesarArchivo(any());
    }

    // Test para el endpoint /api/upload con archivo vacío
    @Test
    void subirArchivoDebeRechazarArchivoVacio() throws Exception {
        // Datos simulados de un archivo vacío
        MockMultipartFile archivoVacio = new MockMultipartFile(
            "archivo",
            "vacio.csv",
            "text/csv",
            new byte[0]
        );

        // Realiza el request simulando la subida del archivo vacío
        mockMvc.perform(multipart("/api/upload")
            .file(archivoVacio)
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Archivo vacío."));

        // Verifica que el servicio no fue llamado
        verifyNoInteractions(archivoService);
    }
}
