Set-Location -Path $PSScriptRoot

# start zookeeper
$zookeeperConfigPath = $PSScriptRoot + '/kafka/config/zookeeper.properties'
Start-Process kafka/bin/windows/zookeeper-server-start.bat $zookeeperConfigPath
Start-Sleep -s 5

# start kafka on one node 
$kafkaConfigPath = $PSScriptRoot + '/kafka/config/server.properties'
Start-Process kafka/bin/windows/kafka-server-start.bat $kafkaConfigPath
Start-Sleep -s 5

# start kafka-manager
cd kafka-manager
Start-Process activator '"run 9001"'
cd ..
Start-Sleep -s 1

# create topic
#kafka/bin/windows/kafka-topics.bat '--create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic data1'

# start kafka console producer
#Start-Process kafka/bin/windows/kafka-console-producer.bat '--broker-list localhost:9092 --topic sensor1'

# start kafka console consumer
#Start-Process kafka/bin/windows/kafka-console-consumer.bat '--zookeeper localhost:2181 --topic sensor1'
