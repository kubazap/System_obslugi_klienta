package pl.zapala.system_obslugi_klienta.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.zapala.system_obslugi_klienta.exception.StorageException;
import pl.zapala.system_obslugi_klienta.exception.VirusFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Serwis odpowiedzialny za skanowanie danych oraz plików pod kątem wirusów
 * przy użyciu demona ClamAV w trybie INSTREAM.
 * <p>
 * Parametry połączenia i włączenia skanowania są konfigurowane właściwościami:
 * <ul>
 *   <li>clamav.enabled – czy skanowanie jest włączone (domyślnie true)</li>
 *   <li>clamav.host – adres hosta ClamAV (domyślnie localhost)</li>
 *   <li>clamav.port – port ClamAV (domyślnie 3310)</li>
 * </ul>
 */
@Service
public class AntivirusService {

    @Value("${clamav.enabled:true}")
    private boolean enabled;

    @Value("${clamav.host:localhost}")
    private String host;

    @Value("${clamav.port:3310}")
    private int port;

    /**
     * Skanuje tablicę bajtów pod kątem wirusów.
     *
     * @param data dane do skanowania
     * @throws VirusFoundException gdy wykryto wirusa
     * @throws StorageException gdy wystąpi błąd odczytu lub połączenia z ClamAV
     */
    public void scan(byte[] data) {
        if (!enabled) return;
        doScan(new ByteArrayInputStream(data));
    }

    /**
     * Skanuje plik MultipartFile pod kątem wirusów.
     *
     * @param file plik do skanowania
     * @throws VirusFoundException gdy wykryto wirusa
     * @throws StorageException gdy nie można odczytać pliku lub połączenie z ClamAV nie powiodło się
     */
    public void scan(MultipartFile file) {
        if (!enabled) return;
        try (InputStream in = file.getInputStream()) {
            doScan(in);
        } catch (IOException e) {
            throw new StorageException("Nie można odczytać pliku do skanowania", e);
        }
    }

    /**
     * Wysyła strumień danych do demona ClamAV w trybie INSTREAM i odczytuje wynik.
     *
     * @param payload strumień danych do skanowania
     * @throws VirusFoundException gdy ClamAV zgłosi znalezienie wirusa
     * @throws StorageException gdy brak odpowiedzi, nieoczekiwana odpowiedź lub błąd I/O
     */
    private void doScan(InputStream payload) {
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            // Rozpoczęcie skanowania przez INSTREAM
            out.write("zINSTREAM\0".getBytes(StandardCharsets.US_ASCII));

            byte[] buf = new byte[8192];
            int n;
            while ((n = payload.read(buf)) >= 0) {
                out.write(ByteBuffer.allocate(4)
                        .order(ByteOrder.BIG_ENDIAN)
                        .putInt(n)
                        .array());
                out.write(buf, 0, n);
            }
            // Zakończenie strumienia
            out.write(new byte[]{0, 0, 0, 0});
            out.flush();

            // Odczyt odpowiedzi
            String resp = new BufferedReader(new InputStreamReader(in)).readLine();

            if (resp == null) {
                throw new StorageException("Brak odpowiedzi od ClamAV");
            }

            if (resp.contains("FOUND")) {
                String sig = resp.replaceFirst(".*?:", "")
                        .replace("FOUND", "")
                        .trim();
                throw new VirusFoundException(
                        "Wykryto niebezpieczny plik, spróbuj dodać inny. (" + sig + ')');
            }

            if (!resp.contains("OK")) {
                throw new StorageException("Nieoczekiwana odpowiedź ClamAV: " + resp);
            }

        } catch (VirusFoundException ex) {
            throw ex;

        } catch (IOException ex) {
            throw new StorageException(
                    "Błąd połączenia z ClamAV (" + host + ':' + port + ')', ex);
        }
    }
}