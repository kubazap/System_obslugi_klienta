package pl.zapala.system_obslugi_klienta.models;

public class MessageDto {
    private Long id;
    private Integer senderId;
    private String senderFirstName;
    private String senderLastName;
    private Integer receiverId;
    private String receiverFirstName;
    private String receiverLastName;
    private String content;
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