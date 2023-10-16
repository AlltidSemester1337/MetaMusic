# Meta Music Service

API for inserting, updating and retrieving information about artists and their tracks.

# API features / Usage

##     * Add track to artist catalogue

**PUT /api/v1/artists/byname/<artistName>/tracks**

Example request, response and curl can be found in

    /target/generated-snippets/addTrack

##     * Edit artist name or add new alias

**PUT /api/v1/artists/byname/<artistName>**

Example request, response and curl can be found in

    /target/generated-snippets/editArtistName

##     * Fetch all tracks paginated for artist by name

**GET /api/v1/artists/byname/<artistName>/tracks**

Example request, response and curl can be found in

    /target/generated-snippets/fetchTracks

##     * Fetch artist of the day

**GET /api/v1/artists/artistOfTheDay**

Rotates on artists in a cyclical daily updated manner, starting and re-starting from last artist until first artist (lowest ID)

Example request, response and curl can be found in

    /target/generated-snippets/artistOfTheDay

All snippets generated upon building project and executing integration tests

Also enabled for the following actuator endpoints:

    /actuator/httpexchanges

# System design / architecture

![MetaMusic Design](MetaMusic.drawio.png?raw=true)

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

    mvn clean install
    docker build .
    docker run -e SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL} \
    -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} \
    -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
    -p 8080:8080 <image_id>