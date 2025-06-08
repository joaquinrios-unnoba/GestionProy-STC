package STC.example.STC.Project;
import STC.example.STC.Project.Servicios.GoogleCalendarService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.api.services.calendar.model.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@SpringBootApplication
public class StcProjectApplication {
    public static void main(String[] args) throws FileNotFoundException {


        Dotenv dotenv = Dotenv.configure()
            .directory("./backend")
            .load();

        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
        System.setProperty("GEMINI_API_KEY", dotenv.get("GEMINI_API_KEY"));


        /*
        PRUEBAS, DESPUES BORRAR, SI NO LO USAN BORREN TAMBIEN LA EXCEPCION (FileNotFoundException)
        GoogleCalendarService googleCalendarService = new GoogleCalendarService();

        File file = new File("backend/src/main/resources/respuesta.csv");
        List<Event> eventos = googleCalendarService.csvToEventList(new FileInputStream(file));

        System.out.println(eventos.size());
        int si = 1;
        for (Event e : eventos) {
            System.out.println("Pasada n°: " + si);
            si = si + 1;
            System.out.println("Título: " + e.getSummary());
            System.out.println("Descripción: " + e.getDescription());
            System.out.println("Inicio: " + e.getStart().getDateTime());
            System.out.println("Fin: " + e.getEnd().getDateTime());
            System.out.println("----------------------------");
        }*/

        SpringApplication.run(StcProjectApplication.class, args);
    }
}
