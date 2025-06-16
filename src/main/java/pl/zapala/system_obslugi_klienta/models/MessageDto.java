package pl.zapala.system_obslugi_klienta.models;

/**
 * DTO używane w API do przesyłania danych wiadomości między front-endem a back-endem.
 */
public class MessageDto {

    /**
     * Unikalny identyfikator wiadomości.
     * Pozwala rozróżnić wiadomości w odpowiedziach API.
     */
    private Long id;

    /**
     * Identyfikator nadawcy wiadomości.
     * Używane w żądaniach wysyłania i do oznaczenia rozmówcy w interfejsie.
     */
    private Integer senderId;

    /**
     * Imię nadawcy wiadomości.
     * Przydatne do wyświetlania pełnej informacji o rozmówcy.
     */
    private String senderFirstName;

    /**
     * Nazwisko nadawcy wiadomości.
     * Przydatne do wyświetlania pełnej informacji o rozmówcy.
     */
    private String senderLastName;

    /**
     * Identyfikator odbiorcy wiadomości.
     * Używane do routowania wiadomości w API i w logice powiadomień.
     */
    private Integer receiverId;

    /**
     * Imię odbiorcy wiadomości.
     * Wyświetlane w interfejsie obok treści wiadomości.
     */
    private String receiverFirstName;

    /**
     * Nazwisko odbiorcy wiadomości.
     * Wyświetlane w interfejsie obok treści wiadomości.
     */
    private String receiverLastName;

    /**
     * Treść wiadomości przesyłanej między pracownikami.
     * Pole obowiązkowe przy wysyłaniu nowej wiadomości.
     */
    private String content;

    /**
     * Znacznik czasu wysłania wiadomości w formacie ISO_OFFSET_DATE_TIME.
     * Umożliwia front-endowi poprawne wyświetlenie daty i godziny.
     */
    private String timestamp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getSenderFirstName() { return senderFirstName; }
    public void setSenderFirstName(String senderFirstName) { this.senderFirstName = senderFirstName; }
    public String getSenderLastName() { return senderLastName; }
    public void setSenderLastName(String senderLastName) { this.senderLastName = senderLastName; }
    public String getReceiverFirstName() { return receiverFirstName; }
    public void setReceiverFirstName(String receiverFirstName) { this.receiverFirstName = receiverFirstName; }
    public String getReceiverLastName() { return receiverLastName; }
    public void setReceiverLastName(String receiverLastName) { this.receiverLastName = receiverLastName; }
}