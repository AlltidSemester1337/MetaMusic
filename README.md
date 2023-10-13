# Meta Music Service

API for inserting, updating and retrieving information about artists and their tracks.

# API features / Usage

##    * Add track to artist catalogue

**PUT /api/v1/tracks/add**

Example request, response and curl can be found in

    /target/generated-snippets/addTrack

Upon building project and executing integration tests

Also enabled for the following actuator endpoints:

    /actuator/httpexchanges

# Local run prerequisites

Set up and have postgresql server running, define proper values for the following env variables:

SPRING_DATASOURCE_URL

SPRING_DATASOURCE_USERNAME

SPRING_DATASOURCE_PASSWORD

# Run from commandline

    mvn clean install
    mvn spring-boot:run -Dspring.datasource.url=${SPRING_DATASOURCE_URL} \
    -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
    -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# Run using Docker

    docker build .
    docker run -e SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL} \
    -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} \
    -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
    -p 8080:8080 <image_id>