root /usr/share/nginx/html;
index index.html;

# The corporate site uses only the public site API. Keep generic backend
# routes (including the management API) off the public website origin.
location ^~ /api/open/site/v1/ {
    set $site_backend backend;
    proxy_pass http://$site_backend:8080/open/site/v1/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $remote_addr;
    proxy_set_header X-Forwarded-Proto $scheme;
}

location ^~ /api/ {
    return 404;
}

location / {
    try_files $uri $uri/ /index.html;
}
