INSERT INTO bgv.platform_config (
    id,
    platform_brand_name,
    platform_legal_name,
    platform_logo_url,
    support_email,
    default_from_name,
    default_from_email,
    website_url,
    updated_at
)
VALUES (
    1,
    'Mayuktha',
    'Mayuktha Technologies Pvt Ltd',
    'https://cdn.Mayuktha.com/platform/logo.png',
    'support@Mayuktha.com',
    'Mayuktha Team',
    'no-reply@Mayuktha.com',
    'https://Mayuktha.com',
     NOW()
)
ON CONFLICT (id) DO NOTHING;   -- PostgreSQL safe
