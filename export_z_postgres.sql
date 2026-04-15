psql -p 5434 -U postgres -d testy -t -A -c "
SELECT jsonb_build_object(
    'alias', pk.alias,
    'email', pk.email,
    'fingerprint', pk.fingerprint,
    'public_key_pem', pk.public_key_pem,
    'created_at', pk.created_at,
    'expires_at', pk.expires_at,
    'import_logs', COALESCE(
        (SELECT jsonb_agg(jsonb_build_object(
            'attempt_timestamp', kil.attempt_timestamp,
            'success', kil.success,
            'error_message', kil.error_message,
            'attempted_alias', kil.attempted_alias
        ))
         FROM public.key_import_logs kil
         WHERE kil.public_key_id = pk.id),
        '[]'::jsonb
    )
)
FROM public.public_keys pk;" > export.json