FROM cubejs/cube:v0.26

LABEL org.opencontainers.image.authors="historia_clinica@pladema.exa.unicen.edu.ar"

ENV CUBEJS_DB_HOST=postgresql
ENV CUBEJS_DB_NAME=hospitalDB
ENV CUBEJS_DB_USER=postgres
ENV CUBEJS_DB_PASS=Local123
ENV CUBEJS_WEB_SOCKETS=true
ENV CUBEJS_DEV_MODE=false
ENV CUBEJS_CACHE_AND_QUEUE_DRIVER=memory
ENV CUBEJS_DB_TYPE=postgres
ENV CUBEJS_API_SECRET=a36e649cedd441107c7531ec2da8415e592f19e9f93c250f001e9c40a660694892a3f78ccd8c027ae8504453ba25ebd0de47467a984bf98176c3197a9ffd685d
ENV CUBEJS_JWT_ALGS=HS512

COPY schema/ /cube/conf/schema/
COPY cube.js /cube/conf/
