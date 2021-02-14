package id.giansar.demo.dto;

public class ResponseDto {
    public int status;
    public Message message = new Message();

    public ResponseDto(int status, String messageCode, String messageText) {
        this.status = status;
        this.message.messageCode = messageCode;
        this.message.messageText = messageText;
    }

    public class Message {
        public String messageCode;
        public String messageText;
    }
}
