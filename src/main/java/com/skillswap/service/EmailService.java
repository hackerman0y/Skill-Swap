package com.skillswap.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ── Sends email asynchronously so it never slows down the app ──
    @Async
    public void sendEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = send as HTML
            helper.setFrom("noreply.skillswap@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Email sent to: " + toEmail);

        } catch (Exception e) {
            // Never crash the app if email fails — just log it
            System.err.println("❌ Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════
    // EMAIL TEMPLATE 1: Swap Request Received
    // Sent to the RECEIVER when someone sends them a swap request
    // ══════════════════════════════════════════════
    public void sendSwapRequestEmail(String toEmail, String receiverName,
                                     String senderName, String offeredSkill,
                                     String wantedSkill) {
        String subject = "🔄 New Swap Request on SkillSwap!";

        String body = "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='" +
                "margin:0;padding:0;background:#f5f7fb;font-family:Segoe UI,sans-serif;'>" +

                // Container
                "<div style='max-width:560px;margin:40px auto;background:#ffffff;" +
                "border-radius:20px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);'>" +

                // Header
                "<div style='background:linear-gradient(135deg,#6c63ff,#8b5cf6);" +
                "padding:32px 24px;text-align:center;'>" +
                "<h1 style='color:white;margin:0;font-size:1.6rem;letter-spacing:-0.5px;'>🔄 SkillSwap</h1>" +
                "<p style='color:rgba(255,255,255,0.85);margin:6px 0 0;font-size:0.9rem;'>Skill Exchange Platform</p>" +
                "</div>" +

                // Body
                "<div style='padding:32px 28px;'>" +
                "<h2 style='color:#1f2937;font-size:1.2rem;margin:0 0 8px;'>Hey " + receiverName + "! 👋</h2>" +
                "<p style='color:#6b7280;font-size:0.92rem;line-height:1.6;margin:0 0 24px;'>" +
                "You have received a new skill swap request on SkillSwap!</p>" +

                // Swap details card
                "<div style='background:#f5f7fb;border-radius:14px;padding:20px 24px;margin-bottom:24px;" +
                "border-left:4px solid #6c63ff;'>" +
                "<p style='margin:0 0 12px;font-size:0.85rem;color:#6b7280;font-weight:600;" +
                "text-transform:uppercase;letter-spacing:0.5px;'>Swap Details</p>" +

                "<div style='display:flex;align-items:center;gap:12px;margin-bottom:10px;'>" +
                "<span style='background:#6c63ff;color:white;border-radius:8px;padding:4px 12px;" +
                "font-size:0.8rem;font-weight:700;'>FROM</span>" +
                "<span style='color:#1f2937;font-weight:600;font-size:0.95rem;'>" + senderName + "</span>" +
                "</div>" +

                "<div style='display:flex;gap:10px;margin-top:14px;flex-wrap:wrap;'>" +
                "<div style='flex:1;min-width:120px;background:white;border-radius:10px;padding:12px;" +
                "border:1px solid #e5e7eb;text-align:center;'>" +
                "<div style='font-size:1.4rem;margin-bottom:4px;'>🎁</div>" +
                "<div style='font-size:0.7rem;color:#6b7280;font-weight:600;margin-bottom:4px;'>OFFERING</div>" +
                "<div style='color:#1f2937;font-weight:700;font-size:0.88rem;'>" + offeredSkill + "</div>" +
                "</div>" +

                "<div style='display:flex;align-items:center;font-size:1.2rem;'>⇄</div>" +

                "<div style='flex:1;min-width:120px;background:white;border-radius:10px;padding:12px;" +
                "border:1px solid #e5e7eb;text-align:center;'>" +
                "<div style='font-size:1.4rem;margin-bottom:4px;'>🎯</div>" +
                "<div style='font-size:0.7rem;color:#6b7280;font-weight:600;margin-bottom:4px;'>WANTS</div>" +
                "<div style='color:#1f2937;font-weight:700;font-size:0.88rem;'>" + wantedSkill + "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +

                // CTA Button
                "<div style='text-align:center;margin-bottom:24px;'>" +
                "<a href='http://localhost:8080/index.html' style='display:inline-block;" +
                "background:linear-gradient(135deg,#6c63ff,#8b5cf6);color:white;text-decoration:none;" +
                "padding:13px 32px;border-radius:25px;font-weight:700;font-size:0.92rem;" +
                "box-shadow:0 4px 14px rgba(108,99,255,0.4);'>View Request on SkillSwap →</a>" +
                "</div>" +

                "<p style='color:#9ca3af;font-size:0.78rem;text-align:center;margin:0;'>" +
                "You received this email because you have an account on SkillSwap.<br>" +
                "© 2026 SkillSwap — Learn & Teach Skills Easily 🚀</p>" +
                "</div></div></body></html>";

        sendEmail(toEmail, subject, body);
    }

    // ══════════════════════════════════════════════
    // EMAIL TEMPLATE 2: Store Purchase Confirmation
    // Sent to the BUYER when they successfully buy an item
    // ══════════════════════════════════════════════
    public void sendPurchaseConfirmationEmail(String toEmail, String username,
                                              String itemName, String itemEmoji,
                                              String itemCategory, int price,
                                              int remainingBalance) {
        String subject = "🛍️ Purchase Confirmed - " + itemName;

        String body = "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='" +
                "margin:0;padding:0;background:#f5f7fb;font-family:Segoe UI,sans-serif;'>" +

                // Container
                "<div style='max-width:560px;margin:40px auto;background:#ffffff;" +
                "border-radius:20px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);'>" +

                // Header
                "<div style='background:linear-gradient(135deg,#f59e0b,#d97706);" +
                "padding:32px 24px;text-align:center;'>" +
                "<h1 style='color:white;margin:0;font-size:1.6rem;'>🛍️ Purchase Confirmed!</h1>" +
                "<p style='color:rgba(255,255,255,0.85);margin:6px 0 0;font-size:0.9rem;'>SkillSwap Store</p>" +
                "</div>" +

                // Body
                "<div style='padding:32px 28px;'>" +
                "<h2 style='color:#1f2937;font-size:1.2rem;margin:0 0 8px;'>Hey " + username + "! 🎉</h2>" +
                "<p style='color:#6b7280;font-size:0.92rem;line-height:1.6;margin:0 0 24px;'>" +
                "Your purchase was successful! Here's your receipt:</p>" +

                // Item card
                "<div style='background:#fffbeb;border-radius:14px;padding:24px;" +
                "border:2px solid #fde68a;margin-bottom:24px;text-align:center;'>" +
                "<div style='font-size:4rem;margin-bottom:12px;'>" + itemEmoji + "</div>" +
                "<div style='font-size:1.2rem;font-weight:800;color:#1f2937;margin-bottom:6px;'>" + itemName + "</div>" +
                "<div style='display:inline-block;background:#fef3c7;color:#d97706;padding:3px 14px;" +
                "border-radius:20px;font-size:0.75rem;font-weight:700;text-transform:uppercase;" +
                "letter-spacing:0.5px;margin-bottom:16px;'>" + itemCategory + "</div>" +

                // Price row
                "<div style='display:flex;justify-content:center;gap:32px;margin-top:8px;'>" +
                "<div style='text-align:center;'>" +
                "<div style='font-size:0.72rem;color:#9ca3af;font-weight:600;text-transform:uppercase;" +
                "letter-spacing:0.5px;margin-bottom:4px;'>PAID</div>" +
                "<div style='font-size:1.3rem;font-weight:800;color:#d97706;'>🪙 " + price + "</div>" +
                "</div>" +
                "<div style='width:1px;background:#fde68a;'></div>" +
                "<div style='text-align:center;'>" +
                "<div style='font-size:0.72rem;color:#9ca3af;font-weight:600;text-transform:uppercase;" +
                "letter-spacing:0.5px;margin-bottom:4px;'>REMAINING</div>" +
                "<div style='font-size:1.3rem;font-weight:800;color:#6c63ff;'>🪙 " + remainingBalance + "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +

                // Success message
                "<div style='background:#f0fdf4;border-radius:12px;padding:16px;margin-bottom:24px;" +
                "border-left:4px solid #10b981;'>" +
                "<p style='margin:0;color:#065f46;font-size:0.88rem;font-weight:600;'>" +
                "✅ " + itemName + " has been added to your inventory!</p>" +
                "<p style='margin:6px 0 0;color:#6b7280;font-size:0.82rem;'>" +
                "You can find it in your profile inventory at any time.</p>" +
                "</div>" +

                // CTA
                "<div style='text-align:center;margin-bottom:24px;'>" +
                "<a href='http://localhost:8080/Store.html' style='display:inline-block;" +
                "background:linear-gradient(135deg,#f59e0b,#d97706);color:white;text-decoration:none;" +
                "padding:13px 32px;border-radius:25px;font-weight:700;font-size:0.92rem;" +
                "box-shadow:0 4px 14px rgba(245,158,11,0.4);'>Visit Store Again →</a>" +
                "</div>" +

                "<p style='color:#9ca3af;font-size:0.78rem;text-align:center;margin:0;'>" +
                "Thank you for your purchase!<br>" +
                "© 2026 SkillSwap — Learn & Teach Skills Easily 🚀</p>" +
                "</div></div></body></html>";

        sendEmail(toEmail, subject, body);
    }
}