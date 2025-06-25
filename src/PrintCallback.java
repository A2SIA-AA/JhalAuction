import Communication.Message;

public class PrintCallback implements MessageCallback {
    @Override
    public void traiterMessage(Message message) {
        System.out.println(message);
    }
}
