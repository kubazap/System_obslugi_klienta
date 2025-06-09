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

@Service
public class AntivirusService {

    @Value("${clamav.enabled:true}")
    private boolean enabled;

    @Value("${clamav.host:localhost}")
    private String host;

    @Value("${clamav.port:3310}")
    private int port;

    public void scan(byte[] data) {
        if (!enabled) return;
        doScan(new ByteArrayInputStream(data));
    }

    public void scan(MultipartFile file) {
        if (!enabled) return;
        try (InputStream in = file.getInputStream()) {
            doScan(in);
        } catch (IOException e) {
            throw new StorageException("Nie można odczytać pliku do skanowania", e);
        }
    }

    private void doScan(InputStream payload) {

        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream  in  = socket.getInputStream()) {

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
            out.write(new byte[]{0, 0, 0, 0});
            out.flush();

            String resp = new BufferedReader(new InputStreamReader(in))
                    .readLine();

            if (resp == null)
                throw new StorageException("Brak odpowiedzi od ClamAV");

            if (resp.contains("FOUND")) {
                String sig = resp.replaceFirst(".*?:", "")
                        .replace("FOUND", "")
                        .trim();
                throw new VirusFoundException(
                        "Wykryto niebezpieczny plik, spróbuj dodać inny. (" + sig + ')');
            }

            if (!resp.endsWith("OK"))
                throw new StorageException("Nieoczekiwana odpowiedź ClamAV: " + resp);

        } catch (VirusFoundException ex) {
            throw ex;

        } catch (IOException ex) {
            throw new StorageException(
                    "Błąd połączenia z ClamAV (" + host + ':' + port + ')', ex);
        }
    }
}