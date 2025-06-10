package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer senderId;
    private String senderFirstName;
    private String senderLastName;
    private Integer receiverId;
    private String receiverFirstName;
    private String receiverLastName;
    private String content;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime sentAt;

    public Long getId() { return id; }
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public OffsetDateTime getSentAt() { return sentAt; }
    public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }

    public String getSenderFirstName() { return senderFirstName; }
    public void setSenderFirstName(String senderFirstName) { this.senderFirstName = senderFirstName; }
    public String getSenderLastName() { return senderLastName; }
    public void setSenderLastName(String senderLastName) { this.senderLastName = senderLastName; }
    public String getReceiverFirstName() { return receiverFirstName; }
    public void setReceiverFirstName(String receiverFirstName) { this.receiverFirstName = receiverFirstName; }
    public String getReceiverLastName() { return receiverLastName; }
    public void setReceiverLastName(String receiverLastName) { this.receiverLastName = receiverLastName; }
}