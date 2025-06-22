package STC.example.STC.Project.Servicios;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;

class GoogleCalendarServiceTest {
    private GoogleCalendarService calendarService;

    @BeforeEach
    void setUp() {
        calendarService = new GoogleCalendarService();
    }

    // Test para verificar que convierte correctamente un CSV válido a una lista de eventos
    @Test
    void csvToEventListConCsvValidoDebeConvertirCorrectamente() {
        // Se crea un CSV de ejemplo
        String csvContent = """
                Subject,Description,Start Date,End Date
                Clase de prueba,Descripción de prueba,2050-12-01T10:00:00,2050-12-01T12:00:00
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cronograma.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        String timeZone = "America/Argentina/Buenos_Aires";

        // Llama al método que se está probando
        List<Event> eventos = calendarService.csvToEventList(file, timeZone);

        // Verifica que se haya creado un evento
        assertEquals(1, eventos.size());
        Event evento = eventos.get(0);
        assertNotNull(evento);

        // Verifica que los campos del evento se hayan asignado correctamente
        assertEquals("Clase de prueba", evento.getSummary());
        assertEquals("Descripción de prueba", evento.getDescription());

        EventDateTime start = evento.getStart();
        EventDateTime end = evento.getEnd();

        assertNotNull(start);
        assertNotNull(end);

        assertEquals(timeZone, start.getTimeZone());
        assertEquals(timeZone, end.getTimeZone());

        String startStr = start.getDateTime().toStringRfc3339();
        String endStr = end.getDateTime().toStringRfc3339();

        assertTrue(startStr.startsWith("2050-12-01T10:00:00"));
        assertTrue(endStr.startsWith("2050-12-01T12:00:00"));
    }

    // Test para verificar que convierte correctamente un CSV con fechas sin hora
    @Test
    void csvToEventListConCsvFechasSinHoraDebeConvertirCorrectamente() {
        // Se crea un CSV de ejemplo con fechas sin hora
        String csvContent = """
                Subject,Description,Start Date,End Date
                Clase de prueba,Descripción de prueba,2050-12-01,2050-12-01
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cronograma.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        String timeZone = "America/Argentina/Buenos_Aires";

        // Llama al método que se está probando
        List<Event> eventos = calendarService.csvToEventList(file, timeZone);

        // Verifica que se haya creado un evento
        assertEquals(1, eventos.size());
        Event evento = eventos.get(0);
        assertNotNull(evento);

        // Verifica que los campos del evento se hayan asignado correctamente
        assertEquals("Clase de prueba", evento.getSummary());
        assertEquals("Descripción de prueba", evento.getDescription());

        String startStr = evento.getStart().getDateTime().toStringRfc3339();
        String endStr = evento.getEnd().getDateTime().toStringRfc3339();

        assertTrue(startStr.startsWith("2050-12-01T00:00:00"));
        assertTrue(endStr.startsWith("2050-12-01T") && endStr.contains("23:59:59"));
    }

    // Test para verificar que ignora filas mal formadas en el CSV
    @Test
    void csvToEventListConCsvMalFormadoDebeIgnorarFilas() {
        // Se crea un CSV de ejemplo con una fila mal formada
        String csvContent = """
                Subject,Description,Start Date,End Date
                Clase de prueba,Descripción de prueba
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cronograma.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        String timeZone = "America/Argentina/Buenos_Aires";

        // Llama al método que se está probando con el CSV mal formado
        List<Event> eventos = calendarService.csvToEventList(file, timeZone);

        // Verifica que no se haya creado ningún evento debido a la fila mal formada
        assertEquals(0, eventos.size());
    }
}
