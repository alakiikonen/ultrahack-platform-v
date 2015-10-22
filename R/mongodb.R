library(rmongodb)
library(doParallel)

#Connect to mongodb
mongo <- mongo.create(host="127.0.0.1:27017")

#Returns TRUE if connection was succesfull
mongo.is.connected(mongo)

#Database
db <- "local"

#Check current databases
collections <- mongo.get.database.collections(mongo, db)

#Select collection where products are stored
collection <- collections[3]

#Collection where we insert the model data
insert_collection <- paste(db, "models", sep=".")

#Get all the values to array for given key
Ids <- mongo.get.values(mongo, collection , key = "ProductId1234")




######INSERTING DATA###############
HandleOneProduct <- function(productId) {
  #Query collection by product ID
  products <- mongo.find.all(mongo, collection, query = list(ProductId1234 = productId))
 
  #Create dataframe from the list of lists
  df <- do.call(rbind.data.frame, products)
  colnames(df)[3] <- "t"
  
  #Create time vector
  time <- as.integer(seq.int(from = min(df$t), to = (max(df$t)+120), length.out=20))
  
  #Fit orthogonal polynomial model of order 3
  fit <- lm(stock ~ poly(t,3), data=df)
  
  #Create value vector
  forecast <- predict(fit, newdata=data.frame(t=time))

  object <- list(ProductId = productId,
                 timestamp = max(df$t),
                 model = list(x = time,
                              y = forecast))
  
  #MongoUpsert
  mongo.update(mongo, insert_collection, criteria = list(ProductId=productId), object, mongo.update.upsert)
} 

#Write model parameters to all products parallel
#registerDoParallel(cores=2)

#foreach(id=Ids) %dopar% HandleOneProduct(id)

foreach(id=Ids) %do% HandleOneProduct(id)
#Disconnect
mongo.disconnect(mongo)
##################################
