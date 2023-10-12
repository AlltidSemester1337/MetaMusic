# Meta Music Service

API for inserting and retrieving information about artists and their tracks.

# API features / Usage

##   * Add track to artist catalogue

**PUT /api/v1/tracks/add**

Example request payload:

      {
        "title": "Gold Dust Woman",
        "artist": "Fleetwood Mac",
        "genre": "Rock",
        "duration": "4:55",
        "releaseDate": "1977-02-04"
      }

Example success response:

      {
      "updatedCatalogueLink": "/api/v1/artists/tracks?artistName=Fleetwood+Mac"
      }

Example curl:

      curl -X PUT -H "Content-Type: application/json" -d '{
      "title": "Gold Dust Woman",
      "artist": "Fleetwood Mac",
      "genre": "Rock",
      "duration": "4:55",
      "releaseDate": "1977-02-04"
      }
      ' "http://localhost:8080/api/v1/tracks/add"

# Local run prerequisites

TODO

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