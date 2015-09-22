package top.rainynight.site.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


@Service("messageProducer")
public class MessageProducer{
    private JmsTemplate jmsTemplate;

    public void sendMessage(Destination destination, final String message) {
//        jmsTemplate.send(destination, new MessageCreator() {
//            public Message createMessage(Session session) throws JMSException {
//                return session.createTextMessage(message);
//            }
//        });

        jmsTemplate.convertAndSend(destination, message);
    }

    @Resource
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

}