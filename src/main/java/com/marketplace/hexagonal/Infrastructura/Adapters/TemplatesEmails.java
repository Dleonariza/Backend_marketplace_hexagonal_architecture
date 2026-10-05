package com.marketplace.hexagonal.Infrastructura.Adapters;

public final class TemplatesEmails {

    private TemplatesEmails() {
    }

    public static String welcomeEmail(String userName, String marketplaceUrl) {
        return WELCOME.formatted(userName, marketplaceUrl);
    }

    public static String otpVerificationEmail(String otpCode, String marketplaceUrl) {
        return OTP_VERIFICATION.formatted(otpCode, marketplaceUrl);
    }

    public static String passwordRecoveryEmail(String resetUrl) {
        return PASSWORD_RECOVERY.formatted(resetUrl);
    }

    public static String sellerAcceptedEmail(String sellerStudioUrl) {
        return SELLER_ACCEPTED.formatted(sellerStudioUrl);
    }

    public static String shippingConfirmationEmail(String trackingUrl, String orderNumber, String itemsHtml,
            String recipientName, String addressLine1, String addressLine2, String country,
            String deliveryDate, String deliveryTime) {
        return SHIPPING_CONFIRMATION.formatted(trackingUrl, orderNumber, itemsHtml,
                recipientName, addressLine1, addressLine2, country, deliveryDate, deliveryTime);
    }

    public static String shipmentItem(String imageUrl, String imageAlt, String productName,
            String detailsLine, int quantity) {
        return SHIPMENT_ITEM.formatted(imageUrl, imageAlt, productName, detailsLine, quantity);
    }

    public static String orderConfirmationEmail(String customerName, String orderNumber, String orderDate,
            String viewOrderUrl, String itemsHtml, String subtotal, String shipping, String total) {
        return ORDER_CONFIRMATION.formatted(customerName, orderNumber, orderDate,
                viewOrderUrl, itemsHtml, subtotal, shipping, total);
    }

    public static String orderSummaryItem(String imageUrl, String productName, String detailsLine,
            String price, int quantity) {
        return ORDER_SUMMARY_ITEM.formatted(imageUrl, productName, detailsLine, price, quantity);
    }

    public static String reviewRequestEmail(String productImageUrl, String productName, String reviewUrl) {
        return REVIEW_REQUEST.formatted(productImageUrl, productName, reviewUrl);
    }

    private static final String WELCOME = """
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>Welcome to LUXE</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
</head>
<body style="margin:0;padding:0;background-color:#F8FAFC;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F8FAFC">
<tr><td align="center" style="padding:24px 20px;">
<table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px;max-width:600px;background-color:#ffffff;border:1px solid #f2f3ff;border-radius:12px;">
<tr><td align="center" bgcolor="#faf8ff" style="padding:32px 20px;border-bottom:1px solid #f2f3ff;border-radius:12px 12px 0 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:32px;line-height:1.2;font-weight:700;letter-spacing:-0.01em;color:#004ac6;">LUXO</div>
</td></tr>
<tr><td>
<img src="https://lh3.googleusercontent.com/aida-public/AB6AXuDD7ktm26jJasx5QKNJ7a-_PRwAKNVGcyYJocXDSkuvK0sHycJp5XkKYaCWx-dHD_fcEKKf0SkuHnF-SQLZpaHtOha4zMftbB-lpm55NdADHDlRZnVmEmwZg4A-oAxaxPWJ-GeXak4FCmnIF_2D7BhZQGjOLnlh4bRQ0Cde3_WgMyGcGdE-XzBDL3OBKVugCccs6ZZse7GilaQKcX-eTJbDnxpJfhzBSuSLaobRUfIYC0ByizI6aXRgk5V-w8Ww9Ol9-BJXn0gidJVA" alt="Luxury Lifestyle Hero" width="600" style="display:block;width:100%%;height:auto;border:0;"/>
</td></tr>
<tr><td align="center" style="padding:48px 32px;">
<h2 style="margin:0 0 16px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:28px;line-height:1.2;font-weight:600;letter-spacing:-0.01em;color:#131b2e;">Welcome to the World of LUXO</h2>
<p style="margin:0 0 32px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Hi %s, we're thrilled to have you join our community of connoisseurs. Discover curated collections, exclusive sellers, and premium experiences designed just for you.</p>
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#2563eb" style="border-radius:9999px;">
<a href="%s" style="display:inline-block;padding:14px 32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;text-decoration:none;border-radius:9999px;">Explore the Marketplace</a>
</td></tr>
</table>
</td></tr>
<tr><td bgcolor="#faf8ff" style="padding:48px 20px;">
<h3 style="margin:0 0 32px 0;text-align:center;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;color:#131b2e;">Getting Started</h3>
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0">
<tr>
<td width="33%%" valign="top" style="padding:0 4px;"><table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#ffffff;border:1px solid #f2f3ff;border-radius:8px;">
<tr><td align="center" style="padding:16px 12px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#F8FAFC" width="48" height="48" style="width:48px;height:48px;border-radius:9999px;font-size:22px;line-height:48px;">&#128100;</td></tr>
</table>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;margin:8px 0 4px 0;">Complete Profile</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Set up your preferences for tailored recommendations.</div>
</td></tr>
</table></td>
<td width="33%%" valign="top" style="padding:0 4px;"><table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#ffffff;border:1px solid #f2f3ff;border-radius:8px;">
<tr><td align="center" style="padding:16px 12px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#F8FAFC" width="48" height="48" style="width:48px;height:48px;border-radius:9999px;font-size:22px;line-height:48px;">&#10084;</td></tr>
</table>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;margin:8px 0 4px 0;">Follow Stores</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Keep track of your favorite premium sellers.</div>
</td></tr>
</table></td>
<td width="33%%" valign="top" style="padding:0 4px;"><table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#ffffff;border:1px solid #f2f3ff;border-radius:8px;">
<tr><td align="center" style="padding:16px 12px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#F8FAFC" width="48" height="48" style="width:48px;height:48px;border-radius:9999px;font-size:22px;line-height:48px;">&#129517;</td></tr>
</table>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;margin:8px 0 4px 0;">First Discovery</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Browse our exclusive catalog and make it yours.</div>
</td></tr>
</table></td>
</tr>
</table>
</td></tr>
<tr><td align="center" bgcolor="#F8FAFC" style="padding:32px 20px;border-radius:0 0 12px 12px;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;color:#004ac6;margin-bottom:16px;">LUXO</div>
<p style="margin:0 0 8px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&copy; 2024 LUXO Marketplace. Todos los derechos reservados.</p>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;"><a href="#" style="color:#64748B;text-decoration:none;">Privacidad</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Términos</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Soporte</a></p>
<p style="margin:24px 0 0 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#c3c6d7;font-weight:400;">You received this email because you registered on our platform. <a href="#" style="color:#c3c6d7;text-decoration:underline;">Unsubscribe</a></p>
</td></tr>
</table>
</td></tr>
</table>
</body>
</html>
""";

    private static final String OTP_VERIFICATION = """
<!DOCTYPE html>
<html lang="es" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>Lujo - Verifica tu cuenta</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
</head>
<body style="margin:0;padding:0;background-color:#F8FAFC;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F8FAFC">
<tr><td align="center" style="padding:24px 20px;">
<table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px;max-width:600px;background-color:#ffffff;border:1px solid #f2f3ff;border-radius:12px;">
<tr><td align="center" bgcolor="#faf8ff" style="padding:32px 20px;border-bottom:1px solid #f2f3ff;border-radius:12px 12px 0 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:32px;line-height:1.2;font-weight:700;letter-spacing:-0.01em;color:#004ac6;">Lujo</div>
</td></tr>
<tr><td align="center" style="padding:48px 32px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#eaedff" width="64" height="64" style="width:64px;height:64px;border-radius:9999px;font-size:29px;line-height:64px;">&#9993;</td></tr>
</table>
<h2 style="margin:24px 0 16px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:28px;line-height:1.2;font-weight:600;letter-spacing:-0.01em;color:#131b2e;">Verifica tu cuenta</h2>
<p style="margin:0 0 32px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Gracias por registrarte en Lujo Marketplace. Para completar la configuración de tu cuenta y garantizar tu seguridad, por favor ingresa el siguiente código de verificación en la aplicación.</p>
<table role="presentation" width="320" cellpadding="0" cellspacing="0" border="0" align="center" style="width:320px;max-width:100%%;background-color:#F8FAFC;border:1px solid #eaedff;border-radius:8px;">
<tr><td align="center" style="padding:24px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:36px;line-height:1.2;font-weight:700;letter-spacing:12px;color:#004ac6;">%s</td></tr>
</table>
<p style="margin:8px 0 32px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&#128336; Este código expirará en 10 minutos.</p>
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#2563eb" style="border-radius:9999px;">
<a href="%s" style="display:inline-block;padding:14px 32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;text-decoration:none;border-radius:9999px;">Ir al Marketplace</a>
</td></tr>
</table>
<p style="margin:24px 0 0 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Si no solicitaste esta verificación, puedes ignorar este correo electrónico de forma segura o contactar a nuestro equipo de soporte.</p>
</td></tr>
<tr><td align="center" bgcolor="#faf8ff" style="padding:24px 20px;border-top:1px solid #f2f3ff;border-radius:0 0 12px 12px;">
<p style="margin:0 0 16px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;"><a href="#" style="color:#64748B;text-decoration:none;">Privacidad</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Términos</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Soporte</a></p>
<p style="margin:0 0 8px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&copy; 2024 Lujo Marketplace. Todos los derechos reservados.</p>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#c3c6d7;font-weight:400;">Este es un correo electrónico automatizado, por favor no respondas a este mensaje.</p>
</td></tr>
</table>
</td></tr>
</table>
</body>
</html>
""";

    private static final String PASSWORD_RECOVERY = """
<!DOCTYPE html>
<html lang="es" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>Restablecer contraseña - Lujo</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
</head>
<body style="margin:0;padding:0;background-color:#F8FAFC;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F8FAFC">
<tr><td align="center" style="padding:24px 20px;">
<table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px;max-width:600px;background-color:#ffffff;border:1px solid #f2f3ff;border-radius:12px;">
<tr><td align="center" bgcolor="#faf8ff" style="padding:32px 20px;border-bottom:1px solid #f2f3ff;border-radius:12px 12px 0 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:32px;line-height:1.2;font-weight:700;letter-spacing:-0.01em;color:#004ac6;">LUJO</div>
</td></tr>
<tr><td align="center" style="padding:48px 32px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#eaedff" width="64" height="64" style="width:64px;height:64px;border-radius:9999px;font-size:29px;line-height:64px;">&#128274;</td></tr>
</table>
<h2 style="margin:24px 0 16px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:28px;line-height:1.2;font-weight:600;letter-spacing:-0.01em;color:#131b2e;">Restablecer contraseña</h2>
<p style="margin:0 0 32px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Lujo. Haz clic en el botón de abajo para configurar una nueva contraseña.</p>
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#2563eb" style="border-radius:9999px;">
<a href="%s" style="display:inline-block;padding:14px 32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;text-decoration:none;border-radius:9999px;">Cambiar contraseña</a>
</td></tr>
</table>
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-top:32px;border-top:1px solid #eaedff;">
<tr><td align="center" style="padding-top:24px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Si no solicitaste este cambio, puedes ignorar este correo de forma segura. Tu cuenta permanece protegida.</td></tr>
</table>
</td></tr>
<tr><td align="center" bgcolor="#faf8ff" style="padding:24px 32px;border-top:1px solid #eaedff;border-radius:0 0 12px 12px;">
<p style="margin:0 0 8px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&copy; 2024 Lujo Marketplace. Todos los derechos reservados.</p>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;"><a href="#" style="color:#004ac6;text-decoration:none;">Privacidad</a> <span style="color:#64748B;">&bull;</span> <a href="#" style="color:#004ac6;text-decoration:none;">Términos</a> <span style="color:#64748B;">&bull;</span> <a href="#" style="color:#004ac6;text-decoration:none;">Soporte</a></p>
</td></tr>
</table>
</td></tr>
</table>
</body>
</html>
""";

    private static final String SELLER_ACCEPTED = """
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>Welcome to the Seller Community - LUXE 2026</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
</head>
<body style="margin:0;padding:0;background-color:#F8FAFC;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F8FAFC">
<tr><td align="center" style="padding:24px 20px;">
<table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px;max-width:600px;background-color:#ffffff;border:1px solid #f2f3ff;border-radius:12px;">
<tr><td align="center" bgcolor="#faf8ff" style="padding:32px 20px;border-bottom:1px solid #f2f3ff;border-radius:12px 12px 0 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:32px;line-height:1.2;font-weight:700;letter-spacing:-0.01em;color:#004ac6;">LUXE</div>
</td></tr>
<tr><td>
<img src="https://lh3.googleusercontent.com/aida-public/AB6AXuDdRXgNDwdmgG6MbyYTq5TjY9LnkSDGw9y_BgAHiZ7mD0NnEWcxysIqAF3xON7JsbkNfMhYyXA7kAUkfyTf83_uyT1lKeFcnIVLp5TUCEo2Lbd3pveTHLThUe6a23shGgg0dOiGAzeaY7_UqgdpjfBJ4fLugYHBnI-FiDGR-wFFjtr-oOahcDG7e8Ph1MYutZRzXMYHzyG_J1e-s3hOvZiXfot_W2VS1sdLcjkIRw-cHtTrBHiXiYPW9iwxJQPwLv0utosbgehqIjn9" alt="Premium seller studio" width="600" style="display:block;width:100%%;height:auto;border:0;"/>
</td></tr>
<tr><td bgcolor="#faf8ff" style="padding:24px 24px 16px 24px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0">
<tr><td bgcolor="#2563eb" style="border-radius:9999px;padding:4px 14px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;">&#10003; Approved</td></tr>
</table>
<h2 style="margin:16px 0 0 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:28px;line-height:1.2;font-weight:600;letter-spacing:-0.01em;color:#131b2e;">Welcome to the Seller Community</h2>
</td></tr>
<tr><td style="padding:32px 24px;">
<p style="margin:0 0 24px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#434655;">Congratulations! We are thrilled to inform you that your application to become a seller on the LUXE 2026 platform has been approved. You are now part of an exclusive network of premium vendors.</p>
<p style="margin:0 0 24px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#434655;">Your journey towards reaching a discerning audience begins now. To help you get started, we've outlined the essential first steps to set up your digital storefront for success.</p>
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0">
<tr>
<td width="33%%" valign="top" style="padding:0 4px;"><table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F8FAFC;border:1px solid #c3c6d7;border-radius:8px;">
<tr><td style="padding:16px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0">
<tr><td align="center" bgcolor="#eaedff" width="40" height="40" style="width:40px;height:40px;border-radius:9999px;font-size:18px;line-height:40px;">&#128230;</td></tr>
</table>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;font-size:18px;color:#131b2e;margin:8px 0 4px 0;">List products</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Add your premium inventory with high-quality imagery.</div>
</td></tr>
</table></td>
<td width="33%%" valign="top" style="padding:0 4px;"><table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F8FAFC;border:1px solid #c3c6d7;border-radius:8px;">
<tr><td style="padding:16px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0">
<tr><td align="center" bgcolor="#eaedff" width="40" height="40" style="width:40px;height:40px;border-radius:9999px;font-size:18px;line-height:40px;">&#128666;</td></tr>
</table>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;font-size:18px;color:#131b2e;margin:8px 0 4px 0;">Configure shipping</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Set up your delivery zones and fulfillment preferences.</div>
</td></tr>
</table></td>
<td width="33%%" valign="top" style="padding:0 4px;"><table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F8FAFC;border:1px solid #c3c6d7;border-radius:8px;">
<tr><td style="padding:16px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0">
<tr><td align="center" bgcolor="#eaedff" width="40" height="40" style="width:40px;height:40px;border-radius:9999px;font-size:18px;line-height:40px;">&#127980;</td></tr>
</table>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;font-size:18px;color:#131b2e;margin:8px 0 4px 0;">Complete store profile</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Define your brand identity and seller policies.</div>
</td></tr>
</table></td>
</tr>
</table>
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-top:32px;">
<tr><td align="center"><table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#2563eb" style="border-radius:9999px;">
<a href="%s" style="display:inline-block;padding:14px 32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;text-decoration:none;border-radius:9999px;">Go to Seller Studio &#8594;</a>
</td></tr>
</table></td></tr>
</table>
</td></tr>
<tr><td align="center" bgcolor="#faf8ff" style="padding:24px 20px;border-top:1px solid #dae2fd;border-radius:0 0 12px 12px;">
<p style="margin:0 0 8px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;"><a href="#" style="color:#64748B;text-decoration:none;">Privacidad</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Términos</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Soporte</a></p>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&copy; 2024 Lujo Marketplace. Todos los derechos reservados.</p>
</td></tr>
</table>
</td></tr>
</table>
</body>
</html>
""";

    private static final String SHIPPING_CONFIRMATION = """
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>Order Shipped - LUXE 2026</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
</head>
<body style="margin:0;padding:0;background-color:#F8FAFC;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F8FAFC">
<tr><td align="center" style="padding:24px 20px;">
<table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px;max-width:600px;background-color:#ffffff;border:1px solid #f2f3ff;border-radius:12px;">
<tr><td align="center" bgcolor="#faf8ff" style="padding:32px 20px;border-bottom:1px solid #f2f3ff;border-radius:12px 12px 0 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:32px;line-height:1.2;font-weight:700;letter-spacing:-0.01em;color:#004ac6;">Lujo</div>
</td></tr>
<tr><td align="center" style="padding:32px 24px 0 24px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#eaedff" width="64" height="64" style="width:64px;height:64px;border-radius:9999px;font-size:29px;line-height:64px;">&#128666;</td></tr>
</table>
<h2 style="margin:24px 0 16px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:28px;line-height:1.2;font-weight:600;letter-spacing:-0.01em;color:#131b2e;">Your order is on its way</h2>
<p style="margin:0 0 24px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Great news! Your recent order from Lujo has shipped and is currently in transit to your destination.</p>
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#2563eb" style="border-radius:9999px;">
<a href="%s" style="display:inline-block;padding:14px 32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;text-decoration:none;border-radius:9999px;">Track Shipment</a>
</td></tr>
</table>
</td></tr>
<tr><td style="padding:32px 24px 0 24px;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="border-top:1px solid #F8FAFC;">
<tr>
<td style="padding-top:32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;color:#131b2e;">Order Details</td>
<td align="right" style="padding-top:32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#64748B;">%s</td>
</tr>
</table>
</td></tr>
<tr><td style="padding:16px 24px 0 24px;">
%s
</td></tr>
<tr><td style="padding:24px 24px 0 24px;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F8FAFC;border:1px solid #F1F5F9;border-radius:12px;">
<tr>
<td width="50%%" valign="top" style="padding:16px;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;font-weight:600;margin-bottom:4px;">&#127968; Shipping Address</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;line-height:1.7;">%s<br/>%s<br/>%s<br/>%s</div>
</td>
<td width="50%%" valign="top" style="padding:16px;border-left:1px solid #dae2fd;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;font-weight:600;margin-bottom:4px;">&#128336; Estimated Delivery</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;line-height:1.7;">%s<br/>%s</div>
</td>
</tr>
</table>
</td></tr>
<tr><td align="center" style="padding:24px;">
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;">Need help with your order? <a href="#" style="color:#004ac6;font-weight:600;text-decoration:none;">Contact Support</a></p>
</td></tr>
<tr><td align="center" bgcolor="#faf8ff" style="padding:24px 20px;border-top:1px solid #F8FAFC;border-radius:0 0 12px 12px;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;color:#004ac6;margin-bottom:12px;">Lujo</div>
<p style="margin:0 0 12px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;"><a href="#" style="color:#64748B;text-decoration:none;">Privacidad</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Términos</a>&nbsp;&nbsp;&nbsp;<a href="#" style="color:#64748B;text-decoration:none;">Soporte</a></p>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&copy; 2024 Lujo Marketplace. Todos los derechos reservados.</p>
</td></tr>
</table>
</td></tr>
</table>
</body>
</html>
""";

    private static final String SHIPMENT_ITEM = """
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F8FAFC;border:1px solid #F1F5F9;border-radius:8px;margin:0 0 16px 0;">
<tr>
<td width="96" style="padding:16px;">
<img src="%s" alt="%s" width="80" height="80" style="display:block;width:80px;height:80px;border-radius:4px;border:0;"/>
</td>
<td valign="middle" style="padding:16px 16px 16px 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;font-weight:600;">%s</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;margin-top:4px;">%s</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;margin-top:8px;">Qty: %s</div>
</td>
</tr>
</table>
""";

    private static final String ORDER_CONFIRMATION = """
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>LUXE Email Templates</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
</head>
<body style="margin:0;padding:0;background-color:#F8FAFC;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F8FAFC">
<tr><td align="center" style="padding:24px 20px;">
<table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px;max-width:600px;background-color:#ffffff;border:1px solid #f2f3ff;border-radius:12px;">
<tr><td align="center" bgcolor="#F8FAFC" style="padding:32px 24px;border-bottom:1px solid #dae2fd;border-radius:12px 12px 0 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:32px;line-height:1.2;font-weight:700;letter-spacing:-0.01em;color:#004ac6;">LUXE</div>
<p style="margin:8px 0 0 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Order Confirmed</p>
</td></tr>
<tr><td align="center" style="padding:32px 32px 0 32px;">
<h2 style="margin:0 0 16px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;font-size:24px;color:#131b2e;">Thank you for your purchase.</h2>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Hi %s, we're getting your order ready to be shipped. We will notify you when it has been sent.</p>
</td></tr>
<tr><td style="padding:32px 32px 0 32px;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F8FAFC;border:1px solid #dae2fd;border-radius:8px;">
<tr>
<td valign="middle" style="padding:24px 0 24px 24px;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#64748B;">Order Number</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;font-weight:500;color:#131b2e;">%s</div>
</td>
<td valign="middle" style="padding:24px 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#64748B;">Order Date</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;font-weight:500;color:#131b2e;">%s</div>
</td>
<td valign="middle" align="right" style="padding:24px 24px 24px 0;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="right">
<tr><td align="center" bgcolor="#2563eb" style="border-radius:9999px;">
<a href="%s" style="display:inline-block;padding:8px 24px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;text-decoration:none;border-radius:9999px;">View Order</a>
</td></tr>
</table>
</td>
</tr>
</table>
</td></tr>
<tr><td style="padding:32px;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#64748B;text-transform:uppercase;letter-spacing:2px;margin-bottom:24px;">Order Summary</div>
%s
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-top:16px;">
<tr>
<td style="padding:4px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Subtotal</td>
<td align="right" style="padding:4px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">%s</td>
</tr>
<tr>
<td style="padding:4px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">Shipping</td>
<td align="right" style="padding:4px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">%s</td>
</tr>
<tr>
<td style="padding:12px 0 0 0;border-top:1px solid #dae2fd;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;">Total</td>
<td align="right" style="padding:12px 0 0 0;border-top:1px solid #dae2fd;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;">%s</td>
</tr>
</table>
</td></tr>
<tr><td align="center" bgcolor="#F8FAFC" style="padding:32px 24px;border-top:1px solid #dae2fd;border-radius:0 0 12px 12px;margin-top:32px;">
<p style="margin:0 0 8px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&copy; 2026 LUXE Global Marketplace. All rights reserved.</p>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;"><a href="#" style="color:#004ac6;text-decoration:none;">Contact Support</a> <span style="color:#dae2fd;">&bull;</span> <a href="#" style="color:#004ac6;text-decoration:none;">Privacy Policy</a></p>
</td></tr>
</table>
</td></tr>
</table>
</body>
</html>
""";

    private static final String ORDER_SUMMARY_ITEM = """
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="border-bottom:1px solid #dae2fd;">
<tr>
<td width="96" style="padding:0 16px 16px 0;">
<img src="%s" alt="Product Image" width="80" height="80" style="display:block;width:80px;height:80px;border-radius:4px;border:0;"/>
</td>
<td valign="middle" style="padding:0 0 16px 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;">%s</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;margin-top:4px;">%s</div>
</td>
<td valign="middle" align="right" style="padding:0 0 16px 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#131b2e;">%s</div>
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;font-weight:400;margin-top:4px;">Qty: %s</div>
</td>
</tr>
</table>
""";

    private static final String REVIEW_REQUEST = """
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>LUXE Email Templates</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
</head>
<body style="margin:0;padding:0;background-color:#F8FAFC;">
<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F8FAFC">
<tr><td align="center" style="padding:24px 20px;">
<table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px;max-width:600px;background-color:#ffffff;border:1px solid #f2f3ff;border-radius:12px;">
<tr><td align="center" bgcolor="#F8FAFC" style="padding:32px 24px;border-bottom:1px solid #dae2fd;border-radius:12px 12px 0 0;">
<div style="font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:32px;line-height:1.2;font-weight:700;letter-spacing:-0.01em;color:#004ac6;">LUXE</div>
</td></tr>
<tr><td align="center" style="padding:32px 32px 0 32px;">
<img src="%s" alt="Product Image" width="128" height="128" style="display:block;width:128px;height:128px;border-radius:9999px;border:4px solid #ffffff;"/>
</td></tr>
<tr><td align="center" style="padding:24px 32px 0 32px;">
<h2 style="margin:0 0 16px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:22px;line-height:1.3;font-weight:600;letter-spacing:0;font-size:24px;color:#131b2e;">How was your experience?</h2>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;line-height:1.6;font-weight:400;letter-spacing:0;color:#64748B;">It's been a few weeks since you received your <strong style="color:#131b2e;">%s</strong>. We'd love to hear your thoughts and help others make informed decisions.</p>
</td></tr>
<tr><td align="center" style="padding:24px 32px 0 32px;">
<div style="font-size:36px;line-height:1;letter-spacing:6px;"><span style="color:#F59E0B;">&#9733;&#9733;&#9733;&#9733;</span><span style="color:#dae2fd;">&#9734;</span></div>
</td></tr>
<tr><td align="center" style="padding:32px 32px 48px 32px;">
<table role="presentation" cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td align="center" bgcolor="#2563eb" style="border-radius:9999px;">
<a href="%s" style="display:inline-block;padding:14px 32px;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:14px;line-height:1.5;font-weight:500;letter-spacing:0.01em;color:#ffffff;text-decoration:none;border-radius:9999px;">Write a Review</a>
</td></tr>
</table>
</td></tr>
<tr><td align="center" bgcolor="#F8FAFC" style="padding:32px 24px;border-top:1px solid #dae2fd;border-radius:0 0 12px 12px;">
<p style="margin:0 0 8px 0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;color:#64748B;">&copy; 2026 LUXE Global Marketplace. All rights reserved.</p>
<p style="margin:0;font-family:'Inter','Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.4;font-weight:500;letter-spacing:0.02em;"><a href="#" style="color:#64748B;text-decoration:none;">Unsubscribe</a></p>
</td></tr>
</table>
</td></tr>
</table>
</body>
</html>
""";
}
