library(rmongodb)
library(jsonlite)

#Connect to mongodb
mongo <- mongo.create(host="127.0.0.1:27017")

#Returns TRUE if connection was succesfull
mongo.is.connected(mongo)

#Let's change the current directory
setwd("~/R/Scripts")

#Open data that would come from Cassandra DB
data <- read.csv("Data.csv", sep=",", header=TRUE)

#Creates BSON object from every row of the dataframe "data"
bsonObject <- lapply(split(data, 1:nrow(data)), function(data_row) mongo.bson.from.JSON(toJSON(data_row)))

#Check that class is correct (should be "list")
class(bsonObject)

#Check current databases
mongo.get.databases(mongo)

#Database
db <- "local"

#Create name for collection
collection <- paste(db, "test", sep=".")

#Insert data with the speficications
mongo.insert.batch(mongo, collection, bsonObject)


mongo.disconnect(mongo)
