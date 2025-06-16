package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Encja reprezentująca powiadomienie dla użytkownika w systemie.
 * Przechowuje informacje o typie powiadomienia, treści oraz stanie odczytu.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * Data i godzina utworzenia powiadomienia, zapisana wraz ze strefą czasową.
     */
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    /**
     * Flaga oznaczająca, czy powiadomienie zostało odczytane (true) czy nie (false).
     */
    @Column(name = "read")
    private boolean read;

    public Long getId() { return id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }
}
