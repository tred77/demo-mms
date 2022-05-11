### How to run 
This application is a spring boot application which is using H2 mem database to store data. There are some initial data which will be imported into the database at the start time.
#### Build 
```
mvn install 
```

#### Run
```
mvn spring-boot:run
```

#### Access
```
http://localhost:8080/swagger-ui/index.html
```

### Test Data

There are two xslx files in the project which contain initial dataset. 
To load data and keep track of database changes I use liquibase. 
The mentioned xslx files are processed with the help of a small tool developed for this purpose. 
You could find it in the test files under the tools folder. 
This tool reads the xslx files and create DML sql files runnable over any relational database.
In the products xlsx file there were some rows referring to undefined categories in the Category xslx file.
For those unknown categories, a category record with name set to "Unknown" is created to avoid data inconsistency. For sure developed tool would be useful for test purpose and if this sort of data should be imported into the system, a data digestion pipeline is required. To that end, Spring batch could be used.
