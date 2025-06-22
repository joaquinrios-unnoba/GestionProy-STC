package STC.example.STC.Project.Servicios;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeminiServicioTest {
    @Mock
    private HttpClient httpClient;

    @Mock
    private MultipartFile file;

    @Mock
    private HttpResponse<String> mockResponse;

    private GeminiServicio geminiServicio;

    @BeforeEach
    void setUp() {
        geminiServicio = new GeminiServicio(httpClient);
    }

    // Test para verificar que se envía la imagen y se retorna el CSV extraído
    @Test
    void enviarImagenRetornarResultadoDeberiaRetornarCsvExtraido() throws Exception {
        // Simula bytes de una imagen
        when(httpClient.<String>send(any(), any())).thenReturn(mockResponse);
        byte[] fakeBytes = "fake image".getBytes();
        when(file.getBytes()).thenReturn(fakeBytes);
        when(file.getContentType()).thenReturn("image/png");

        // Simula la respuesta de Gemini con un CSV falso
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        ArrayNode candidates = mapper.createArrayNode();

        ObjectNode candidate = mapper.createObjectNode();
        ObjectNode content = mapper.createObjectNode();
        ArrayNode parts = mapper.createArrayNode();

        ObjectNode part0 = mapper.createObjectNode();
        String texto = "```csv\nSubject,Description,Start Date,End Date\n" +
                "Clase de prueba,Descripción de prueba,2050-12-01T00:00:00,2050-12-01T23:59:59\n```";
        part0.put("text", texto);

        parts.add(part0);
        content.set("parts", parts);
        candidate.set("content", content);
        candidates.add(candidate);
        root.set("candidates", candidates);

        String respuestaFalsa = mapper.writeValueAsString(root);

        // Simula la respuesta de la API de Gemini
        when(httpClient.<String>send(any(), any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(respuestaFalsa);

        // Llama al método que se está probando
        String resultado = geminiServicio.enviarImagenRetornarResultado(file);

        // Verifica que el resultado contenga el CSV esperado
        assertTrue(resultado.contains("Subject,Description,Start Date,End Date"));
        assertTrue(resultado.contains("Clase de prueba"));
        assertTrue(resultado.contains("Descripción de prueba"));
        assertTrue(resultado.contains("2050-12-01T00:00:00"));
        assertTrue(resultado.contains("2050-12-01T23:59:59"));
    }
}
