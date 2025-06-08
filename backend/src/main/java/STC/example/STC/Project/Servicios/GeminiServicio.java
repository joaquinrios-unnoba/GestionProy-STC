package STC.example.STC.Project.Servicios;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiServicio {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String enviarImagenRetornarResultado(MultipartFile file) throws Exception {
        byte[] imageBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> imageData = new HashMap<>();
        imageData.put("mime_type", file.getContentType());
        imageData.put("data", base64Image);

        Map<String, Object> parts = new HashMap<>();
        parts.put("inline_data", imageData);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", new Object[] {
            Map.of("parts", new Object[] {
            parts,
            Map.of("text",
            "Convierte el cronograma académico proporcionado a un formato CSV para importar a Google Calendar. Cada fila del CSV debe representar una actividad o clase distinta, con las siguientes columnas:\n" +
            "\n" +
            "* Subject: Título breve para la clase o actividad, identificando con un prefijo 'Clase:' o 'Actividad:' según corresponda.\n" +
            "* Description: Información detallada sobre la clase o actividad, en cuanto a lo que se va a hacer o cómo será.\n" +
            "* Start Date: La fecha y hora de inicio, debe tener el formato ISO 8601 que usa 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora coloca 00:00:00. Asume que el año es el actual si no se indica en el cronograma.\n" +
            "* End Date: La fecha y hora de fin, debe tener el formato ISO 8601 que usa 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora coloca 23:59:59. Asume que el año es el actual si no se indica en el cronograma.\n"
            )
            })
        });

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey))
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

        Map<String, Object> csvParts = new HashMap<>();
        csvParts.put("text",
            "Este es el texto de la instrucción original para el CSV:\n" +
            "Convierte el cronograma académico proporcionado a un formato CSV para importar a Google Calendar. Cada fila del CSV debe representar una actividad o clase distinta, con las siguientes columnas:\n" +
            "\n" +
            "* Subject: Título breve para la clase o actividad, identificando con un prefijo 'Clase:' o 'Actividad:' según corresponda.\n" +
            "* Description: Información detallada sobre la clase o actividad, en cuanto a lo que se va a hacer o cómo será.\n" +
            "* Start Date: La fecha y hora de inicio, debe tener el formato ISO 8601 que usa 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora coloca 00:00:00. Asume que el año es el actual si no se indica en el cronograma.\n" +
            "* End Date: La fecha y hora de fin, debe tener el formato ISO 8601 que usa 'YYYY-MM-DDTHH:MM:SS'. Si no dice hora coloca 23:59:59. Asume que el año es el actual si no se indica en el cronograma.\n" +
            "\n\n" +
            "Este es el CSV generado previamente. Por favor, reanaliza la imagen y el CSV para corregir errores en formatos y duplicados. Devuelve únicamente el CSV corregido en formato CSV, sin explicaciones ni comentarios.\n\n" +
            csvExtraido
        );

        Map<String, Object> reanalisisRequestBody = new HashMap<>();
        reanalisisRequestBody.put("contents", new Object[] {
            Map.of("parts", new Object[] {
                parts,
                csvParts
            })
        });

        String reanalisisJsonBody = mapper.writeValueAsString(reanalisisRequestBody);

        HttpRequest reanalisisRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey))
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

        // Borrar la descarga al escritorio antes de hacer merge a dev
        String desktopPath = System.getProperty("user.home") + "/Desktop/respuesta.csv";
        try (FileWriter writer = new FileWriter(desktopPath)) {
            writer.write(csvExtraido);
        }

        return csvExtraido;
    }

    private String extraerCsv(String textoCompleto) {
        Pattern pattern = Pattern.compile("```csv\\s*(.*?)\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(textoCompleto);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return textoCompleto;
    }
}
