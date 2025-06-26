package STC.example.STC.Project.Servicios;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Primary
@Service
public class GeminiServicio implements IAServicio {
    // Clave de API de Gemini, se inyecta desde el archivo de propiedades
    // Se recupera usando la clase main de la aplicación para cargar las variables de entorno
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String URL_GEMINI = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    // Cliente HTTP para realizar solicitudes a la API de Gemini
    private final HttpClient httpClient;

    public GeminiServicio() {
        // Se crea un cliente HTTP que se usará para enviar solicitudes a la API de Gemini
        this.httpClient = HttpClient.newHttpClient();
    }

    public GeminiServicio(HttpClient httpClient) {
        // Constructor que permite inyectar un cliente HTTP personalizado, útil para pruebas
        this.httpClient = httpClient;
    }

    // Este método envía una imagen a la API de Gemini y retorna el resultado en formato CSV
    public String enviarImagenRetornarResultado(MultipartFile file) throws Exception {
        // Primer envío de la imagen a Gemini para generar el CSV
        
        String tipoArchivo = file.getContentType();
        boolean esPDF = tipoArchivo != null && tipoArchivo.equals("application/pdf");

        // Se convierte la imagen a Base64 y se prepara el cuerpo de la solicitud
        byte[] imageBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> imageData = new HashMap<>();
        imageData.put("mime_type", file.getContentType());
        imageData.put("data", base64Image);

        Calendar calendar = Calendar.getInstance();

        String prompt;
        if (esPDF) {
            prompt = "Convierte el cronograma académico del archivo PDF proporcionado a un formato CSV para importar a Google Calendar. " +
                    "El archivo puede tener varias páginas. Cada fila del CSV debe representar una actividad o clase distinta, con las siguientes columnas:\n\n" +
                    "* Subject: Título breve para la clase o actividad.\n" +
                    "* Description: Información detallada sobre la clase o actividad.\n" +
                    "* Start Date: Formato OBLIGATORIO 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora, aunque diga tiempo estimado, coloca ESTRICTAMENTE 00:00:00. El año es " + calendar.get(Calendar.YEAR) + " si no se indica lo contrario.\n" +
                    "* End Date: Formato OBLIGATORIO 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora, aunque diga tiempo estimado, coloca ESTRICTAMENTE 23:59:59.\n";
        } else {
            prompt = "Convierte el cronograma académico de la imagen proporcionada a un formato CSV para importar a Google Calendar. " +
                    "Cada fila del CSV debe representar una actividad o clase distinta, con las siguientes columnas:\n\n" +
                    "* Subject: Título breve para la clase o actividad.\n" +
                    "* Description: Información detallada sobre la clase o actividad.\n" +
                    "* Start Date: Formato OBLIGATORIO 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora, aunque diga tiempo estimado, coloca ESTRICTAMENTE 00:00:00. El año es " + calendar.get(Calendar.YEAR) + " si no se indica lo contrario.\n" +
                    "* End Date: Formato OBLIGATORIO 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora, aunque diga tiempo estimado, coloca ESTRICTAMENTE 23:59:59.\n";
        }

        Map<String, Object> parts = new HashMap<>();
        parts.put("inline_data", imageData);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", new Object[] {
            Map.of("parts", new Object[] {
                parts,
                Map.of("text", prompt)
            })
        });

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_GEMINI + "?key=" + geminiApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = mapper.readTree(response.body());
        String textoRespuesta = root.path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text").asText();
        String csvExtraido = extraerCsv(textoRespuesta);

        // Segundo envío para reanalizar la imagen y el CSV extraído

        // Se envía el CSV extraído junto con la imagen para corregir errores de formato y duplicados
        String promptCorreccion;
        if (esPDF) {
            promptCorreccion = "Usando el archivo PDF y el CSV generado a partir del texto extraído, realiza las siguientes correcciones:\n" +
                "* Asegúrate de que Start Date y End Date usen el formato ESTRICTO 'YYYY-MM-DDTHH:MM:SS' y que no tenga texto colocado por error.\n" +
                "* Si Start Date no incluye hora, coloca EXACTAMENTE 00:00:00.\n" +
                "* Si End Date no incluye hora, coloca EXACTAMENTE 23:59:59.\n" +
                "* El año es " + calendar.get(Calendar.YEAR) + " si no se menciona explícitamente otro.\n" +
                "* Elimina duplicados, unificando filas similares cuando sea posible.\n" +
                "\n" +
                "CSV generado previamente:\n" +
                csvExtraido;
        } else {
            promptCorreccion = "Usando la imagen y el CSV generado a partir del texto extraído, realiza las siguientes correcciones:\n" +
                "* Asegúrate de que Start Date y End Date usen el formato ESTRICTO 'YYYY-MM-DDTHH:MM:SS' y que no tenga texto colocado por error.\n" +
                "* Si Start Date no incluye hora, coloca EXACTAMENTE 00:00:00.\n" +
                "* Si End Date no incluye hora, coloca EXACTAMENTE 23:59:59.\n" +
                "* El año es " + calendar.get(Calendar.YEAR) + " si no se menciona explícitamente otro.\n" +
                "* Elimina duplicados, unificando filas similares cuando sea posible.\n" +
                "\n" +
                "CSV generado previamente:\n" +
                csvExtraido;
        }

        Map<String, Object> csvParts = new HashMap<>();
        csvParts.put("text", promptCorreccion);

        Map<String, Object> reanalisisRequestBody = new HashMap<>();
        reanalisisRequestBody.put("contents", new Object[] {
            Map.of("parts", new Object[] {
                parts,
                csvParts
            })
        });

        String reanalisisJsonBody = mapper.writeValueAsString(reanalisisRequestBody);

        HttpRequest reanalisisRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_GEMINI + "?key=" + geminiApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reanalisisJsonBody))
                .build();

        HttpResponse<String> reanalisisResponse = httpClient.send(reanalisisRequest, HttpResponse.BodyHandlers.ofString());

        JsonNode reanalisisRoot = mapper.readTree(reanalisisResponse.body());
        String textoReanalisis = reanalisisRoot.path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text").asText();
        csvExtraido = extraerCsv(textoReanalisis);

        // ¡IMPORTANTE!
        // Borrar la descarga al escritorio antes de hacer merge a main
        // ¡IMPORTANTE!
        String desktopPath = System.getProperty("user.home") + "/Desktop/respuesta.csv";
        try (FileWriter writer = new FileWriter(desktopPath)) {
            writer.write(csvExtraido);
        }

        return csvExtraido;
    }

    // Este método extrae el contenido del CSV del texto completo, buscando el bloque delimitado por ```csv
    // Si no encuentra el bloque, devuelve el texto completo
    private String extraerCsv(String textoCompleto) {
        Pattern pattern = Pattern.compile("```csv\\s*(.*?)\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(textoCompleto);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return textoCompleto;
    }
}
