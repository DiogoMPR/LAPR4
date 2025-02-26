package core.services;

import core.domain.email.Email;
import core.protocol.Jobs4UProtocol;
import core.protocol.ProtocolCodes;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Service class for sending emails.
 *
 * @author Miguel Cardoso
 */
@Service
public class EmailService {

    /**
     * Creates an email with the given recipient, subject, and body.
     *
     * @param candidateEmail the email address of the recipient
     * @param subject the subject of the email
     * @param body the body of the email
     * @return the created email
     */
    public Email createEmail(String candidateEmail, String subject, String body) {
        return new Email(candidateEmail, subject, body);
    }

    /**
     * Sends a list of emails.
     *
     * @param emailsToSend the list of emails to send
     */
    public void sendAllEmails(List<Email> emailsToSend) {
        for (Email email : emailsToSend) {
            try {
                Socket socket = new Socket("127.0.0.1", 2005);
                DataInputStream inData = new DataInputStream(socket.getInputStream());

                Jobs4UProtocol protocol = new Jobs4UProtocol(socket);
                protocol.sendEmail(email.toWho(), email.subject(), email.body());

                inData.readByte();
                byte code = inData.readByte();

                if (code == ProtocolCodes.ACK.code()) {
                    System.out.println("ACK Code Received: Email to " + email.toWho() + " sent successfully!");
                } else if (code == ProtocolCodes.ERR.code()) {
                    System.out.println("ERR Code Received: Could not send email to " + email.toWho() + "!");
                }

                socket.close();
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
