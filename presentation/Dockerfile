FROM busybox:1.36.1-musl

WORKDIR /site

COPY . /site

EXPOSE 8081

CMD ["httpd", "-f", "-p", "8081", "-h", "/site"]
