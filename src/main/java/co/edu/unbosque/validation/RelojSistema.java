package co.edu.unbosque.validation;

import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class RelojSistema implements Reloj {
    @Override
    public LocalDate hoy() {
        return LocalDate.now();
    }
}