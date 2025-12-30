package com.servicedesk.service;

import com.servicedesk.entity.Department;
import com.servicedesk.entity.ServiceRequest;
import com.servicedesk.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Email Service
 * Handles automated email notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Send request created email to user
     */
    @Async
    public void sendRequestCreatedEmail(ServiceRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getRequester().getEmail());
            helper.setSubject("Request #" + request.getId() + " Created Successfully");

            String htmlContent = buildRequestCreatedEmail(request);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Request created email sent to {} for request #{}",
                    request.getRequester().getEmail(),
                    request.getId());

        } catch (MessagingException e) {
            log.error("Failed to send request created email for request #{}", request.getId(), e);
        } catch (Exception e) {
            log.error("Email service not configured or error sending email", e);
        }
    }

    /**
     * Send status change email
     */
    @Async
    public void sendStatusChangeEmail(ServiceRequest request, String oldStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getRequester().getEmail());
            helper.setSubject("Request #" + request.getId() + " Status Updated");

            String htmlContent = buildStatusChangeEmail(request, oldStatus);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Status change email sent to {} for request #{}",
                    request.getRequester().getEmail(),
                    request.getId());

        } catch (MessagingException e) {
            log.error("Failed to send status change email for request #{}", request.getId(), e);
        } catch (Exception e) {
            log.error("Email service not configured or error sending email", e);
        }
    }

    /**
     * Send SLA breach alert email
     */
    @Async
    public void sendSLABreachEmail(ServiceRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Send to management/admin
            helper.setTo("admin@example.com"); // Configure this
            helper.setSubject("SLA BREACH ALERT - Request #" + request.getId());

            String htmlContent = buildSLABreachEmail(request);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("SLA breach email sent for request #{}", request.getId());

        } catch (MessagingException e) {
            log.error("Failed to send SLA breach email for request #{}", request.getId(), e);
        } catch (Exception e) {
            log.error("Email service not configured or error sending email", e);
        }
    }

    /**
     * Send assignment notification email
     */
    @Async
    public void sendAssignmentEmail(ServiceRequest request, Department department) {
        try {
            if (department.getEmail() == null || department.getEmail().isEmpty()) {
                log.warn("Department {} has no email configured", department.getName());
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(department.getEmail());
            helper.setSubject("New Request Assigned - #" + request.getId());

            String htmlContent = buildAssignmentEmail(request, department);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Assignment email sent to {} for request #{}",
                    department.getEmail(),
                    request.getId());

        } catch (MessagingException e) {
            log.error("Failed to send assignment email for request #{}", request.getId(), e);
        } catch (Exception e) {
            log.error("Email service not configured or error sending email", e);
        }
    }

    /**
     * Build request created email HTML
     */
    private String buildRequestCreatedEmail(ServiceRequest request) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f5f5f5; }
                        .info-box { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #1976d2; }
                        .button {
                            background-color: #1976d2;
                            color: white;
                            padding: 12px 24px;
                            text-decoration: none;
                            border-radius: 5px;
                            display: inline-block;
                            margin-top: 20px;
                        }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Request Created Successfully</h2>
                        </div>
                        <div class="content">
                            <p>Hello %s,</p>
                            <p>Your service request has been created successfully.</p>

                            <div class="info-box">
                                <strong>Request ID:</strong> #%d<br>
                                <strong>Title:</strong> %s<br>
                                <strong>Category:</strong> %s<br>
                                <strong>Priority:</strong> %s<br>
                                <strong>Status:</strong> %s
                            </div>

                            <p>We will process your request and keep you updated on its progress.</p>

                            <a href="http://localhost:3000/requests/%d" class="button">
                                View Request Details
                            </a>
                        </div>
                        <div class="footer">
                            <p>This is an automated message. Please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        request.getRequester().getFirstName(),
                        request.getId(),
                        request.getTitle(),
                        request.getCategory() != null ? request.getCategory().getName() : "N/A",
                        request.getPriority(),
                        request.getStatus(),
                        request.getId());
    }

    /**
     * Build status change email HTML
     */
    private String buildStatusChangeEmail(ServiceRequest request, String oldStatus) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f5f5f5; }
                        .status-change { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #4caf50; }
                        .button {
                            background-color: #1976d2;
                            color: white;
                            padding: 12px 24px;
                            text-decoration: none;
                            border-radius: 5px;
                            display: inline-block;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Request Status Updated</h2>
                        </div>
                        <div class="content">
                            <p>Hello %s,</p>
                            <p>Your request <strong>#%d</strong> status has been updated.</p>

                            <div class="status-change">
                                <strong>Previous Status:</strong> %s<br>
                                <strong>Current Status:</strong> %s<br>
                                <strong>Title:</strong> %s
                            </div>

                            <a href="http://localhost:3000/requests/%d" class="button">
                                View Request Details
                            </a>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        request.getRequester().getFirstName(),
                        request.getId(),
                        oldStatus,
                        request.getStatus(),
                        request.getTitle(),
                        request.getId());
    }

    /**
     * Build SLA breach email HTML
     */
    private String buildSLABreachEmail(ServiceRequest request) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #f44336; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #fff3cd; }
                        .alert { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #f44336; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>⚠️ SLA BREACH ALERT</h2>
                        </div>
                        <div class="content">
                            <p><strong>IMMEDIATE ATTENTION REQUIRED</strong></p>

                            <div class="alert">
                                <strong>Request ID:</strong> #%d<br>
                                <strong>Title:</strong> %s<br>
                                <strong>Priority:</strong> %s<br>
                                <strong>Department:</strong> %s<br>
                                <strong>Status:</strong> %s
                            </div>

                            <p>This request has breached its SLA deadline and requires immediate escalation.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        request.getId(),
                        request.getTitle(),
                        request.getPriority(),
                        request.getDepartment() != null ? request.getDepartment().getName() : "Unassigned",
                        request.getStatus());
    }

    /**
     * Build assignment email HTML
     */
    private String buildAssignmentEmail(ServiceRequest request, Department department) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f5f5f5; }
                        .info-box { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #1976d2; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>New Request Assigned</h2>
                        </div>
                        <div class="content">
                            <p>Hello %s Team,</p>
                            <p>A new request has been assigned to your department.</p>

                            <div class="info-box">
                                <strong>Request ID:</strong> #%d<br>
                                <strong>Title:</strong> %s<br>
                                <strong>Category:</strong> %s<br>
                                <strong>Priority:</strong> %s<br>
                                <strong>Description:</strong> %s
                            </div>

                            <p>Please review and take appropriate action.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        department.getName(),
                        request.getId(),
                        request.getTitle(),
                        request.getCategory() != null ? request.getCategory().getName() : "N/A",
                        request.getPriority(),
                        request.getDescription());
    }
}
