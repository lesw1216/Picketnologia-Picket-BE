package com.picketlogia.picket.api.auth.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Slf4j
public abstract class BaseMailService {

    protected final JavaMailSender mailSender;

    protected static final boolean IS_HTML = true;
    protected static final String DEFAULT_ENCODE = "UTF-8";

    protected String subject = "[Picket] 안녕하세요. Picket에서 알립니다.";

    protected BaseMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public BaseMailService(JavaMailSender mailSender, String subject) {
        this.mailSender = mailSender;
        this.subject = subject;
    }

    /**
     * 지정된 이메일 주소로 본 서비스의 기본 메일을 발송한다.
     *
     * @param email 수신자 이메일 주소
     */
    public void sendToEmail(String email) {

        MimeMessage mimeMessage = createMimeMessage(email);
        mailSender.send(mimeMessage);
    }

    /**
     * 수신자와 본문 HTML이 채워진 MimeMessage를 생성해 반환한다.
     *
     * @param email 수신자 이메일 주소
     * @return 전송 준비가 완료된 MimeMessage
     */
    protected MimeMessage createMimeMessage(String email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, DEFAULT_ENCODE);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(createView(), IS_HTML);

        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }

        return mimeMessage;
    }

    /**
     * 이메일에 보여주기 위한 내용 추가
     * @return String 형식의 html
     */
    protected abstract String createView();
}
