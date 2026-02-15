INSERT INTO public.platform_config (
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

INSERT INTO public.check_category(
    category_id, description, label, name, code, has_documents, is_active, price, sla_days, warning_threshold_days
)
VALUES
(1, 'Identity', 'Identity', 'IDENTITY', 'IDENTITY', true, true, 0, 0, 0),
(2, 'Education', 'Education', 'EDUCATION', 'EDUCATION', true, true, 0, 0, 0),
(3, 'Professional/Work Experience', 'Work Experience', 'WORK', 'WORK', true, true, 0, 0, 0),
(4, 'Other', 'Other', 'OTHER', 'OTHER', false, true, 0, 0, 0),
(5, 'Address', 'Address', 'ADDRESS', 'ADDRESS', true, true, 0, 0, 0),
(6, 'Court', 'Court', 'COURT', 'COURT', false, true, 0, 0, 0);
