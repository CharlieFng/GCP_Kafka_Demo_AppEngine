# local start app
 mvn appengine:run

# deploy to cloud
 mvn appengine:deploy

mvn appengine:run -Dspring.profiles.active=local
mvn appengine:run -Dspring.profiles.active=dev


mvn appengine:deploy -Dspring.profiles.active=local
mvn package appengine:deploy -Dspring.profiles.active=dev