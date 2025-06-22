package STC.example.STC.Project.Servicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArchivoServiceTest {
    @Mock
    private GeminiServicio geminiServicio;

    private ArchivoService archivoService;

    @BeforeEach
    void setUp() {
        archivoService = new ArchivoService(geminiServicio);
    }

    // Test para verificar que el servicio de Gemini es llamado correctamente
    @Test
    void procesarArchivoDeberiaRetornarResultadoGemini() throws Exception {
        // Simula un archivo MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        when(geminiServicio.enviarImagenRetornarResultado(mockFile)).thenReturn("Resultado CSV");

        // Llama al método que se está probando
        String resultado = archivoService.procesarArchivo(mockFile);

        // Verifica que el resultado sea el esperado y que se haya llamado al servicio de Gemini
        assertEquals("Resultado CSV", resultado);
        verify(geminiServicio).enviarImagenRetornarResultado(mockFile);
    }
}
