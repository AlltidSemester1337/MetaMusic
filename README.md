# Meta Music Service

API for inserting and retrieving information about artists and their tracks.

# API features / Usage

##  * Add track to artist catalogue

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
mvn spring-boot:run

# Run using Docker

docker build .
docker run -p 8080:8080 <image_id>