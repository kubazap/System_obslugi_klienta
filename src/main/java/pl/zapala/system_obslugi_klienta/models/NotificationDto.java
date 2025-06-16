package pl.zapala.system_obslugi_klienta.models;

/**
 * DTO używane w API do przesyłania danych o powiadomieniach.
 */
public class NotificationDto {

    /**
     * Unikalny identyfikator powiadomienia.
     */
    private Long id;

    /**
     * Identyfikator użytkownika, do którego należy powiadomienie.
     */
    private Integer userId;

    /**
     * Typ powiadomienia, określający jego kategorię (np. "NEW_MESSAGE").
     */
    private String type;

    /**
     * Treść powiadomienia wyświetlana użytkownikowi.
     */
    private String content;

    /**
     * Znacznik czasu utworzenia powiadomienia w formacie ISO_OFFSET_DATE_TIME.
     * Pozwala front-endowi na poprawne wyświetlenie daty i godziny.
     */
    private String createdAt;

    /**
     * Flaga wskazująca, czy powiadomienie zostało odczytane.
     * - true  — powiadomienie odczytane,
     * - false — nieodczytane.
     */
    private boolean read;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
