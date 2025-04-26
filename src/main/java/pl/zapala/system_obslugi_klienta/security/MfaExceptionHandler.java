package pl.zapala.system_obslugi_klienta.security;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.zapala.system_obslugi_klienta.exception.MfaVerificationException;

@ControllerAdvice
public class MfaExceptionHandler {

    @ExceptionHandler(MfaVerificationException.class)
    public String handleMfaException(MfaVerificationException ex,
                                     RedirectAttributes attrs) {

        String msg = ex.getMessage();
        attrs.addFlashAttribute("mfaErrorMessage", msg);

        if (msg.contains("Sesja wygasła")) {
            return "redirect:/login?timeout";
        }

        return "redirect:/login?mfa&error";
    }
}
